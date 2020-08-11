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

package org.wso2.carbon.identity.organization.mgt.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_PAGINATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_SORTING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORG_ID_NOT_FOUND;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_ACCESS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PRIMARY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.RDN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.READ_WRITE_LDAP_USER_STORE_CLASS_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROOT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.UNIQUE_ID_READ_WRITE_LDAP_USER_STORE_CLASS_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationAddObject;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationObject;
import static org.wso2.carbon.user.core.ldap.LDAPConstants.USER_SEARCH_BASE;

/**
 * This class implements the {@link OrganizationManager} interface.
 */
public class OrganizationManagerImpl implements OrganizationManager {

    private static final Log log = LogFactory.getLog(OrganizationManagerImpl.class);
    private OrganizationMgtDao organizationMgtDao = OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao();
    private int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

    @Override
    public Organization addOrganization(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        logOrganizationAddObject(organizationAdd);
        validateAddOrganizationRequest(organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        organization.setId(generateUniqueID());
        organization.setTenantId(tenantId);
        // If user store domain is not provided, defaults to PRIMARY
        if (organization.getUserStoreConfigs().get(USER_STORE_DOMAIN) == null) {
            organization.getUserStoreConfigs().put(USER_STORE_DOMAIN, new UserStoreConfig(USER_STORE_DOMAIN, PRIMARY));
        }
        // If RDN is not provided, defaults to organization ID
        if (organization.getUserStoreConfigs().get(RDN) == null) {
            organization.getUserStoreConfigs().put(RDN, new UserStoreConfig(RDN, "ou=".concat(organization.getId())));
        }
        // Construct and set DN using RDN, User store domain and the parent ID
        String dn = constructDn(
                organization.getParentId(),
                organization.getUserStoreConfigs().get(RDN).getValue(),
                organization.getUserStoreConfigs().get(USER_STORE_DOMAIN).getValue()
        );
        organization.getUserStoreConfigs().put(DN, new UserStoreConfig(DN, dn));
        logOrganizationObject(organization);
        organizationMgtDao.addOrganization(tenantId, organization);
        return organization;
    }

    @Override
    public Organization getOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        Organization organization = organizationMgtDao.getOrganization(tenantId, organizationId.trim());
        if (organization == null) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR,
                    "Organization id" + organizationId + " doesn't exist");
        }
        return organization;
    }

    @Override
    public List<Organization> getOrganizations(int offset, int limit, String sortBy, String sortOrder)
            throws OrganizationManagementException {

        // Validate pagination and sorting parameters
        sortBy = getMatchingColumnNameForSortingParameter(sortBy);
        if (offset < 0 || (offset > -1 && limit < 0)) {
            throw handleClientException(ERROR_CODE_INVALID_PAGINATION, "[ limit > 0, offset >= 0]");
        }
        return organizationMgtDao.getOrganizations(tenantId, offset, limit, sortBy, sortOrder);
    }

    @Override
    public Organization patchOrganization(String organizationId, OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        return null;
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        if (!isOrganizationExistById(organizationId.trim())) {
            throw handleClientException(ERROR_CODE_ORG_ID_NOT_FOUND, "ID - " + organizationId + " Tenant - " + tenantId);
        }
        organizationMgtDao.deleteOrganization(tenantId, organizationId.trim());
    }

    @Override
    public boolean isOrganizationExistByName(String organizationName) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistByName(tenantId, organizationName);
    }

    @Override
    public boolean isOrganizationExistById(String id) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistById(tenantId, id);
    }

    @Override
    public Map<String, UserStoreConfig> getUserStoreConfigs(String organizationId)
            throws OrganizationManagementException {

        return organizationMgtDao.getUserStoreConfigsByOrgId(tenantId, organizationId);
    }

    private void validateAddOrganizationRequest(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        // Check required fields.
        if (StringUtils.isBlank(organizationAdd.getName())) {
            throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Required fields are empty");
        }
        organizationAdd.setName(organizationAdd.getName().trim());
        // Attribute keys cannot be empty
        for (Attribute attribute : organizationAdd.getAttributes()) {
            if (StringUtils.isBlank(attribute.getKey())) {
                throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                        "Attribute keys cannot be empty.");
            }
            // Sanitize input
            attribute.setKey(attribute.getKey().trim());
            attribute.setValue(attribute.getValue().trim());
        }
        // User store config keys and values can't be empty
        List<UserStoreConfig> userStoreConfigs = organizationAdd.getUserStoreConfigs();
        for (int i = 0; i < userStoreConfigs.size(); i++) {
            UserStoreConfig config = userStoreConfigs.get(i);
            if (StringUtils.isBlank(config.getKey()) || StringUtils.isBlank(config.getValue())) {
                throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                        "User store config attribute keys or values cannot be empty.");
            }
            // Sanitize input
            config.setKey(config.getKey().trim());
            config.setValue(config.getValue().trim());
            // User store configs may only contain RDN and USER_STORE_DOMAIN (DN to be constructed later)
            if (config.getKey().equalsIgnoreCase(RDN) || config.getKey().equalsIgnoreCase(USER_STORE_DOMAIN)) {
                config.setKey(config.getKey().toUpperCase());
            } else {
                userStoreConfigs.remove(i);
            }
        }
        // Check if the organization name already exists for the given tenant
        if (isOrganizationExistByName(organizationAdd.getName())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ALREADY_EXISTS_ERROR,
                    "Organization name " + organizationAdd.getName() + " already exists in this tenant.");
        }
        // Check if parent org exists
        if (StringUtils.isNotBlank(organizationAdd.getParentId()) &&
                !isOrganizationExistById(organizationAdd.getParentId().trim())) {
            throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Defined parent organization doesn't exist " + organizationAdd.getParentId().trim());
        }
        // TODO Check if the user store domain matches that of the parent.
        organizationAdd.setParentId(
                StringUtils.isNotBlank(organizationAdd.getParentId()) ? organizationAdd.getParentId().trim() : ROOT);
    }

    private String constructDn(String parentId, String rdn, String userStoreDomain)
            throws OrganizationManagementException {

        //TODO mind the user store domain
        String parentDn, dn;
        try {
            UserRealm userRealm = OrganizationMgtDataHolder.getInstance().getRealmService().getTenantUserRealm(tenantId);
            String userStoreClass = userRealm.getRealmConfiguration().getUserStoreClass();
            // Check if organization management is supported by the user store
            if (!(UNIQUE_ID_READ_WRITE_LDAP_USER_STORE_CLASS_NAME.equals(userStoreClass)
                    || READ_WRITE_LDAP_USER_STORE_CLASS_NAME.equals(userStoreClass))) {
                throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                        "Organization Mgt is only supported for Read/Write LDAP user stores. Provided domain : " + userStoreDomain);
            }
            if (ROOT.equals(parentId)) {
                // If root level organization
                parentDn = userRealm.getRealmConfiguration().getUserStoreProperty(USER_SEARCH_BASE);
                dn = rdn.concat(",").concat(parentDn);
            } else {
                dn = rdn.concat(",").concat(getUserStoreConfigs(parentId).get(DN).getValue());
            }
            if (log.isDebugEnabled()) {
                log.debug("User store domain : " + userStoreDomain + ", RDN : " + rdn + ", DN : " + dn);
            }
            return dn;
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_USER_STORE_ACCESS_ERROR, "Error while constructing the DN", e);
        }
    }

    private Organization generateOrganizationFromRequest(OrganizationAdd organizationAdd) {

        Organization organization = new Organization();
        organization.setName(organizationAdd.getName());
        organization.setDescription(organizationAdd.getDescription());
        organization.setParentId(organizationAdd.getParentId());
        organization.setActive(true);
        organization.setHasAttributes(!organizationAdd.getAttributes().isEmpty());
        // Convert attributes list to map for better accessibility
        organization.setAttributes(organizationAdd.getAttributes().stream().collect(
                Collectors.toMap(Attribute::getKey, attribute -> attribute)));
        organization.setUserStoreConfigs(organizationAdd.getUserStoreConfigs().stream().collect(
                Collectors.toMap(UserStoreConfig::getKey, config -> config)));
        return organization;
    }

    private String getMatchingColumnNameForSortingParameter(String sortBy)
            throws OrganizationManagementClientException {

        if (sortBy == null) {
            return null;
        }
        switch (sortBy.trim().toLowerCase()) {
            case "name":
                return "V.NAME";
            case "createdTime":
                return "V.CREATED_TIME";
            case "lastModified":
                return "V.LAST_MODIFIED";
            case "rdn":
                return "V.RDN";
            default:
                throw handleClientException(ERROR_CODE_INVALID_SORTING,
                        "'sortOrder' [ASC | DESC] and 'sortBy' [name | createdTime | lastModified | rdn]");
        }
    }
}
