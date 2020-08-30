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
import org.wso2.carbon.custom.userstore.manager.CustomUserStoreManager;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_CHILDREN_GET_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_CONFIG_GET_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_CONFIG_PATCH_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_DELETE_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_GET_BY_ID_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_GET_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_RESOURCE_BASE_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_ADD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REMOVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REPLACE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_ACTIVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PRIMARY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.RDN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROOT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.SCIM2_USER_RESOURCE_BASE_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getLdapRootDn;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationAddObject;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationObject;

/**
 * This class implements the {@link OrganizationManager} interface.
 */
public class OrganizationManagerImpl implements OrganizationManager {

    private static final Log log = LogFactory.getLog(OrganizationManagerImpl.class);
    private OrganizationMgtDao organizationMgtDao = OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao();
    private int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    private String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    // TODO get scim id of the logged in user here.
    private String authenticatedUserId = "dummyId";

    @Override
    public Organization addOrganization(OrganizationAdd organizationAdd, boolean isImport)
            throws OrganizationManagementException {

        logOrganizationAddObject(organizationAdd);
        validateAddOrganizationRequest(organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        organization.setId(generateUniqueID());
        organization.setTenantId(tenantId);
        organization.getMetadata().getCreatedBy().setId(authenticatedUserId);
        organization.getMetadata().getCreatedBy().set$ref(String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, authenticatedUserId));
        organization.getMetadata().getLastModifiedBy().setId(authenticatedUserId);
        organization.getMetadata().getLastModifiedBy().set$ref(String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, authenticatedUserId));
        setUserStoreConfigs(organization);
        logOrganizationObject(organization);
        if (!isImport) {
            createLdapDirectory(
                    tenantId,
                    organization.getUserStoreConfigs().get(USER_STORE_DOMAIN).getValue(),
                    organization.getUserStoreConfigs().get(DN).getValue());
            if (log.isDebugEnabled()) {
                log.debug("Creating LDAP subdirectory for the organization id : " + organization.getId());
            }
        }
        organizationMgtDao.addOrganization(tenantId, organization);
        return organization;
    }

    @Override
    public Organization getOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_GET_BY_ID_REQUEST, "Provided organization ID is empty");
        }
        Organization organization = organizationMgtDao.getOrganization(tenantId, organizationId.trim());
        if (organization == null) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_GET_BY_ID_REQUEST,
                    "Organization id " + organizationId + " doesn't exist in this tenant : " + tenantId);
        }
        // Set derivable attributes
        if (organization.getParent().getId() != ROOT) {
            organization.getParent().set$ref(
                    String.format(ORGANIZATION_RESOURCE_BASE_PATH, tenantDomain, organization.getParent().getId()));
        }
        organization.getMetadata().getCreatedBy().set$ref(
                String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, organization.getMetadata().getCreatedBy().getId()));
        organization.getMetadata().getLastModifiedBy().set$ref(
                String.format(SCIM2_USER_RESOURCE_BASE_PATH, tenantDomain, organization.getMetadata().getLastModifiedBy().getId()));
        //TODO set meta users' username
        return organization;
    }

    @Override
    public String getOrganizationIdByName(String organizationName) throws OrganizationManagementException {

        // Throwing server exceptions as this method has not being exposed via an endpoint.
        if (StringUtils.isBlank(organizationName)) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR, "Provided organization name is empty.");
        }
        organizationName = organizationName.trim();
        String organizationId = organizationMgtDao.getOrganizationIdByName(tenantId, organizationName);
        if (organizationId == null) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR,
                    "Organization name " + organizationName + " doesn't exist in this tenant " + tenantId);
        }
        return organizationId;
    }

    @Override
    public List<Organization> getOrganizations(Condition condition, int offset, int limit, String sortBy, String sortOrder)
            throws OrganizationManagementException {

        // Validate pagination and sorting parameters
        sortBy = getMatchingColumnNameForSortingParameter(sortBy);
        return organizationMgtDao.getOrganizations(condition, tenantId, offset, limit, sortBy, sortOrder);
    }

    @Override
    public void patchOrganization(String organizationId, List<Operation> operations)
            throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Provided organization ID is empty");
        }
        organizationId = organizationId.trim();
        if (!isOrganizationExistById(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                    "Organization Id " + organizationId + " doesn't exist in this tenant " + tenantId);
        }
        validateOrganizationPatchOperations(operations, organizationId);
        for (Operation operation : operations) {
            organizationMgtDao.patchOrganization(organizationId, operation);
        }
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_DELETE_REQUEST, "Provided organization ID is empty");
        }
        if (!isOrganizationExistById(organizationId.trim())) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_DELETE_REQUEST,
                    "Organization Id " + organizationId + " doesn't exist in this tenant " + tenantId);
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

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CONFIG_GET_REQUEST, "Provided organization Id is empty");
        }
        organizationId = organizationId.trim();
        if (organizationMgtDao.isOrganizationExistById(tenantId, organizationId)) {
            return organizationMgtDao.getUserStoreConfigsByOrgId(tenantId, organizationId);
        } else {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CONFIG_GET_REQUEST,
                    "Provided organization Id " + organizationId + " doesn't exist in this tenant " + tenantId);
        }
    }

    @Override
    public List<String> getChildOrganizationIds(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CHILDREN_GET_REQUEST, "Provided organization Id is empty");
        }
        organizationId = organizationId.trim();
        if (organizationMgtDao.isOrganizationExistById(tenantId, organizationId)) {
            return organizationMgtDao.getChildOrganizationIds(organizationId);
        } else {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CHILDREN_GET_REQUEST,
                    " Provided organization Id " + organizationId + " doesn't exist in this tenant " + tenantId);
        }
    }

    @Override
    public void patchUserStoreConfigs(String organizationId, List<Operation> operations)
            throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CONFIG_PATCH_REQUEST, "Provided organization Id is empty");
        }
        organizationId = organizationId.trim();
        if (!isOrganizationExistById(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_CONFIG_PATCH_REQUEST,
                    "Provided organization Id " + organizationId + " doesn't exist in this tenant " + tenantId);
        }
        validateUserStoreConfigPatchOperations(operations, organizationId);
        for (Operation operation : operations) {
            organizationMgtDao.patchUserStoreConfigs(organizationId, operation);
        }
    }

    private void validateAddOrganizationRequest(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {
        //TODO check if RDN is available for the parent

        // Check required fields.
        if (StringUtils.isBlank(organizationAdd.getName())) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                    "Required fields are empty");
        }
        organizationAdd.setName(organizationAdd.getName().trim());
        // Trim display name
        if (StringUtils.isNotBlank(organizationAdd.getDisplayName())) {
            organizationAdd.setDisplayName(organizationAdd.getDisplayName().trim());
        } else {
            organizationAdd.setDisplayName(null);
        }
        // Attribute keys cannot be empty
        for (Attribute attribute : organizationAdd.getAttributes()) {
            if (StringUtils.isBlank(attribute.getKey())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                        "Attribute keys cannot be empty.");
            }
            // Sanitize input
            attribute.setKey(attribute.getKey().trim());
            attribute.setValue(attribute.getValue().trim());
        }
        // Check if attribute keys are duplicated
        Set<String> tempSet = new HashSet<>(organizationAdd.getAttributes().stream()
                .map(Attribute::getKey).collect(Collectors.toList()));
        if (organizationAdd.getAttributes().size() > tempSet.size()) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST, "Duplicate attribute keys detected");
        }
        // User store config keys and values can't be empty
        List<UserStoreConfig> userStoreConfigs = organizationAdd.getUserStoreConfigs();
        for (int i = 0; i < userStoreConfigs.size(); i++) {
            UserStoreConfig config = userStoreConfigs.get(i);
            if (StringUtils.isBlank(config.getKey()) || StringUtils.isBlank(config.getValue())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                        "User store config attribute keys or values cannot be empty.");
            }
            // Sanitize input
            config.setKey(config.getKey().trim().toUpperCase());
            config.setValue(config.getValue().trim());
            // Set user store domain value to upper case
            if (config.getKey().equals(USER_STORE_DOMAIN)) {
                config.setValue(config.getValue().toUpperCase());
            }
            // User store configs may only contain RDN and USER_STORE_DOMAIN. (DN to be derived and added later)
            if (!(config.getKey().equals(RDN) || config.getKey().equals(USER_STORE_DOMAIN))) {
                userStoreConfigs.remove(i);
                if (log.isDebugEnabled()) {
                    log.debug("Dropping additional user store configs. " +
                            "Only 'USER_STORE_DOMAIN' and 'RDN' are allowed." + config.getKey());
                }
            }
        }
        // Check if the organization name already exists for the given tenant
        if (isOrganizationExistByName(organizationAdd.getName())) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                    "Organization name " + organizationAdd.getName() + " already exists in this tenant.");
        }
        // Check if the parent organization exists
        String parentId = organizationAdd.getParent().getId();
        if (StringUtils.isNotBlank(parentId) &&
                !isOrganizationExistById(parentId.trim())) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                    "Defined parent organization doesn't exist in this tenant. " + parentId.trim());
        }
        parentId = StringUtils.isNotBlank(parentId) ? parentId.trim() : ROOT;
        organizationAdd.getParent().setId(parentId);
        // Load the parent organization, if not 'ROOT'
        Organization parentOrg = null;
        if (parentId != ROOT) {
            parentOrg = getOrganization(parentId);
            // populate parent's properties in the 'OrganizationAdd' object to avoid duplicate DB calls down the lane
            organizationAdd.getParent().setName(parentOrg.getName());
            organizationAdd.getParent().setDisplayName(parentOrg.getDisplayName());
            organizationAdd.getParent().set$ref(String.format(ORGANIZATION_RESOURCE_BASE_PATH, tenantDomain, parentId));
        } else {
            organizationAdd.getParent().setName(ROOT);
            organizationAdd.getParent().setDisplayName(ROOT);
        }
        // Check if the parent organization is active for non ROOT organizations
        if (parentId != ROOT && parentOrg.getStatus() != Organization.OrgStatus.ACTIVE) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST, "Defined parent organization : "
                    + parentId + " is not ACTIVE");
        }
        // Check if the user store domain matches that of the parent, for non ROOT organizations
        if (!ROOT.equals(parentId)) {
            String parentUserStoreDomain = getUserStoreConfigs(parentId).get(USER_STORE_DOMAIN).getValue();
            for (UserStoreConfig config : organizationAdd.getUserStoreConfigs()) {
                if (USER_STORE_DOMAIN.equals(config.getKey()) && !parentUserStoreDomain.equals(config.getValue())) {
                    throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                            "Defined user store domain : " + config.getValue() + ", doesn't match that of the parent : "
                                    + parentUserStoreDomain);
                }
            }
        }
    }

    private String constructDn(String parentId, String parentDn, String rdn, String userStoreDomain)
            throws OrganizationManagementException {

        boolean rootOrg = ROOT.equals(parentId);
        String dn;
        if (rootOrg) {
            String ldapRoot = getLdapRootDn(userStoreDomain, tenantId);
            dn = "ou=".concat(rdn).concat(",").concat(ldapRoot);
        } else {
            dn = "ou=".concat(rdn).concat(",").concat(parentDn);
        }
        return dn;
    }

    private Organization generateOrganizationFromRequest(OrganizationAdd organizationAdd) {

        Organization organization = new Organization();
        organization.setName(organizationAdd.getName());
        organization.setDisplayName(organizationAdd.getDisplayName());
        organization.setDescription(organizationAdd.getDescription());
        organization.getParent().setId(organizationAdd.getParent().getId());
        organization.getParent().setName(organizationAdd.getParent().getName());
        organization.getParent().setDisplayName(organizationAdd.getParent().getDisplayName());
        organization.getParent().set$ref(organizationAdd.getParent().get$ref());
        organization.setStatus(Organization.OrgStatus.ACTIVE);
        organization.setHasAttributes(!organizationAdd.getAttributes().isEmpty());
        // Convert attributes list to map for better accessibility
        organization.setAttributes(organizationAdd.getAttributes().stream().collect(
                Collectors.toMap(Attribute::getKey, attribute -> attribute)));
        organization.setUserStoreConfigs(organizationAdd.getUserStoreConfigs().stream().collect(
                Collectors.toMap(UserStoreConfig::getKey, config -> config)));
        return organization;
    }

    private void setUserStoreConfigs(Organization organization) throws OrganizationManagementException {

        Map<String, UserStoreConfig> parentConfigs = new HashMap<>();
        if (!ROOT.equals(organization.getParent().getId())) {
            // TODO this is a duplicate DB call. Same happens while validating the request
            parentConfigs = getUserStoreConfigs(organization.getParent().getId());
        }
        if (organization.getUserStoreConfigs().get(USER_STORE_DOMAIN) == null) {
            // If user store domain is not defined for a non-root organization, defaults to parent's domain
            if (!ROOT.equals(organization.getParent().getId())) {
                organization.getUserStoreConfigs().put(USER_STORE_DOMAIN,
                        new UserStoreConfig(USER_STORE_DOMAIN, parentConfigs.get(USER_STORE_DOMAIN).getValue()));
            } else {
                // If user store domain is not defined for a root organization, defaults to PRIMARY
                organization.getUserStoreConfigs().put(USER_STORE_DOMAIN, new UserStoreConfig(USER_STORE_DOMAIN, PRIMARY));
            }
        }
        // If RDN is not provided, defaults to organization ID
        if (organization.getUserStoreConfigs().get(RDN) == null) {
            organization.getUserStoreConfigs().put(RDN, new UserStoreConfig(RDN, organization.getId()));
        }
        // Construct and set DN using RDN, User store domain and the parent ID
        String dn = constructDn(
                organization.getParent().getId(),
                ROOT.equals(organization.getParent().getId()) ? null : parentConfigs.get(DN).getValue(),
                organization.getUserStoreConfigs().get(RDN).getValue(),
                organization.getUserStoreConfigs().get(USER_STORE_DOMAIN).getValue()
        );
        organization.getUserStoreConfigs().put(DN, new UserStoreConfig(DN, dn));
    }

    private String getMatchingColumnNameForSortingParameter(String sortBy)
            throws OrganizationManagementClientException {

        if (sortBy == null) {
            return null;
        }
        switch (sortBy.trim().toLowerCase()) {
            case "name":
                return VIEW_NAME_COLUMN;
            case "description":
                return VIEW_DESCRIPTION_COLUMN;
            case "createdtime":
                return VIEW_CREATED_TIME_COLUMN;
            case "lastmodified":
                return VIEW_LAST_MODIFIED_COLUMN;
            default:
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_GET_REQUEST,
                        "Invalid sort parameter. 'sortOrder' [ASC | DESC] and 'sortBy' [name | description | createdTime | lastModified ]");
        }
    }

    private void validateOrganizationPatchOperations(List<Operation> operations, String organizationId)
            throws OrganizationManagementException {

        for (Operation operation : operations) {
            // Validate op
            if (StringUtils.isBlank(operation.getOp())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation is not defined");
            }
            String op = operation.getOp().trim().toLowerCase();
            if (!(PATCH_OP_ADD.equals(op) || PATCH_OP_REMOVE.equals(op)
                    || PATCH_OP_REPLACE.equals(op))) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Patch op must be either ['add', 'replace', 'remove']");
            }

            // Validate path
            if (StringUtils.isBlank(operation.getPath())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation path is not defined");
            }
            String path = operation.getPath().trim();
            // Set path to lower case
            if (path.toLowerCase().startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                // Convert only the '/attributes/' part to lower case to treat the attribute name case sensitively
                path = path.replaceAll("(?i)" + Pattern.quote(PATCH_PATH_ORG_ATTRIBUTES), PATCH_PATH_ORG_ATTRIBUTES);
            } else if (path.equalsIgnoreCase(PATCH_PATH_ORG_PARENT_ID)) {
                path = PATCH_PATH_ORG_PARENT_ID;
            } else {
                path = path.toLowerCase();
            }
            // Is valid path
            if (!(path.equals(PATCH_PATH_ORG_NAME) ||
                    path.equals(PATCH_PATH_ORG_DESCRIPTION) ||
                    path.equals(PATCH_PATH_ORG_ACTIVE) ||
                    path.equals(PATCH_PATH_ORG_PARENT_ID) ||
                    path.startsWith(PATCH_PATH_ORG_ATTRIBUTES))) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Invalid Patch operation path : " + path);
            }

            // Validate value
            String value;
            // Value is mandatory for Add and Replace operations
            if (StringUtils.isBlank(operation.getValue()) && !PATCH_OP_REMOVE.equals(op)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation value is not defined");
            } else {
                // Avoid NPEs down the road
                value = operation.getValue() != null ? operation.getValue().trim() : "";
            }
            // You can only remove attributes
            if (PATCH_OP_REMOVE.equals(op) && !path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Can not remove mandatory field : " + path);
            }
            // Primary fields can only be 'Replaced'
            if (!path.startsWith(PATCH_PATH_ORG_ATTRIBUTES) && !op.equals(PATCH_OP_REPLACE)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Primary organization fields can only be replaced. Provided op : " + op + ", Path : " + path);
            }
            // Check for boolean values upon patching the ACTIVE field
            if (path.equals(PATCH_PATH_ORG_ACTIVE) &&
                    !(value.equals("true") || value.equals("false"))) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "ACTIVE field could only contain 'true' or 'false'. Provided : " + value);
            }
            // You can't deactivate an organization having any ACTIVE child
            if (path.equals(PATCH_PATH_ORG_ACTIVE) && value.equals("false") && !canDeactivate(organizationId)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Error deactivating organization : " + organizationId + " as it has one or more ACTIVE organization/s");
            }
            // Check if the new parent exist before patching the PARENT field
            if (path.equals(PATCH_PATH_ORG_PARENT_ID) && !isOrganizationExistById(value)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Provided parent ID does not exist : " + value);
            }
            // Check if new organization Name already exists
            if (path.equals(PATCH_PATH_ORG_NAME) && isOrganizationExistByName(value)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Provided organization name already exists : " + value);
            }
            if (path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                String attributeKey = path.replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
                // Attribute key can not be empty
                if (StringUtils.isBlank(attributeKey)) {
                    throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                            "Attribute key is not defined in the path : " + path);
                }
                boolean attributeExist = organizationMgtDao.isAttributeExistByKey(tenantId, organizationId, attributeKey);
                // If attribute key to be added already exists, update its value
                if (op.equals(PATCH_OP_ADD) && attributeExist) {
                    op = PATCH_OP_REPLACE;
                }
                if (op.equals(PATCH_OP_REMOVE) && !attributeExist) {
                    throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                            "Can not remove non existing attribute key : " + path);
                }
            }

            // Set sanitized input
            operation.setOp(op);
            operation.setPath(path);
            operation.setValue(value);
        }
    }

    private void validateUserStoreConfigPatchOperations(List<Operation> operations, String organizationId)
            throws OrganizationManagementException {

        for (Operation operation : operations) {
            // Validate op
            if (StringUtils.isBlank(operation.getOp())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation is not defined");
            }
            String op = operation.getOp().trim().toLowerCase();
            if (!PATCH_OP_REPLACE.equals(op)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "Configuration patch may only contain 'replace' operation");
            }

            // Validate path
            if (StringUtils.isBlank(operation.getPath())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation path is not defined");
            }
            String path = operation.getPath().trim().toUpperCase();
            // Only the RDN can be patched
            if (!RDN.equalsIgnoreCase(path)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST,
                        "UserStore configuration patch may only have 'RDN' as path. Provided:" + path);
            }
            // Validate value
            // Value is mandatory for user store config patch operations
            if (StringUtils.isBlank(operation.getValue())) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST, "Patch operation value is not defined");
            }
            // TODO check if the RDN available for the parent
            operation.setOp(PATCH_OP_REPLACE);
            operation.setPath(path);
            operation.setValue(operation.getValue().trim());
        }
    }

    /**
     * To deactivate an organization, it shouldn't have any 'ACTIVE' organizations down in the hierarchy.
     *
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    private boolean canDeactivate(String organizationId) throws OrganizationManagementException {

        List<String> children = getChildOrganizationIds(organizationId);
        for (String child : children) {
            Organization organization = getOrganization(child);
            if (organization.getStatus() == Organization.OrgStatus.ACTIVE) {
                if (log.isDebugEnabled()) {
                    log.debug("Active child organization : " + organization.getId());
                }
                return false;
            } else {
                return canDeactivate(organization.getId());
            }
        }
        return true;
    }

    private void createLdapDirectory(int tenantId, String userStoreDomain, String dn)
            throws OrganizationManagementException {

        try {
            UserRealm tenantUserRealm = ((UserRealm) OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getTenantUserRealm(tenantId));
            if (tenantUserRealm == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                        "Error obtaining tenant realm for the tenant id : " + tenantId);
            }
            if (tenantUserRealm.getUserStoreManager() == null ||
                    tenantUserRealm.getUserStoreManager().getSecondaryUserStoreManager(userStoreDomain) == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                        "Error obtaining user store manager for the domain : " + userStoreDomain + ", tenant id : " + tenantId);
            }
            UserStoreManager userStoreManager = tenantUserRealm.getUserStoreManager()
                    .getSecondaryUserStoreManager(userStoreDomain);
            if (userStoreManager instanceof CustomUserStoreManager) {
                ((CustomUserStoreManager) userStoreManager).createOu(dn);
                if (log.isDebugEnabled()) {
                    log.debug("Created subdirectory : " + dn + ", in the user store domain : " + userStoreDomain);
                }
            } else {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST,
                        "User store manager doesn't support adding LDAP directories. Tenant id : "
                                + tenantId + ", Domain : " + userStoreDomain);
            }
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                    "Error creating the DN : " + dn + " in the user store domain : " + userStoreDomain, e);
        }
    }
}
