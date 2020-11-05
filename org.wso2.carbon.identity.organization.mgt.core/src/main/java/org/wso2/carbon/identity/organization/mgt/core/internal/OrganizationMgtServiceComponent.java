/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.mgt.core.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManagerImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.CacheBackedOrganizationMgtDAO;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationAuthorizationDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.handler.OrganizationMgtAuditHandler;
import org.wso2.carbon.identity.organization.mgt.core.handler.OrganizationMgtValidationHandler;
import org.wso2.carbon.identity.organization.mgt.core.model.MetaUser;
import org.wso2.carbon.identity.organization.mgt.core.model.Metadata;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.util.HashMap;
import java.util.Map;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INITIALIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ID_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ID_DEFAULT_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_NAME_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_NAME_DEFAULT_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PRIMARY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.RDN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROOT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.SCIM2_USER_RESOURCE_BASE_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getLdapRootDn;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getUserIDFromUserName;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

/**
 * OSGI service component for organization management core bundle.
 */
@Component(name = "carbon.organization.mgt.component",
           immediate = true)
public class OrganizationMgtServiceComponent {

    private static final Log log = LogFactory.getLog(OrganizationMgtServiceComponent.class);

    /**
     * Register Organization Manager service in the OSGI context.
     *
     * @param componentContext
     */
    @Activate
    protected void activate(ComponentContext componentContext) {

        try {
            OrganizationMgtDataHolder.getInstance().setOrganizationMgtDao(new OrganizationMgtDaoImpl());
            OrganizationMgtDataHolder.getInstance().setOrganizationAuthDao(new OrganizationAuthorizationDaoImpl());
            OrganizationMgtDataHolder.getInstance().setCacheBackedOrganizationMgtDAO(
                    new CacheBackedOrganizationMgtDAO(OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao()));
            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(OrganizationManager.class.getName(), new OrganizationManagerImpl(), null);
            bundleContext.registerService(AbstractEventHandler.class.getName(), new OrganizationMgtAuditHandler(),
                    null);
            bundleContext.registerService(AbstractEventHandler.class.getName(), new OrganizationMgtValidationHandler(),
                    null);
            createRootIfNotExist();
            if (log.isDebugEnabled()) {
                log.debug("Organization Management component activated successfully.");
            }
        } catch (Throwable e) {
            log.error("Error while activating Organization Management module.", e);
        }
    }

    @Reference(name = "realm.service",
               service = org.wso2.carbon.user.core.service.RealmService.class,
               cardinality = ReferenceCardinality.MANDATORY,
               policy = ReferencePolicy.DYNAMIC,
               unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        OrganizationMgtDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Unset the Realm Service.");
        }
        OrganizationMgtDataHolder.getInstance().setRealmService(null);
    }

    @Reference(name = "configuration.context.service",
               service = ConfigurationContextService.class,
               cardinality = ReferenceCardinality.MANDATORY,
               policy = ReferencePolicy.DYNAMIC,
               unbind = "unsetConfigurationContextService")
    protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {

        if (log.isDebugEnabled()) {
            log.debug("ConfigurationContextService Instance registered.");
        }
    }

    protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {

        if (log.isDebugEnabled()) {
            log.debug("ConfigurationContextService Instance was unset.");
        }
    }

    @Reference(
            name = "identity.event.service",
            service = IdentityEventService.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityEventService"
    )
    protected void setIdentityEventService(IdentityEventService identityEventService) {

        OrganizationMgtDataHolder.getInstance().setIdentityEventService(identityEventService);
    }

    protected void unsetIdentityEventService(IdentityEventService identityEventService) {

        OrganizationMgtDataHolder.getInstance().setIdentityEventService(null);
    }

    // TODO add ROOT organization support for multiple tenants
    // TODO add support for multiple user store domains
    private void createRootIfNotExist() throws OrganizationManagementException {

        OrganizationManager organizationManager = (OrganizationManager) PrivilegedCarbonContext
                .getThreadLocalCarbonContext().getOSGiService(OrganizationManager.class);
        boolean rootExist = organizationManager.isOrganizationExistByName(ROOT);
        if (!rootExist) {
            Organization root = new Organization();
            String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
            int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            String userName = OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getBootstrapRealmConfiguration().getAdminUserName();
            String userId = getUserIDFromUserName(userName, tenantId);
            // Construct root organization
            root.setId(generateUniqueID());
            root.setName(ROOT);
            root.setDisplayName(ROOT);
            root.setDescription(ROOT);
            root.setStatus(Organization.OrgStatus.ACTIVE);
            root.setHasAttributes(false);
            root.setTenantId(tenantId);
            Metadata metadata = root.getMetadata();
            MetaUser createdBy = new MetaUser(userId,
                    String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, userId), userName);
            MetaUser lastModifiedBy = new MetaUser(userId,
                    String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, userId), userName);
            metadata.setCreatedBy(createdBy);
            metadata.setLastModifiedBy(lastModifiedBy);
            root.setMetadata(metadata);
            Map<String, UserStoreConfig> userStoreConfigs = root.getUserStoreConfigs();
            String userSearchBase = getLdapRootDn(PRIMARY, tenantId);
            userStoreConfigs.put(USER_STORE_DOMAIN, new UserStoreConfig(USER_STORE_DOMAIN, PRIMARY));
            userStoreConfigs.put(RDN, new UserStoreConfig(RDN, userSearchBase));
            userStoreConfigs.put(DN, new UserStoreConfig(DN, userSearchBase));
            // Create root
            CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                    OrganizationMgtDataHolder.getInstance().getCacheBackedOrganizationMgtDAO();
            cacheBackedOrganizationMgtDAO.addOrganization(tenantId, root);
            // Assign super user to the ROOT organization
            assignSuperUserToRootOrganization(tenantId, root.getId(), userName);
        }
    }

    private void assignSuperUserToRootOrganization(int tenantId, String rootId, String superUsername) throws
            OrganizationManagementServerException {

        // Get organization id and organization name claim URIs
        String orgNameClaimUri = !StringUtils.isBlank(IdentityUtil.getProperty(ORGANIZATION_NAME_CLAIM_URI)) ?
                IdentityUtil.getProperty(ORGANIZATION_NAME_CLAIM_URI).trim() :
                ORGANIZATION_NAME_DEFAULT_CLAIM_URI;
        String orgIdClaimUri = !StringUtils.isBlank(IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI)) ?
                IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI).trim() :
                ORGANIZATION_ID_DEFAULT_CLAIM_URI;
        try {
             UserStoreManager userStoreManager = OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getTenantUserRealm(tenantId).getUserStoreManager();
             Map<String, String> claims = new HashMap<>();
             claims.put(orgIdClaimUri, rootId);
             claims.put(orgNameClaimUri, ROOT);
             userStoreManager.setUserClaimValues(superUsername, claims, "default");
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_INITIALIZATION_ERROR,
                    "Error while assigning " + superUsername + " to the ROOT organization " + rootId +
                            ". Check if the claims are available : " + orgIdClaimUri + ", " + orgNameClaimUri, e);
        }
    }
}
