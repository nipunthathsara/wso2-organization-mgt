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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.user.role.mgt.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants;
import org.wso2.carbon.identity.organization.mgt.core.dao.CacheBackedOrganizationMgtDAO;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMappingForEvent;
import org.wso2.carbon.identity.organization.mgt.core.model.UserRoleInheritance;
import org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAO;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAOImpl;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtClientException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.internal.OrganizationUserRoleMgtDataHolder;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.RoleMember;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMappingUser;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleOperation;
import org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils;
import org.wso2.carbon.identity.scim2.common.DAO.GroupDAO;
import org.wso2.carbon.identity.scim2.common.exceptions.IdentitySCIMException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.INSERT_ROLES_WITH_STORED_PROCEDURE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REPLACE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.DATA;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.ORGANIZATION_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_ASSIGN_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_REVOKE_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_ASSIGN_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_REVOKE_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.TENANT_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.USER_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.USER_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ADD_ORG_ROLE_USER_REQUEST_INVALID_USER;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ADD_ORG_ROLE_USER_REQUEST_MAPPING_EXISTS;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.DELETE_ORG_ROLE_USER_REQUEST_INVALID_DIRECT_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.DELETE_ORG_ROLE_USER_REQUEST_INVALID_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.INVALID_ROLE_ID;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.INVALID_ROLE_NON_INTERNAL_ROLE;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_INVALID_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_INVALID_OPERATION;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_INVALID_PATH;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_INVALID_VALUE;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_OPERATION_UNDEFINED;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_PATH_UNDEFINED;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.PATCH_ORG_ROLE_USER_REQUEST_TOO_MANY_OPERATIONS;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.getUserStoreManager;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleServerException;

/**
 * Organization User Role Manager Impl.
 */
public class OrganizationUserRoleManagerImpl implements OrganizationUserRoleManager {

    @Override
    public void addOrganizationUserRoleMappings(String organizationId, UserRoleMapping userRoleMapping)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

        // Fire pre-event.
        fireEvent(PRE_ASSIGN_ORGANIZATION_USER_ROLE, organizationId, null,
                OrganizationMgtEventConstants.Status.FAILURE);
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
        CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                new CacheBackedOrganizationMgtDAO(organizationMgtDao);
        String roleId = userRoleMapping.getRoleId();
        int hybridRoleId = getHybridRoleIdFromSCIMGroupId(roleId);
        userRoleMapping.setHybridRoleId(hybridRoleId);
        validateAddRoleMappingRequest(organizationId, userRoleMapping);
        List<UserRoleMappingUser> usersGetPermissionsForSubOrgs = new ArrayList<>();
        List<UserRoleMappingUser> usersGetPermissionOnlyToOneOrg = new ArrayList<>();
        AbstractUserStoreManager userStoreManger;
        try {
            userStoreManger = (AbstractUserStoreManager) getUserStoreManager(getTenantId());
            if (userStoreManger == null) {
                throw handleServerException(
                        OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_OPERATIONS_ERROR,
                        " for tenant Id " + getTenantId());
            }
            for (UserRoleMappingUser user : userRoleMapping.getUsers()) {
                boolean userExists = userStoreManger.isExistingUserWithID(user.getUserId());
                if (!userExists) {
                    throw handleClientException(ADD_ORG_ROLE_USER_REQUEST_INVALID_USER,
                            "No user exists with user ID: " + user.getUserId());
                }
                if (user.isCascadedRole()) {
                    usersGetPermissionsForSubOrgs.add(user);
                } else {
                    usersGetPermissionOnlyToOneOrg.add(user);
                }
            }
        } catch (UserStoreException e) {
            throw handleServerException(
                    OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_OPERATIONS_ERROR,
                    " for tenant Id " + getTenantId());
        }

        String useSpforInsert = System.getProperty(INSERT_ROLES_WITH_STORED_PROCEDURE);

        if (StringUtils.isNotBlank(useSpforInsert) && Boolean.parseBoolean(useSpforInsert)) {
            organizationUserRoleMgtDAO.addOrganizationUserRoleMappingsWithSp(usersGetPermissionsForSubOrgs, roleId,
                    hybridRoleId, getTenantId(), organizationId);
        } else {
            List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
            // Get child organizations and add role mappings.
            if (CollectionUtils.isNotEmpty(usersGetPermissionsForSubOrgs)) {
                List<String> childOrganizationList = cacheBackedOrganizationMgtDAO.
                        getAllOfChildOrganizationIds(organizationId);
                // Add starting organization populate role mapping for that.
                organizationUserRoleMappings
                        .addAll(populateOrganizationUserRoleMappings(organizationId, roleId, hybridRoleId, organizationId,
                                usersGetPermissionsForSubOrgs));
                for (String childOrg : childOrganizationList) {
                    organizationUserRoleMappings
                            .addAll(populateOrganizationUserRoleMappings(childOrg, roleId, hybridRoleId, organizationId,
                                    usersGetPermissionsForSubOrgs));
                }
            }
            // Populate role mappings for non-cascading assignments.
            organizationUserRoleMappings
                    .addAll(populateOrganizationUserRoleMappings(organizationId, roleId, hybridRoleId, organizationId,
                            usersGetPermissionOnlyToOneOrg));
            organizationUserRoleMgtDAO
                    .addOrganizationUserRoleMappings(organizationUserRoleMappings, getTenantId());
        }
        // Fire post-event.
        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForEvent =
                new OrganizationUserRoleMappingForEvent(organizationId, roleId, userRoleMapping.getUsers().stream()
                        .map(m -> new UserRoleInheritance(m.getUserId(), m.isCascadedRole()))
                        .collect(Collectors.toList()));
        fireEvent(POST_ASSIGN_ORGANIZATION_USER_ROLE, organizationId, organizationUserRoleMappingForEvent,
                OrganizationMgtEventConstants.Status.SUCCESS);
    }

    @Override
    public List<RoleMember> getUsersByOrganizationAndRole(String organizationID, String roleId, int offset, int limit,
                                                          List<String> requestedAttributes, String filter)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .getUserIdsByOrganizationAndRole(organizationID, roleId, offset, limit, requestedAttributes,
                        getTenantId(), filter);
    }

    @Override
    public void patchOrganizationsUserRoleMapping(String organizationId, String roleId,
                                                  String userId, List<UserRoleOperation> userRoleOperations)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

        boolean operationValue = userRoleOperations.get(0).getValue();
        if (userRoleOperations.size() != 1) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_TOO_MANY_OPERATIONS, null);
        }
        validatePatchOperation(userRoleOperations.get(0));
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        int directlyAssignedRoleMappingsInheritance = organizationUserRoleMgtDAO
                .getDirectlyAssignedOrganizationUserRoleMappingInheritance(organizationId, userId, roleId,
                        getTenantId());
        // Check whether directly assigned role mapping exists, only if the update request is valid.
        if (directlyAssignedRoleMappingsInheritance == -1) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_INVALID_MAPPING, null);
        }
        // No change required. includeSubOrgs value of existing role mapping is same as the requested change.
        if (directlyAssignedRoleMappingsInheritance == (operationValue ? 1 : 0)) {
            return;
        }
        // Update is required.
        OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
        CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                new CacheBackedOrganizationMgtDAO(organizationMgtDao);
        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        List<String> organizationListToBeDeleted = new ArrayList<>();
        List<String> childOrganizationList = cacheBackedOrganizationMgtDAO.
                getAllOfChildOrganizationIds(organizationId);
        int hybridRoleId = getHybridRoleIdFromSCIMGroupId(roleId);
        for (String childOrg : childOrganizationList) {
            if (operationValue) {
                List<UserRoleMappingUser> userRoleMappings = new ArrayList<>();
                userRoleMappings.add(new UserRoleMappingUser(userId, operationValue));
                organizationUserRoleMappings
                        .addAll(populateOrganizationUserRoleMappings(childOrg, roleId, hybridRoleId, organizationId,
                                userRoleMappings));
            } else {
                organizationListToBeDeleted.add(childOrg);
            }
        }
        organizationUserRoleMgtDAO
                .updateIncludeSubOrgProperty(organizationId, roleId, userId, userRoleOperations.get(0).getValue(),
                        organizationUserRoleMappings, organizationListToBeDeleted, getTenantId());
    }

    @Override
    public void deleteOrganizationsUserRoleMapping(String organizationId, String userId, String roleId,
                                                   String assignedLevel, boolean includeSubOrg,
                                                   boolean checkInheritance)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

        // Fire pre-event.
        fireEvent(PRE_REVOKE_ORGANIZATION_USER_ROLE, organizationId, null,
                OrganizationMgtEventConstants.Status.FAILURE);
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        boolean roleMappingExists = organizationUserRoleMgtDAO
                .isOrganizationUserRoleMappingExists(organizationId, userId, roleId, assignedLevel, includeSubOrg,
                        checkInheritance, getTenantId());
        if (!roleMappingExists) {
            throw handleClientException(DELETE_ORG_ROLE_USER_REQUEST_INVALID_MAPPING,
                    String.format("No organization user role mapping found for organization: %s, user: %s, role: %s",
                            organizationId, roleId, userId));
        }

        /*
         Check whether the role mapping is directly assigned to the particular organization or inherited from the
         parent level.
         */
        int directlyAssignedRoleMappingsInheritance = organizationUserRoleMgtDAO
                .getDirectlyAssignedOrganizationUserRoleMappingInheritance(organizationId, userId, roleId,
                        getTenantId());
        if (directlyAssignedRoleMappingsInheritance == -1) {
            throw handleClientException(DELETE_ORG_ROLE_USER_REQUEST_INVALID_DIRECT_MAPPING,
                    String.format("No directly assigned organization user role mapping found for organization: %s, " +
                                    "user: %s, role: %s, directly assigned at organization: %s",
                            organizationId, userId, roleId, organizationId));
        }

        /*
        directlyAssignedRoleMappingsInheritance should be 1(inherit = true) or 0(inherit = false) or
        -1(no directly assigned role mapping).
        If returns 0, no need to check for role mapping in child organization. It is a directly assigned role
        mapping with include sub-org = false.
         */
        List<String> organizationListToBeDeleted = new ArrayList<>();
        if (directlyAssignedRoleMappingsInheritance == 1) {
            OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
            CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                    new CacheBackedOrganizationMgtDAO(organizationMgtDao);
            organizationListToBeDeleted = cacheBackedOrganizationMgtDAO.
                    getAllOfChildOrganizationIds(organizationId);
            organizationListToBeDeleted.add(organizationId);
        } else {
            organizationListToBeDeleted.add(organizationId);
        }
        organizationUserRoleMgtDAO
                .deleteOrganizationsUserRoleMapping(organizationId, organizationListToBeDeleted, userId, roleId,
                        getTenantId());
        // Fire post-event.
        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForEvent =
                new OrganizationUserRoleMappingForEvent(organizationId, roleId, userId);
        fireEvent(POST_REVOKE_ORGANIZATION_USER_ROLE, organizationId, organizationUserRoleMappingForEvent,
                OrganizationMgtEventConstants.Status.SUCCESS);
    }

    @Override
    public void deleteOrganizationsUserRoleMappings(String userId) throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        organizationUserRoleMgtDAO.deleteOrganizationsUserRoleMappings(userId, getTenantId());
    }

    @Override
    public List<Role> getRolesByOrganizationAndUser(String organizationId, String userId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO.getRolesByOrganizationAndUser(organizationId, userId, getTenantId());
    }

    @Override
    public boolean isOrganizationUserRoleMappingExists(String organizationId, String userId, String roleId,
                                                       String assignedLevel, boolean includeSubOrg,
                                                       boolean checkInheritance)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .isOrganizationUserRoleMappingExists(organizationId, userId, roleId, assignedLevel, includeSubOrg,
                        checkInheritance, getTenantId());
    }

    private int getTenantId() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    }

    private List<OrganizationUserRoleMapping> populateOrganizationUserRoleMappings(String organizationId, String roleId,
            int hybridRoleId, String assignedAt, List<UserRoleMappingUser> usersList) {

        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        for (UserRoleMappingUser user : usersList) {
            OrganizationUserRoleMapping organizationUserRoleMapping = new OrganizationUserRoleMapping();
            organizationUserRoleMapping.setOrganizationId(organizationId);
            organizationUserRoleMapping.setRoleId(roleId);
            organizationUserRoleMapping.setHybridRoleId(hybridRoleId);
            organizationUserRoleMapping.setUserId(user.getUserId());
            organizationUserRoleMapping.setAssignedLevelOrganizationId(assignedAt);
            organizationUserRoleMapping.setCascadedRole(user.isCascadedRole());
            organizationUserRoleMappings.add(organizationUserRoleMapping);
        }
        return organizationUserRoleMappings;
    }

    private void fireEvent(String eventName, String organizationId, Object data,
                           OrganizationMgtEventConstants.Status status) throws OrganizationUserRoleMgtServerException {

        IdentityEventService eventService = OrganizationUserRoleMgtDataHolder.getInstance().getIdentityEventService();
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
        } catch (IdentityEventException e) {
            throw handleServerException(OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_EVENTING_ERROR,
                    eventName, e);
        }
    }

    private String getTenantDomain() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    }

    private String getAuthenticatedUserId() throws OrganizationUserRoleMgtServerException {

        return Utils.getUserIDFromUserName(getAuthenticatedUsername(), getTenantId());
    }

    private String getAuthenticatedUsername() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
    }

    private void validateAddRoleMappingRequest(String organizationId, UserRoleMapping userRoleMapping)
            throws OrganizationUserRoleMgtException {

        for (UserRoleMappingUser user : userRoleMapping.getUsers()) {
            boolean roleMappingExists =
                    isOrganizationUserRoleMappingExists(organizationId, user.getUserId(), userRoleMapping.getRoleId(),
                            organizationId, user.isCascadedRole(), false);
            if (roleMappingExists) {
                throw handleClientException(ADD_ORG_ROLE_USER_REQUEST_MAPPING_EXISTS, String.format(
                        "Directly assigned role %s to user: %s over the organization: %s is already exists",
                        userRoleMapping.getRoleId(), user.getUserId(), organizationId));
            }
        }
    }

    private void validatePatchOperation(UserRoleOperation userRoleOperation)
            throws OrganizationUserRoleMgtClientException {

        // Validate op.
        if (StringUtils.isBlank(userRoleOperation.getOp())) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_OPERATION_UNDEFINED, null);
        }
        String op = userRoleOperation.getOp().trim().toLowerCase(Locale.ENGLISH);
        if (!PATCH_OP_REPLACE.equals(op)) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_INVALID_OPERATION, null);
        }

        // Validate path.
        if (StringUtils.isBlank(userRoleOperation.getPath())) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_PATH_UNDEFINED, null);
        }
        if (!StringUtils.equals("/includeSubOrgs", userRoleOperation.getPath())) {
            throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_INVALID_PATH, null);
        }

        // Validate value.
        if (!userRoleOperation.getValue() || userRoleOperation.getValue()) {
            return;
        }
        throw handleClientException(PATCH_ORG_ROLE_USER_REQUEST_INVALID_VALUE, null);
    }

    private int getHybridRoleIdFromSCIMGroupId(String roleId) throws OrganizationUserRoleMgtException {

        GroupDAO groupDAO = new GroupDAO();
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        try {
            String groupName = groupDAO.getGroupNameById(getTenantId(), roleId);
            if (groupName == null) {
                throw handleClientException(INVALID_ROLE_ID, "Invalid role ID : " + roleId);
            }
            String[] groupNameParts = groupName.split("/");
            if (groupNameParts.length != 2) {
                throw handleServerException(INVALID_ROLE_ID, "Invalid role ID. Group name : " + groupName);
            }
            String domain = groupNameParts[0];
            if (!"INTERNAL".equalsIgnoreCase(domain)) {
                throw handleClientException(INVALID_ROLE_NON_INTERNAL_ROLE,
                        "Provided role : " + groupName + ", is not an INTERNAL role");
            }
            String roleName = groupNameParts[1];
            return organizationUserRoleMgtDAO.getRoleIdBySCIMGroupName(roleName, getTenantId());
        } catch (IdentitySCIMException e) {
            throw new OrganizationUserRoleMgtServerException(e);
        }
    }
}
