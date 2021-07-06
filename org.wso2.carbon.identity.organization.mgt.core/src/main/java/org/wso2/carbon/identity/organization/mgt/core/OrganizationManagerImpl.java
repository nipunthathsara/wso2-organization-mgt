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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.Status;
import org.wso2.carbon.identity.organization.mgt.core.dao.CacheBackedOrganizationMgtDAO;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationAuthorizationDao;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationAuthorizationDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.MetaUser;
import org.wso2.carbon.identity.organization.mgt.core.model.Metadata;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.identity.organization.mgt.core.usermgt.AbstractOrganizationMgtUserStoreManager;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.CASCADE_INSERT_USER_ORG_ROLES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_DISABLED_PARENT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_DUPLICATE_ATTRIBUTE_KEYS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_INCOMPATIBLE_USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_INCOMPATIBLE_USER_STORE_MANAGER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_INVALID_PARENT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_MISSING_ATTRIBUTE_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_MISSING_USER_STORE_CONFIG_KEY_OR_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_NAME_CONFLICT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_RDN_CONFLICT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_REQUIRED_FIELDS_MISSING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_UNAUTHORIZED_PARENT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ADD_REQUEST_UNEXPECTED_DN_PARAMETER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_ACTIVE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_CANNOT_DELETE_WITH_ACTIVE_CHILD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_CANNOT_DELETE_WITH_USERS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_ORGANIZATION_ID_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.DELETE_REQUEST_UNSUPPORTED_USER_STORE_MANAGER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_EVENTING_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_DELETE_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_CHILDREN_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_CHILDREN_REQUEST_ORGANIZATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_ID_BY_NAME_REQUEST_ORGANIZATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_ORG_BY_NAME_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_REQUEST_ORGANIZATION_ID_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.GET_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.IMPORT_REQUEST_REQUIRED_FIELDS_MISSING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.LIST_REQUEST_INVALID_SORT_PARAMETER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_ATTRIBUTE_KEY_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_CANNOT_ACTIVATE_WITH_DISABLED_PARENT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_CHILD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_USERS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_ATTRIBUTE_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_OPERATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_PARENT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_REMOVE_OPERATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_INVALID_STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_NAME_UNAVAILABLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_OPERATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_ORGANIZATION_ID_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_PATH_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_REQUEST_VALUE_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_OPERATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_OPERATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_PATH_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_RDN_UNAVAILABLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.PATCH_USER_STORE_CONFIGS_REQUEST_VALUE_UNDEFINED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ADMIN_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_CREATE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_RESOURCE_BASE_PATH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_ADD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REMOVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REPLACE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_DISPLAY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.RDN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROOT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.UI_EXECUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.DATA;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.ORGANIZATION_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_CREATE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_DELETE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_CHILD_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_IMPORT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_LIST_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_PATCH_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_PATCH_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_CREATE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_DELETE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_GET_CHILD_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_GET_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_GET_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_IMPORT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_LIST_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_PATCH_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_PATCH_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.TENANT_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.USER_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.USER_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COLUMN_LOWER_WRAPPER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_STATUS_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getUserIDFromUserName;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.hasActiveUsers;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.hasUsers;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationAddObject;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationObject;

/**
 * This class implements the {@link OrganizationManager} interface.
 */
public class OrganizationManagerImpl implements OrganizationManager {

    private static final Log log = LogFactory.getLog(OrganizationManagerImpl.class);
    private OrganizationMgtDao organizationMgtDao = OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao();
    private OrganizationAuthorizationDao authorizationDao = OrganizationMgtDataHolder.getInstance()
            .getOrganizationAuthDao();
    private CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
            OrganizationMgtDataHolder.getInstance().getCacheBackedOrganizationMgtDAO();

    @Override
    @SuppressFBWarnings("GC_UNRELATED_TYPES")
    public Organization addOrganization(OrganizationAdd organizationAdd, boolean isImport)
            throws OrganizationManagementException {

        logOrganizationAddObject(organizationAdd);
        // 'DN' will only be accepted via /import API or if appended by a pre-handler
        sanitizeUserStoreConfigKeys(organizationAdd);
        if (organizationAdd.getUserStoreConfigs().contains(DN) && !isImport) {
            throw handleClientException(ADD_REQUEST_UNEXPECTED_DN_PARAMETER, null);
        }
        // Fire pre-event
        String event = isImport ? PRE_IMPORT_ORGANIZATION : PRE_CREATE_ORGANIZATION;
        fireEvent(event, null, organizationAdd, Status.FAILURE);
        validateAddOrganizationRequest(organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        // We can't perform this in the authorization valve. Hence, authorize here
        boolean isAuthorized = isUserAuthorizedToCreateOrganization(organization.getParent().getId());
        if (!isAuthorized) {
            throw handleClientException(ADD_REQUEST_UNAUTHORIZED_PARENT, null);
        }
        organization.setId(generateUniqueID());
        organization.setTenantId(getTenantId());
        // Set metadata
        organization.getMetadata().getCreatedBy().setId(getAuthenticatedUserId());
//        organization.getMetadata().getCreatedBy()
//                .setRef(String.format(SCIM2_USER_RESOURCE_BASE_PATH, getTenantDomain(), getAuthenticatedUserId()));
//        organization.getMetadata().getCreatedBy().setUsername(getAuthenticatedUsername());
        organization.getMetadata().setLastModifiedBy(organization.getMetadata().getCreatedBy());
        setUserStoreConfigs(organization, isImport);
        logOrganizationObject(organization);
        if (!isImport) {
            createLdapDirectory(
                    getTenantId(),
                    organization.getUserStoreConfigs().get(USER_STORE_DOMAIN).getValue(),
                    organization.getUserStoreConfigs().get(DN).getValue(),
                    organization.getUserStoreConfigs().get(RDN).getValue()
            );
            if (log.isDebugEnabled()) {
                log.debug("Creating LDAP subdirectory for the organization id : " + organization.getId());
            }
        }
        cacheBackedOrganizationMgtDAO.addOrganization(getTenantId(), organization);
        grantImmediateParentPermissions(organization.getId(), organization.getParent().getId(),
                getAuthenticatedUserId());
        // Fire post-event
        event = isImport ? POST_IMPORT_ORGANIZATION : POST_CREATE_ORGANIZATION;
        fireEvent(event, null, organization, Status.SUCCESS);
        return organization;
    }

    @Override
    public Organization getOrganization(String organizationId, boolean includePermissions)
            throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_GET_ORGANIZATION, organizationId, null, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(GET_REQUEST_ORGANIZATION_ID_UNDEFINED, null);
        }
        Organization organization;
        if (includePermissions) {
            boolean getAsAdmin = isAuthorizedAsAdmin();
            // Response should include permissions of the authenticated user
            organization = organizationMgtDao.getOrganization(getTenantId(), organizationId.trim(),
                    getAuthenticatedUserId(), getAsAdmin);
        } else {
            organization = organizationMgtDao.getOrganization(getTenantId(), organizationId.trim(), null, false);
        }
        if (organization == null) {
            throw handleClientException(GET_REQUEST_INVALID_ORGANIZATION, null);
        }
        // Set derivable attributes
        if (!ROOT.equals(organization.getName())) {
            organization.getParent().setRef(String
                    .format(ORGANIZATION_RESOURCE_BASE_PATH, getTenantDomain(), organization.getParent().getId()));
        }
//        organization.getMetadata().getCreatedBy().setRef(String.format(SCIM2_USER_RESOURCE_BASE_PATH,
//                getTenantDomain(), organization.getMetadata().getCreatedBy().getId()));
//        organization.getMetadata().getCreatedBy()
//                .setUsername(getUserNameFromUserID(organization.getMetadata().getCreatedBy().getId(), getTenantId()));
//        organization.getMetadata().getLastModifiedBy().setRef(String
//                .format(SCIM2_USER_RESOURCE_BASE_PATH, getTenantDomain(),
//                        organization.getMetadata().getLastModifiedBy().getId()));
//        organization.getMetadata().getLastModifiedBy().setUsername(
//                getUserNameFromUserID(organization.getMetadata().getLastModifiedBy().getId(), getTenantId()));
        // Fire post-event
        fireEvent(POST_GET_ORGANIZATION, organizationId, organization, Status.SUCCESS);
        return organization;
    }

    @Override
    public List<Organization> getOrganizations(Condition condition, int offset, int limit, String sortBy,
                                               String sortOrder, List<String> requestedAttributes,
                                               boolean includePermissions)
            throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_LIST_ORGANIZATIONS, null, condition, Status.FAILURE);
        // Validate pagination and sorting parameters
        sortBy = getMatchingColumnNameForSortingParameter(sortBy);
        boolean listAsAdmin = isAuthorizedAsAdmin();
        List<Organization> organizations = organizationMgtDao
                .getOrganizations(condition, getTenantId(), offset, limit, sortBy, sortOrder, requestedAttributes,
                        getAuthenticatedUserId(), includePermissions, listAsAdmin);
        // Populate derivable information of the organizations
        for (Organization organization : organizations) {
            if (!ROOT.equals(organization.getName())) {
                organization.getParent().setRef(String
                        .format(ORGANIZATION_RESOURCE_BASE_PATH, getTenantDomain(), organization.getParent().getId()));
            }
//            organization.getMetadata().getCreatedBy().setRef(String
//                    .format(SCIM2_USER_RESOURCE_BASE_PATH, getTenantDomain(),
//                            organization.getMetadata().getCreatedBy().getId()));
//            organization.getMetadata().getLastModifiedBy().setRef(String
//                    .format(SCIM2_USER_RESOURCE_BASE_PATH, getTenantDomain(),
//                            organization.getMetadata().getLastModifiedBy().getId()));
        }
        // Fire post-events
        fireEvent(POST_LIST_ORGANIZATIONS, null, organizations, Status.SUCCESS);
        return organizations;
    }

    @Override
    public String getOrganizationIdByName(String organizationName) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationName)) {
            throw handleClientException(GET_ID_BY_NAME_REQUEST_ORGANIZATION_UNDEFINED, null);
        }
        organizationName = organizationName.trim();
        String organizationId = organizationMgtDao.getOrganizationIdByName(getTenantId(), organizationName);
        if (organizationId == null) {
            throw handleClientException(GET_ORG_BY_NAME_REQUEST_INVALID_ORGANIZATION, null);
        }
        return organizationId;
    }

    @Override
    public void patchOrganization(String organizationId, List<Operation> operations)
            throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_PATCH_ORGANIZATION, organizationId, operations, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(PATCH_REQUEST_ORGANIZATION_ID_UNDEFINED,
                    "Provided organization ID is empty");
        }
        organizationId = organizationId.trim();
        if (!isOrganizationExistById(organizationId)) {
            throw handleClientException(PATCH_REQUEST_INVALID_ORGANIZATION, null);
        }
        validateOrganizationPatchOperations(operations, organizationId);
        for (Operation operation : operations) {
            cacheBackedOrganizationMgtDAO.patchOrganization(organizationId, operation);
            // Fire post-event
            fireEvent(POST_PATCH_ORGANIZATION, organizationId, operation, Status.SUCCESS);
        }
        // Update metadata
        Metadata metadata = new Metadata();
        metadata.setLastModifiedBy(new MetaUser());
        metadata.getLastModifiedBy().setId(getAuthenticatedUserId());
        organizationMgtDao.modifyOrganizationMetadata(organizationId, metadata);
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_DELETE_ORGANIZATION, organizationId, null, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(DELETE_REQUEST_ORGANIZATION_ID_UNDEFINED, null);
        }
        if (!isOrganizationExistById(organizationId.trim())) {
            throw handleClientException(DELETE_REQUEST_INVALID_ORGANIZATION, null);
        }
        Organization organization = organizationMgtDao.getOrganization(getTenantId(), organizationId, null, false);
        Map<String, UserStoreConfig> configs = organizationMgtDao.getUserStoreConfigsByOrgId(getTenantId(),
                organizationId);
        String userStoreDomain = configs.get(USER_STORE_DOMAIN).getValue();
        String dn = configs.get(DN).getValue();

        validateOrganizationDelete(organizationId, organization);

        // Remove OU from LDAP
        deleteLdapDirectory(getTenantId(), userStoreDomain, dn);
        cacheBackedOrganizationMgtDAO.deleteOrganization(getTenantId(), organizationId.trim());
        // Fire post-event
        fireEvent(POST_DELETE_ORGANIZATION, organizationId, null, Status.SUCCESS);
    }

    private void validateOrganizationDelete(String organizationId, Organization organization)
            throws OrganizationManagementException {

        // Organization should be in the disabled status
        if (!Organization.OrgStatus.DISABLED.equals(organization.getStatus())) {
            throw handleClientException(DELETE_REQUEST_ACTIVE_ORGANIZATION, null);
        }
        // Organization shouldn't have any child organizations.
        if (organizationMgtDao.getChildOrganizationIds(organizationId, null).size() != 0) {
            throw handleClientException(DELETE_REQUEST_CANNOT_DELETE_WITH_ACTIVE_CHILD, null);
        }

        // Organization shouldn't have any users.
        if (hasUsers(organizationId, getTenantId())) {
            throw handleClientException(DELETE_REQUEST_CANNOT_DELETE_WITH_USERS, null);
        }
    }

    @Override
    public boolean isOrganizationExistByName(String organizationName) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistByName(getTenantId(), organizationName);
    }

    @Override
    public boolean isOrganizationExistById(String id) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistById(getTenantId(), id);
    }

    @Override
    public Map<String, UserStoreConfig> getUserStoreConfigs(String organizationId)
            throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_GET_USER_STORE_CONFIGS, organizationId, null, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(GET_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED, null);
        }
        organizationId = organizationId.trim();
        if (organizationMgtDao.isOrganizationExistById(getTenantId(), organizationId)) {
            Map<String, UserStoreConfig> userStoreConfigs = organizationMgtDao
                    .getUserStoreConfigsByOrgId(getTenantId(), organizationId);
            // Fire post-events
            fireEvent(POST_GET_USER_STORE_CONFIGS, organizationId, userStoreConfigs, Status.SUCCESS);
            return userStoreConfigs;
        } else {
            throw handleClientException(GET_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION, null);
        }
    }

    @Override
    public List<String> getChildOrganizationIds(String organizationId) throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_GET_CHILD_ORGANIZATIONS, organizationId, null, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(GET_CHILDREN_REQUEST_ORGANIZATION_UNDEFINED, null);
        }
        organizationId = organizationId.trim();
        if (organizationMgtDao.isOrganizationExistById(getTenantId(), organizationId)) {
            List<String> childOrganizationIds = organizationMgtDao
                    .getChildOrganizationIds(organizationId, getAuthenticatedUserId());
            // Fire post-event
            fireEvent(POST_GET_CHILD_ORGANIZATIONS, organizationId, childOrganizationIds, Status.SUCCESS);
            return childOrganizationIds;
        } else {
            throw handleClientException(GET_CHILDREN_REQUEST_INVALID_ORGANIZATION, null);
        }
    }

    @Override
    public void patchUserStoreConfigs(String organizationId, List<Operation> operations)
            throws OrganizationManagementException {

        // Fire pre-event
        fireEvent(PRE_PATCH_USER_STORE_CONFIGS, organizationId, operations, Status.FAILURE);
        if (StringUtils.isBlank(organizationId)) {
            throw handleClientException(PATCH_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED, null);
        }
        organizationId = organizationId.trim();
        if (!isOrganizationExistById(organizationId)) {
            throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION, null);
        }
        validateUserStoreConfigPatchOperations(operations, organizationId);
        for (Operation operation : operations) {
            organizationMgtDao.patchUserStoreConfigs(organizationId, operation);
            // Fire post-event
            fireEvent(POST_PATCH_USER_STORE_CONFIGS, organizationId, operation, Status.SUCCESS);
        }
        // Update metadata
        Metadata metadata = new Metadata();
        metadata.setLastModifiedBy(new MetaUser());
        metadata.getLastModifiedBy().setId(getAuthenticatedUserId());
        organizationMgtDao.modifyOrganizationMetadata(organizationId, metadata);
    }

    private void validateAddOrganizationRequest(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        // Check required fields.
        if (StringUtils.isBlank(organizationAdd.getName())) {
            throw handleClientException(ADD_REQUEST_REQUIRED_FIELDS_MISSING, "name");
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
                throw handleClientException(ADD_REQUEST_MISSING_ATTRIBUTE_KEY, null);
            }
            // Sanitize input
            attribute.setKey(attribute.getKey().trim());
            attribute.setValue(attribute.getValue().trim());
        }
        // Check if attribute keys are duplicated
        Set<String> tempSet = new HashSet<>(
                organizationAdd.getAttributes().stream().map(Attribute::getKey).collect(Collectors.toList()));
        if (organizationAdd.getAttributes().size() > tempSet.size()) {
            throw handleClientException(ADD_REQUEST_DUPLICATE_ATTRIBUTE_KEYS, null);
        }
        // User store config keys and values can't be empty
        List<UserStoreConfig> userStoreConfigs = organizationAdd.getUserStoreConfigs();
        for (int i = 0; i < userStoreConfigs.size(); i++) {
            UserStoreConfig config = userStoreConfigs.get(i);
            if (StringUtils.isBlank(config.getKey()) || StringUtils.isBlank(config.getValue())) {
                throw handleClientException(ADD_REQUEST_MISSING_USER_STORE_CONFIG_KEY_OR_VALUE, null);
            }
            // Sanitize input
//            config.setKey(config.getKey().trim().toUpperCase(Locale.ENGLISH));
            config.setValue(config.getValue().trim());
            // Set user store domain value to upper case
            if (config.getKey().equals(USER_STORE_DOMAIN)) {
                config.setValue(config.getValue().toUpperCase(Locale.ENGLISH));
            }
            // User store configs may only contain RDN, DN and USER_STORE_DOMAIN. (DN to be derived and added later
            // when not defined in the request)
            if (!(config.getKey().equals(RDN) || config.getKey().equals(USER_STORE_DOMAIN)
                    || config.getKey().equals(DN))) {
                userStoreConfigs.remove(i);
                if (log.isDebugEnabled()) {
                    log.debug("Dropping additional user store config - " + config.getKey()
                            + ". Only 'USER_STORE_DOMAIN', 'RDN' and 'DN' are allowed.");
                }
            }
        }
        // Check if the organization name already exists for the given tenant
        if (isOrganizationExistByName(organizationAdd.getName())) {
            throw handleClientException(ADD_REQUEST_NAME_CONFLICT, null);
        }
        // Check if the parent organization exists
        String parentId = organizationAdd.getParent().getId();
        if (StringUtils.isNotBlank(parentId) && !isOrganizationExistById(parentId.trim())) {
            throw handleClientException(ADD_REQUEST_INVALID_PARENT_ORGANIZATION, null);
        }
        // If parent id is not specified, the organization comes under the ROOT organization
        parentId = StringUtils.isBlank(parentId) ?
                organizationMgtDao.getOrganizationIdByName(getTenantId(), ROOT) :
                parentId.trim();
        organizationAdd.getParent().setId(parentId);
        // Load the parent organization
        Organization parentOrg = getOrganization(parentId, false);
        // populate parent's properties in the 'OrganizationAdd' object to avoid duplicate DB calls down the lane
        organizationAdd.getParent().setName(parentOrg.getName());
        organizationAdd.getParent().setDisplayName(parentOrg.getDisplayName());
        organizationAdd.getParent().setRef(String.format(ORGANIZATION_RESOURCE_BASE_PATH, getTenantDomain(), parentId));
        // Check if the parent organization is active
        if (parentOrg.getStatus() != Organization.OrgStatus.ACTIVE) {
            throw handleClientException(ADD_REQUEST_DISABLED_PARENT_ORGANIZATION, null);
        }
        // Check if the user store domain matches that of the parent
        String parentUserStoreDomain = getUserStoreConfigs(parentId).get(USER_STORE_DOMAIN).getValue();
        for (UserStoreConfig config : organizationAdd.getUserStoreConfigs()) {
            if (USER_STORE_DOMAIN.equals(config.getKey()) && !parentUserStoreDomain.equals(config.getValue())) {
                throw handleClientException(ADD_REQUEST_INCOMPATIBLE_USER_STORE_DOMAIN,
                        "Defined user store domain : " + config.getValue() + ", doesn't match that of the parent : "
                                + parentUserStoreDomain);
            }
        }
    }

    private Organization generateOrganizationFromRequest(OrganizationAdd organizationAdd) {

        Organization organization = new Organization();
        organization.setName(organizationAdd.getName());
        organization.setDisplayName(organizationAdd.getDisplayName());
        organization.setDescription(organizationAdd.getDescription());
        organization.getParent().setId(organizationAdd.getParent().getId());
        organization.getParent().setName(organizationAdd.getParent().getName());
        organization.getParent().setDisplayName(organizationAdd.getParent().getDisplayName());
        organization.getParent().setRef(organizationAdd.getParent().getRef());
        organization.setStatus(Organization.OrgStatus.ACTIVE);
        organization.setHasAttributes(!organizationAdd.getAttributes().isEmpty());
        // Convert attributes 'list' to a 'map' for better accessibility
        organization.setAttributes(organizationAdd.getAttributes().stream()
                .collect(Collectors.toMap(Attribute::getKey, attribute -> attribute)));
        organization.setUserStoreConfigs(organizationAdd.getUserStoreConfigs().stream()
                .collect(Collectors.toMap(UserStoreConfig::getKey, config -> config)));
        return organization;
    }

    private void setUserStoreConfigs(Organization organization, boolean isImport)
            throws OrganizationManagementException {

        Map<String, UserStoreConfig> parentConfigs = getUserStoreConfigs(organization.getParent().getId());
        if (organization.getUserStoreConfigs().get(USER_STORE_DOMAIN) == null) {
            // If user store domain is not defined, defaults to parent's user store domain
            organization.getUserStoreConfigs().put(USER_STORE_DOMAIN,
                    new UserStoreConfig(USER_STORE_DOMAIN, parentConfigs.get(USER_STORE_DOMAIN).getValue()));
        }
        // Import organization requests must mention the RDN explicitly
        if (organization.getUserStoreConfigs().get(RDN) == null && isImport) {
            throw handleClientException(IMPORT_REQUEST_REQUIRED_FIELDS_MISSING, null);
        }
        // If RDN is not provided, defaults to organization ID
        if (organization.getUserStoreConfigs().get(RDN) == null) {
            organization.getUserStoreConfigs().put(RDN, new UserStoreConfig(RDN, organization.getId()));
        }
        String dn = organization.getUserStoreConfigs().get(DN) != null ?
                organization.getUserStoreConfigs().get(DN).getValue() : null;
        // If 'DN' is provided, no validation needed for RDN or DN. As it may or may not be related to the parent
        if (StringUtils.isBlank(dn)) {
            // Check if the RDN is already taken
            boolean isAvailable =
                    organizationMgtDao.isRdnAvailable(organization.getUserStoreConfigs().get(RDN).getValue(),
                            organization.getParent().getId(), getTenantId());
            if (!isAvailable) {
                throw handleClientException(ADD_REQUEST_RDN_CONFLICT, null);
            }
            // Construct and set DN using RDN, User store domain and the parent ID
            dn = "ou="
                    .concat(organization.getUserStoreConfigs().get(RDN).getValue())
                    .concat(",").concat(parentConfigs.get(DN).getValue());
        }
        organization.getUserStoreConfigs().put(DN, new UserStoreConfig(DN, dn));
    }

    private String getMatchingColumnNameForSortingParameter(String sortBy)
            throws OrganizationManagementClientException {

        if (sortBy == null) {
            return String.format(COLUMN_LOWER_WRAPPER, VIEW_NAME_COLUMN);
        }
        switch (sortBy.trim().toLowerCase(Locale.ENGLISH)) {
            case "name":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_NAME_COLUMN);
            case "displayname":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_DISPLAY_NAME_COLUMN);
            case "description":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_DESCRIPTION_COLUMN);
            case "created":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_CREATED_TIME_COLUMN);
            case "lastmodified":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_LAST_MODIFIED_COLUMN);
            case "status":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_STATUS_COLUMN);
            case "parentname":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_PARENT_NAME_COLUMN);
            case "parentdisplayname":
                return String.format(COLUMN_LOWER_WRAPPER, VIEW_PARENT_DISPLAY_NAME_COLUMN);
            default:
                throw handleClientException(LIST_REQUEST_INVALID_SORT_PARAMETER,
                        "Allowed : [ASC | DESC] and 'sortBy' [name | description | displayName | status | "
                                + "lastModified | created | parentName | parentDisplayName], Provided : " + sortBy);
        }
    }

    private void validateOrganizationPatchOperations(List<Operation> operations, String organizationId)
            throws OrganizationManagementException {

        for (Operation operation : operations) {
            // Validate op
            if (StringUtils.isBlank(operation.getOp())) {
                throw handleClientException(PATCH_REQUEST_OPERATION_UNDEFINED, null);
            }
            String op = operation.getOp().trim().toLowerCase(Locale.ENGLISH);
            if (!(PATCH_OP_ADD.equals(op) || PATCH_OP_REMOVE.equals(op) || PATCH_OP_REPLACE.equals(op))) {
                throw handleClientException(PATCH_REQUEST_INVALID_OPERATION,
                        "Patch op must be one of ['add', 'replace', 'remove']. Provided : " + op);
            }

            // Validate path
            if (StringUtils.isBlank(operation.getPath())) {
                throw handleClientException(PATCH_REQUEST_PATH_UNDEFINED, null);
            }
            String path = operation.getPath().trim();
            // Set path to lower case
            if (path.toLowerCase(Locale.ENGLISH).startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                // Convert only the '/attributes/' part to lower case to treat the attribute name case sensitively
                path = path.replaceAll("(?i)" + Pattern.quote(PATCH_PATH_ORG_ATTRIBUTES), PATCH_PATH_ORG_ATTRIBUTES);
            } else if (path.equalsIgnoreCase(PATCH_PATH_ORG_DISPLAY_NAME)) {
                path = PATCH_PATH_ORG_DISPLAY_NAME;
            } else {
                path = path.toLowerCase(Locale.ENGLISH);
            }
            // Is valid path
            if (!(path.equals(PATCH_PATH_ORG_NAME) || path.equals(PATCH_PATH_ORG_DISPLAY_NAME) || path
                    .equals(PATCH_PATH_ORG_DESCRIPTION) || path.equals(PATCH_PATH_ORG_STATUS) || path
                    .equals(PATCH_PATH_ORG_PARENT_ID) || path.startsWith(PATCH_PATH_ORG_ATTRIBUTES))) {
                throw handleClientException(PATCH_REQUEST_INVALID_PATH,
                        "Provided path : " + path + " is invalid");
            }

            // Validate value
            String value;
            // Value is mandatory for Add and Replace operations
            if (StringUtils.isBlank(operation.getValue()) && !PATCH_OP_REMOVE.equals(op)) {
                throw handleClientException(PATCH_REQUEST_VALUE_UNDEFINED, null);
            } else {
                // Avoid NPEs down the road
                value = operation.getValue() != null ? operation.getValue().trim() : "";
            }
            // You can only remove attributes and non-mandatory fields (displayName, description) only
            if (PATCH_OP_REMOVE.equals(op) && !(path.startsWith(PATCH_PATH_ORG_ATTRIBUTES) || path
                    .equals(PATCH_PATH_ORG_DISPLAY_NAME) || path.equals(PATCH_PATH_ORG_DESCRIPTION))) {
                throw handleClientException(PATCH_REQUEST_INVALID_REMOVE_OPERATION,
                        "Cannot remove mandatory field : " + path);
            }
            // Mandatory fields can only be 'Replaced'
            if (!op.equals(PATCH_OP_REPLACE) && !(path.startsWith(PATCH_PATH_ORG_ATTRIBUTES) || path
                    .equals(PATCH_PATH_ORG_DISPLAY_NAME) || path.equals(PATCH_PATH_ORG_DESCRIPTION))) {
                throw handleClientException(PATCH_REQUEST_INVALID_REMOVE_OPERATION,
                        "Mandatory organization fields can only be replaced. Provided op : " + op + ", Path : " + path);
            }
            // STATUS may only contain 'ACTIVE' and 'DISABLED' values
            if (path.equals(PATCH_PATH_ORG_STATUS)) {
                try {
                    value = Organization.OrgStatus.valueOf(value.toUpperCase(Locale.ENGLISH)).toString();
                } catch (IllegalArgumentException e) {
                    throw handleClientException(PATCH_REQUEST_INVALID_STATUS,
                            "STATUS field could only contain 'ACTIVE' or 'DISABLED'. Provided : " + value);
                }
            }
            // You can't deactivate an organization which is having any ACTIVE users or ACTIVE child organizations.
            if (path.equals(PATCH_PATH_ORG_STATUS) && value.equals(Organization.OrgStatus.DISABLED.toString())) {
                if (hasActiveChildOrganizations(organizationId)) {
                    throw handleClientException(PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_CHILD, null);
                } else if (hasActiveUsers(organizationId, getTenantId())) {
                    throw handleClientException(PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_USERS, null);
                }
            }
            // You can't activate an organization, whom's parent organization is not in ACTIVE state.
            if (path.equals(PATCH_PATH_ORG_STATUS) && value.equals(Organization.OrgStatus.ACTIVE.toString())
                    && !canEnable(organizationId)) {
                throw handleClientException(PATCH_REQUEST_CANNOT_ACTIVATE_WITH_DISABLED_PARENT, null);
            }
            // Check if the new parent exist and ACTIVE before patching the '/parent/id' field
            if (path.equals(PATCH_PATH_ORG_PARENT_ID) && !(isOrganizationExistById(value)
                    && Organization.OrgStatus.ACTIVE.equals(getOrganization(value, false).getStatus()))) {
                throw handleClientException(PATCH_REQUEST_INVALID_PARENT, null);
            }
            // Check if the new organization name already exists
            if (path.equals(PATCH_PATH_ORG_NAME) && isOrganizationExistByName(value)) {
                throw handleClientException(PATCH_REQUEST_NAME_UNAVAILABLE, null);
            }
            if (path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                String attributeKey = path.replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
                // Attribute key can not be empty
                if (StringUtils.isBlank(attributeKey)) {
                    throw handleClientException(PATCH_REQUEST_ATTRIBUTE_KEY_UNDEFINED,
                            "Attribute key is not defined in the path : " + path);
                }
                boolean attributeExist = organizationMgtDao
                        .isAttributeExistByKey(getTenantId(), organizationId, attributeKey);
                // If attribute key to be added already exists, update its value
                if (op.equals(PATCH_OP_ADD) && attributeExist) {
                    op = PATCH_OP_REPLACE;
                }
                if (op.equals(PATCH_OP_REMOVE) && !attributeExist) {
                    throw handleClientException(PATCH_REQUEST_INVALID_ATTRIBUTE_KEY,
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
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_OPERATION_UNDEFINED, null);
            }
            String op = operation.getOp().trim().toLowerCase(Locale.ENGLISH);
            if (!PATCH_OP_REPLACE.equals(op)) {
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_OPERATION, null);
            }

            // Validate path
            if (StringUtils.isBlank(operation.getPath())) {
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_PATH_UNDEFINED, null);
            }
            String path = operation.getPath().trim().toUpperCase(Locale.ENGLISH);
            // Only the RDN can be patched
            if (!RDN.equalsIgnoreCase(path)) {
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_PATH,
                        "UserStore configuration patch may only have 'RDN' as path. Provided : " + path);
            }
            // Validate value
            // Value is mandatory for user store config patch operations
            if (StringUtils.isBlank(operation.getValue())) {
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_VALUE_UNDEFINED, null);
            }
            // Check if the RDN is available for this parent
            // Note, only the RDN can be patched
            String parentId = organizationMgtDao.getOrganization(getTenantId(), organizationId, null, false)
                    .getParent().getId();
            boolean isAvailable = organizationMgtDao.isRdnAvailable(operation.getValue(), parentId, getTenantId());
            if (!isAvailable) {
                throw handleClientException(PATCH_USER_STORE_CONFIGS_REQUEST_RDN_UNAVAILABLE,
                        "Directory " + operation.getValue() + " is not available for this parent");
            }
            operation.setOp(PATCH_OP_REPLACE);
            operation.setPath(path);
            operation.setValue(operation.getValue().trim());
        }
    }

    /**
     * Checks whether a given organization has any active child organizations.
     *
     * @param organizationId ID of the organization.
     * @return True if at least one active child organization found, false otherwise.
     * @throws OrganizationManagementException If any errors occurred.
     */
    private boolean hasActiveChildOrganizations(String organizationId) throws OrganizationManagementException {

        List<String> children = organizationMgtDao.getChildOrganizationIds(organizationId, null);
        for (String child : children) {
            Organization organization = getOrganization(child, false);
            if (organization.getStatus() == Organization.OrgStatus.ACTIVE) {
                if (log.isDebugEnabled()) {
                    log.debug("Active child organization detected : " + organization.getId());
                }
                return true;
            }
        }
        return false;
    }

    private boolean canEnable(String organizationId) throws OrganizationManagementException {

        Organization organization = getOrganization(organizationId, false);
        String parentId = organization.getParent().getId();
        if (ROOT.equals(organization.getName())) {
            return true;
        } else {
            // non-root organizations should have an ACTIVE parent to change to ACTIVE state.
            return Organization.OrgStatus.ACTIVE.equals(getOrganization(parentId, false).getStatus());
        }
    }

    private void createLdapDirectory(int tenantId, String userStoreDomain, String dn, String rdn)
            throws OrganizationManagementException {

        // TODO check if RDN is available first
        try {
            UserRealm tenantUserRealm = ((UserRealm) OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getTenantUserRealm(tenantId));
            if (tenantUserRealm == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                        "Error obtaining tenant realm for the tenant id : " + tenantId);
            }
            if (tenantUserRealm.getUserStoreManager() == null
                    || tenantUserRealm.getUserStoreManager().getSecondaryUserStoreManager(userStoreDomain) == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                        "Error obtaining user store manager for the domain : " + userStoreDomain + ", tenant id : "
                                + tenantId);
            }
            UserStoreManager userStoreManager = tenantUserRealm.getUserStoreManager()
                    .getSecondaryUserStoreManager(userStoreDomain);
            if (userStoreManager instanceof AbstractOrganizationMgtUserStoreManager) {
                ((AbstractOrganizationMgtUserStoreManager) userStoreManager).createOu(dn, rdn);
                if (log.isDebugEnabled()) {
                    log.debug("Created subdirectory : " + dn + ", in the user store domain : " + userStoreDomain);
                }
            } else {
                throw handleClientException(ADD_REQUEST_INCOMPATIBLE_USER_STORE_MANAGER,
                        "User store manager doesn't support creating new LDAP directories. Tenant id : " + tenantId
                                + ", Domain : " + userStoreDomain);
            }
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                    "Error creating the DN : " + dn + " in the user store domain : " + userStoreDomain, e);
        }
    }

    private void deleteLdapDirectory(int tenantId, String userStoreDomain, String dn)
            throws OrganizationManagementException {

        try {
            UserRealm tenantUserRealm = ((UserRealm) OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getTenantUserRealm(tenantId));
            if (tenantUserRealm == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_DELETE_ERROR,
                        "Error obtaining tenant realm for the tenant id : " + tenantId);
            }
            if (tenantUserRealm.getUserStoreManager() == null
                    || tenantUserRealm.getUserStoreManager().getSecondaryUserStoreManager(userStoreDomain) == null) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_DELETE_ERROR,
                        "Error obtaining user store manager for the domain : " + userStoreDomain + ", tenant id : "
                                + tenantId);
            }
            UserStoreManager userStoreManager = tenantUserRealm.getUserStoreManager()
                    .getSecondaryUserStoreManager(userStoreDomain);
            if (userStoreManager instanceof AbstractOrganizationMgtUserStoreManager) {
                ((AbstractOrganizationMgtUserStoreManager) userStoreManager).deleteOu(dn);
                if (log.isDebugEnabled()) {
                    log.debug("Removed subdirectory : " + dn + ", in the user store domain : " + userStoreDomain);
                }
            } else {
                throw handleClientException(DELETE_REQUEST_UNSUPPORTED_USER_STORE_MANAGER,
                        "User store manager doesn't support manipulating LDAP directories. Tenant id : " + tenantId
                                + ", Domain : " + userStoreDomain);
            }
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_DELETE_ERROR,
                    "Error deleting the DN : " + dn + " in the user store domain : " + userStoreDomain, e);
        }
    }

    private boolean isUserAuthorizedToCreateOrganization(String parentId) throws OrganizationManagementException {

        // If you are going to create an organization,
        // you should have '/permission/admin/organizations/create' over its parent organization
        OrganizationAuthorizationDao authorizationDao = new OrganizationAuthorizationDaoImpl();
        return authorizationDao.isUserAuthorized(getAuthenticatedUserId(), parentId, ORGANIZATION_CREATE_PERMISSION);
    }

    private void grantImmediateParentPermissions(String organizationId, String parentOrganizationId,
                                                 String newOrgCreatorId)
            throws OrganizationManagementException {

        /*
        Immediate parent's all 'inherit = true' role mappings and,
        all role mappings assigned to the new organization creator will be delegated to the new organization.
         */
        if (StringUtils.isNotEmpty(parentOrganizationId)) {
            String isCascadeInsert = System.getProperty(CASCADE_INSERT_USER_ORG_ROLES);
            if (isCascadeInsert == null || Boolean.parseBoolean(isCascadeInsert)) {
                authorizationDao.addOrganizationAndUserRoleMappings(organizationId, parentOrganizationId,
                        newOrgCreatorId, getTenantId());
            } else {
                List<OrganizationUserRoleMapping> organizationUserRoleMappingsOfParent = authorizationDao
                        .getDelegatingOrganizationUserRoleMappingsToNewOrg(parentOrganizationId, newOrgCreatorId,
                                getTenantId());
                List<OrganizationUserRoleMapping> organizationUserRoleMappingsForNewOrganization = new ArrayList<>();
                for (OrganizationUserRoleMapping mapping : organizationUserRoleMappingsOfParent) {
                    OrganizationUserRoleMapping organizationUserRoleMapping;
                    if (!mapping.isCascadedRole() && StringUtils.equals(newOrgCreatorId, mapping.getUserId())) {
                        /*
                         Delegate the role mappings that user creator has only against the immediate parent.
                         It will be added against new org, ASSIGNED_AT = new org id and INHERIT = false.
                         */
                        organizationUserRoleMapping = new OrganizationUserRoleMapping(
                                organizationId, mapping.getUserId(), mapping.getRoleId(), mapping.getHybridRoleId(),
                                mapping.isCascadedRole(), organizationId);
                    } else {
                        /*
                         Delegate the role mappings that users have  against the immediate parent with INHERIT = true.
                         */
                        organizationUserRoleMapping = new OrganizationUserRoleMapping(
                                organizationId, mapping.getUserId(), mapping.getRoleId(), mapping.getHybridRoleId(),
                                mapping.isCascadedRole(), mapping.getAssignedOrganizationLevel());
                    }
                    organizationUserRoleMappingsForNewOrganization.add(organizationUserRoleMapping);
                }
                // Defaults to SP when property is not available.
                    authorizationDao
                            .addOrganizationAndUserRoleMappings(organizationUserRoleMappingsForNewOrganization, getTenantId());
            }
        }
    }

    private void fireEvent(String eventName, String organizationId, Object data, Status status)
            throws OrganizationManagementException {

        IdentityEventService eventService = OrganizationMgtDataHolder.getInstance().getIdentityEventService();
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put(USER_NAME, getAuthenticatedUsername());
        eventProperties.put(USER_ID, getAuthenticatedUserId());
        eventProperties.put(TENANT_DOMAIN, getTenantDomain());
        eventProperties.put(STATUS, status);
        if (data != null) {
            eventProperties.put(DATA, data);
        }
        if (organizationId != null) {
            eventProperties.put(ORGANIZATION_ID, organizationId);
        }
        Event event = new Event(eventName, eventProperties);
        try {
            eventService.handleEvent(event);
        } catch (OrganizationManagementClientException e) {
            throw e;
        } catch (OrganizationManagementServerException e) {
            throw e;
        } catch (IdentityEventException e) {
            throw handleServerException(ERROR_CODE_EVENTING_ERROR, eventName, e);
        }
    }

    private boolean isAuthorizedAsAdmin() throws OrganizationManagementException {

        // Having this permission ('/permission/admin/manage/identity/organizationmgt/admin') assigned from the WSO2
        // default registry based permission model allows :  listing all the organizations, listing all the users,
        // listing all the groups and granting any permission to any user against any organization.
        try {
            AuthorizationManager authorizationManager = OrganizationMgtDataHolder.getInstance().
                    getRealmService().getTenantUserRealm(getTenantId()).getAuthorizationManager();
            return authorizationManager
                    .isUserAuthorized(getAuthenticatedUsername(), ORGANIZATION_ADMIN_PERMISSION, UI_EXECUTE);
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error while checking if an admin user", e);
        }
    }

    private void sanitizeUserStoreConfigKeys(OrganizationAdd organizationAdd) {

        List<UserStoreConfig> userStoreConfigs = organizationAdd.getUserStoreConfigs();
        for (int i = 0; i < userStoreConfigs.size(); i++) {
            UserStoreConfig config = userStoreConfigs.get(i);
            config.setKey(config.getKey().trim().toUpperCase(Locale.ENGLISH));
        }
    }

    private String getTenantDomain() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    }

    private int getTenantId() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    }

    private String getAuthenticatedUserId() throws OrganizationManagementServerException {

        return getUserIDFromUserName(getAuthenticatedUsername(), getTenantId());
    }

    private String getAuthenticatedUsername() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
    }
}
