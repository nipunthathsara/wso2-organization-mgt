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
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_SORTING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_PATCH_OPERATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVING_CHILD_ORGANIZATION_IDS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_ACCESS_ERROR;
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
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.READ_WRITE_LDAP_USER_STORE_CLASS_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROOT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.UNIQUE_ID_READ_WRITE_LDAP_USER_STORE_CLASS_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getLdapRootDn;
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
    public Organization addOrganization(OrganizationAdd organizationAdd, boolean isImport)
            throws OrganizationManagementException {

        logOrganizationAddObject(organizationAdd);
        validateAddOrganizationRequest(organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        Map<String, UserStoreConfig> parentConfigs = new HashMap<>();
        if (!ROOT.equals(organization.getParentId())) {
            parentConfigs = getUserStoreConfigs(organization.getParentId());
        }
        organization.setId(generateUniqueID());
        organization.setTenantId(tenantId);
        if (organization.getUserStoreConfigs().get(USER_STORE_DOMAIN) == null) {
            // If user store domain is not defined for a non-root organization, defaults to parent's domain
            if (!ROOT.equals(organization.getParentId())) {
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
                organization.getParentId(),
                ROOT.equals(organization.getParentId()) ? null : parentConfigs.get(DN).getValue(),
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
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        organizationId = organizationId.trim();
        if (!isOrganizationExistById(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR,
                    "ID - " + organizationId + " doesn't exist in this tenant - " + tenantId);
        }
        validateOrganizationPatchOperations(operations, organizationId);
        for (Operation operation : operations) {
            if (operation.equals(PATCH_OP_ADD)) {

            }
        }
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        if (!isOrganizationExistById(organizationId.trim())) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR,
                    "ID - " + organizationId + " doesn't exist in this tenant - " + tenantId);
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

    @Override
    public List<String> getChildOrganizationIds(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        organizationId = organizationId.trim();
        if (organizationMgtDao.isOrganizationExistById(tenantId, organizationId)) {
            return organizationMgtDao.getChildOrganizationIds(organizationId);
        } else {
            throw handleClientException(ERROR_CODE_RETRIEVING_CHILD_ORGANIZATION_IDS_ERROR,
                    organizationId + ". This organization ID doesn't exist in this tenant");
        }
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
                if (log.isDebugEnabled()) {
                    log.debug("Dropping additional user store configs. Only 'USER_STORE_DOMAIN' and 'RDN' are allowed.");
                }
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
                    "Defined parent organization doesn't exist in this tenant. " + organizationAdd.getParentId().trim());
        }
        organizationAdd.setParentId(
                StringUtils.isNotBlank(organizationAdd.getParentId()) ? organizationAdd.getParentId().trim() : ROOT);
        // Check if the user store domain matches that of the parent, for non ROOT organizations
        if (!ROOT.equals(organizationAdd.getParentId())) {
            String parentUserStoreDomain = getUserStoreConfigs(organizationAdd.getParentId()).get(USER_STORE_DOMAIN).getValue();
            for (UserStoreConfig config : organizationAdd.getUserStoreConfigs()) {
                if (USER_STORE_DOMAIN.equals(config.getKey()) && !parentUserStoreDomain.equals(config.getValue())) {
                    throw handleClientException(ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                            "Defined user store domain : " + config.getValue() + ", doesn't match that of the parent : " + parentUserStoreDomain);
                }
            }
        }
    }

    private String constructDn(String parentId, String parentDn, String rdn, String userStoreDomain)
            throws OrganizationManagementException {

        boolean rootOrg = ROOT.equals(parentId);
        String dn;
        if (rootOrg) {
            String ldapRoot = getLdapRootDn(userStoreDomain);
            dn = "ou=".concat(rdn).concat(",").concat(ldapRoot);
        } else {
            dn = "ou=".concat(rdn).concat(",").concat(parentDn);
        }
        return dn;
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
                return VIEW_NAME;
            case "description":
                return VIEW_DESCRIPTION;
            case "createdtime":
                return VIEW_CREATED_TIME;
            case "lastmodified":
                return VIEW_LAST_MODIFIED;
            default:
                throw handleClientException(ERROR_CODE_INVALID_SORTING,
                        "'sortOrder' [ASC | DESC] and 'sortBy' [name | description | createdTime | lastModified ]");
        }
    }

    private void validateOrganizationPatchOperations(List<Operation> operations, String organizationId)
            throws OrganizationManagementException {

        for (Operation operation : operations) {
            // Validate op
            if (StringUtils.isBlank(operation.getOp())) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR, "Patch operation is not defined");
            }
            String op = operation.getOp().trim().toLowerCase();
            if (!(PATCH_OP_ADD.equals(operation.getOp()) || PATCH_OP_REMOVE.equals(operation.getOp())
                    || PATCH_OP_REPLACE.equals(operation.getOp()))) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                        "Patch op must be either ['add', 'replace', 'remove']");
            }
            // Validate path
            if (StringUtils.isBlank(operation.getPath())) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR, "Patch operation path is not defined");
            }
            String path = operation.getPath().trim();
            if (!(path.equalsIgnoreCase(PATCH_PATH_ORG_NAME)) ||
                    path.equalsIgnoreCase(PATCH_PATH_ORG_DESCRIPTION) ||
                    path.equalsIgnoreCase(PATCH_PATH_ORG_ACTIVE) ||
                    path.equalsIgnoreCase(PATCH_PATH_ORG_PARENT_ID) ||
                    path.toLowerCase().startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR, "Invalid Patch operation path : " + path);
            }
            // Validate value
            String value = null;
            // Value is mandatory for Add and Replace operations
            if (StringUtils.isBlank(operation.getValue()) && !PATCH_OP_REMOVE.equals(op)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR, "Patch operation value is not defined");
            } else {
                value = operation.getValue().trim();
            }
            // You can only remove attributes
            if (PATCH_OP_REMOVE.equals(op) && !path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR, "Can not remove mandatory field : " + path);
            }
            // Treat attribute paths(attribute names) case sensitive
            if (!path.toLowerCase().startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                path = path.toLowerCase();
            }
            // Primary fields can only be Replaced
            if (!path.startsWith(PATCH_PATH_ORG_ATTRIBUTES) && !op.equals(PATCH_OP_REPLACE)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                        "Primary organization fields can only be replaced. Provided op : " + op + ", Path : " + path);
            }
            // Check for boolean values upon patching the ACTIVE field
            if (path.equals(PATCH_PATH_ORG_ACTIVE) &&
                    !(value.equals("true") || value.equals("false"))) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                        "ACTIVE field could only contain 'true' or 'false'. Provided : " + value);
            }
            // Check if new parent exist before patching the PARENT field
            if (path.equals(PATCH_PATH_ORG_PARENT_ID) && !isOrganizationExistById(value)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                        "Provided parent ID does not exist : " + value);
            }
            // Check if new organization Name already exists
            if (path.equals(PATCH_PATH_ORG_NAME) && isOrganizationExistByName(value)) {
                throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                        "Provided organization name already exists : " + value);
            }
            if (path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                String attributeKey = path.replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
                // Attribute key can not be empty
                if (StringUtils.isBlank(attributeKey)) {
                    throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                            "Attribute key not define in the path : " + path);
                }
                boolean attributeExist = organizationMgtDao.isAttributeExistByKey(tenantId, organizationId, attributeKey);
                // If attribute key to be added already exists, update its value
                if (op.equals(PATCH_OP_ADD) && attributeExist) {
                    op = PATCH_OP_REPLACE;
                }
                if (op.equals(PATCH_OP_REMOVE) && !attributeExist) {
                    throw handleClientException(ERROR_CODE_PATCH_OPERATION_ERROR,
                            "Can not remove non existing attribute key : " + path);
                }
            }

            // Set sanitized input
            operation.setOp(op);
            operation.setPath(path);
            operation.setValue(value);
        }
    }
}
