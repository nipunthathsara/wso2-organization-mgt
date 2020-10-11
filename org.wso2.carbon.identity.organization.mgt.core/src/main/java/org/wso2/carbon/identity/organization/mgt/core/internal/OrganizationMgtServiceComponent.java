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
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManagerImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationAuthorizationDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.listener.OrganizationMgtAuditListener;
import org.wso2.carbon.identity.organization.mgt.core.listener.OrganizationMgtListener;
import org.wso2.carbon.identity.organization.mgt.core.validator.AttributeValidatorImpl;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DEFAULT_ATTRIBUTE_VALIDATOR_CLASS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ATTRIBUTE_VALIDATOR;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.populateManagementRoles;

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
            OrganizationMgtDataHolder.getInstance().setOrganizationMgtRoles(populateManagementRoles(-1234));
            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(OrganizationManager.class.getName(), new OrganizationManagerImpl(), null);
            bundleContext
                    .registerService(OrganizationMgtListener.class.getName(), new OrganizationMgtAuditListener(), null);
            String attributeValidatorClass = !StringUtils
                    .isBlank(IdentityUtil.getProperty(ORGANIZATION_ATTRIBUTE_VALIDATOR)) ?
                    IdentityUtil.getProperty(ORGANIZATION_ATTRIBUTE_VALIDATOR).trim() :
                    DEFAULT_ATTRIBUTE_VALIDATOR_CLASS;
            //TODO fix class loading error
            OrganizationMgtDataHolder.getInstance().setAttributeValidator(new AttributeValidatorImpl());
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
}
