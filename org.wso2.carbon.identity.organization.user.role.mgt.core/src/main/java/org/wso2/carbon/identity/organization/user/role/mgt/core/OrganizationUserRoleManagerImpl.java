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
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.internal.OrganizationUserRoleMgtDataHolder;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMappingUser;
import org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils;
import org.wso2.carbon.identity.scim2.common.DAO.GroupDAO;
import org.wso2.carbon.identity.scim2.common.exceptions.IdentitySCIMException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

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
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ADD_NONE_INTERNAL_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ROLE_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleServerException;

/**
 * Organization User Role Manager Impl.
 */
public class OrganizationUserRoleManagerImpl implements OrganizationUserRoleManager {

    @Override
    public void addOrganizationUserRoleMappings(String organizationId, UserRoleMapping userRoleMapping)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

//        validateAddRoleMappingRequest(organizationUserRoleMappings);
        // Fire pre-event.
        fireEvent(PRE_ASSIGN_ORGANIZATION_USER_ROLE, organizationId, null,
                OrganizationMgtEventConstants.Status.FAILURE);
        GroupDAO groupDAO = new GroupDAO();
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
        CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                new CacheBackedOrganizationMgtDAO(organizationMgtDao);
        String roleId = userRoleMapping.getRoleId();
        int hybridRoleId;

        try {
            String groupName = groupDAO.getGroupNameById(getTenantId(), roleId);
            if (groupName == null) {
                throw handleClientException(ERROR_CODE_INVALID_ROLE_ERROR, userRoleMapping.getRoleId());
            }
            String[] groupNameParts = groupName.split("/");
            if (groupNameParts.length != 2) {
                throw handleServerException(ERROR_CODE_INVALID_ROLE_ERROR, groupName);
            }
            String domain = groupNameParts[0];
            if (!"INTERNAL".equalsIgnoreCase(domain)) {
                throw handleClientException(ERROR_CODE_ADD_NONE_INTERNAL_ERROR, groupName);
            }
            String roleName = groupNameParts[1];
            hybridRoleId = organizationUserRoleMgtDAO.getRoleIdBySCIMGroupName(roleName, getTenantId());
            userRoleMapping.setHybridRoleId(hybridRoleId);
        } catch (IdentitySCIMException e) {
            throw new OrganizationUserRoleMgtServerException(e);
        }
        List<UserRoleMappingUser> usersGetPermissionsForSubOrgs = new ArrayList<>();
        List<UserRoleMappingUser> usersGetPermissionOnlyToOneOrg = new ArrayList<>();
        for (UserRoleMappingUser user : userRoleMapping.getUsers()) {
            if (user.isCascadedRole()) {
                usersGetPermissionsForSubOrgs.add(user);
            } else {
                usersGetPermissionOnlyToOneOrg.add(user);
            }
        }

        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        // Get child organizations and add role mappings.
        if (CollectionUtils.isNotEmpty(usersGetPermissionsForSubOrgs)) {
            Queue<String> organizationList = new LinkedList<>();
            // Add starting organization populate role mapping for that.
            organizationList.add(organizationId);
            organizationUserRoleMappings
                    .addAll(populateOrganizationUserRoleMappings(organizationId, roleId, hybridRoleId,
                            usersGetPermissionsForSubOrgs));
            while (!organizationList.isEmpty()) {
                String currentOrgId = organizationList.remove();
                List<String> children = cacheBackedOrganizationMgtDAO.getChildOrganizationIds(currentOrgId, null);
                for (String childOrg : children) {
                    organizationList.add(childOrg);
                    organizationUserRoleMappings
                            .addAll(populateOrganizationUserRoleMappings(childOrg, roleId, hybridRoleId,
                                    usersGetPermissionsForSubOrgs));
                }
            }
        }
        // Populate role mappings for non-cascading assignments.
        organizationUserRoleMappings.addAll(populateOrganizationUserRoleMappings(organizationId, roleId, hybridRoleId,
                usersGetPermissionOnlyToOneOrg));
        organizationUserRoleMgtDAO.addOrganizationUserRoleMappings(organizationUserRoleMappings, getTenantId());
        // Fire post-event.
        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForEvent =
                new OrganizationUserRoleMappingForEvent(organizationId, roleId, userRoleMapping.getUsers().stream()
                        .map(m -> new UserRoleInheritance(m.getUserId(), m.isCascadedRole()))
                        .collect(Collectors.toList()));
        fireEvent(POST_ASSIGN_ORGANIZATION_USER_ROLE, organizationId, organizationUserRoleMappingForEvent,
                OrganizationMgtEventConstants.Status.SUCCESS);
    }

    @Override
    public List<User> getUsersByOrganizationAndRole(String organizationID, String roleId, int offset, int limit,
                                                    List<String> requestedAttributes, String filter)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .getUserIdsByOrganizationAndRole(organizationID, roleId, offset, limit, requestedAttributes,
                        getTenantId(), filter);
    }

    @Override
    public void deleteOrganizationsUserRoleMapping(String organizationId, String userId, String roleId,
                                                   boolean includeSubOrgs)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

        // Fire pre-event.
        fireEvent(PRE_REVOKE_ORGANIZATION_USER_ROLE, organizationId, null,
                OrganizationMgtEventConstants.Status.FAILURE);
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
        CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                new CacheBackedOrganizationMgtDAO(organizationMgtDao);
        List<String> organizationListToBeDeleted = new ArrayList<>();
        organizationListToBeDeleted.add(organizationId);

        /*
        Traverse the sub organizations and added as the organizations to be checked for deleting the
        mentioned role mapping.
         */
        if (includeSubOrgs) {
            Queue<String> organizationsList = new LinkedList<>();
            // Add starting organization.
            organizationsList.add(organizationId);
            while (!organizationsList.isEmpty()) {
                String currentOrgId = organizationsList.remove();
                List<String> children = cacheBackedOrganizationMgtDAO.getChildOrganizationIds(currentOrgId, null);
                for (String childOrg : children) {
                    organizationsList.add(childOrg);
                    organizationListToBeDeleted.add(childOrg);
                }
            }
        }
        organizationUserRoleMgtDAO
                .deleteOrganizationsUserRoleMapping(organizationListToBeDeleted, userId, roleId, getTenantId());
        // Fire post-event.
        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForEvent =
                new OrganizationUserRoleMappingForEvent(organizationId, roleId, userId, includeSubOrgs);
        fireEvent(POST_REVOKE_ORGANIZATION_USER_ROLE, organizationId, organizationUserRoleMappingForEvent,
                OrganizationMgtEventConstants.Status.SUCCESS);
    }

    @Override
    public List<Role> getRolesByOrganizationAndUser(String organizationId, String userId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO.getRolesByOrganizationAndUser(organizationId, userId, getTenantId());
    }

    @Override
    public boolean isOrganizationUserRoleMappingExists(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .isOrganizationUserRoleMappingExists(organizationId, userId, roleId, getTenantId());
    }

    private int getTenantId() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    }

    private List<OrganizationUserRoleMapping> populateOrganizationUserRoleMappings(String organizationId, String roleId,
            int hybridRoleId, List<UserRoleMappingUser> usersList) {

        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        for (UserRoleMappingUser user : usersList) {
            OrganizationUserRoleMapping organizationUserRoleMapping = new OrganizationUserRoleMapping();
            organizationUserRoleMapping.setOrganizationId(organizationId);
            organizationUserRoleMapping.setRoleId(roleId);
            organizationUserRoleMapping.setHybridRoleId(hybridRoleId);
            organizationUserRoleMapping.setUserId(user.getUserId());
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
}
