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
import org.wso2.carbon.identity.organization.mgt.core.dao.CacheBackedOrganizationMgtDAO;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDaoImpl;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAO;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAOImpl;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMappingUser;
import org.wso2.carbon.identity.scim2.common.DAO.GroupDAO;
import org.wso2.carbon.identity.scim2.common.exceptions.IdentitySCIMException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ADD_NONE_INTERNAL_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ROLE_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleServerException;

/**
 * Organization User Role Manager Impl
 */
public class OrganizationUserRoleManagerImpl implements OrganizationUserRoleManager {

    @Override
    public void addOrganizationUserRoleMappings(String organizationId, UserRoleMapping userRoleMapping)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

//        validateAddRoleMappingRequest(organizationUserRoleMappings);
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

        //@TODO check for mapping existance
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
    }

    @Override
    public List<User> getUsersByOrganizationAndRole(String organizationID, String roleId, int offset, int limit,
                                                    List<String> requestedAttributes)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .getUserIdsByOrganizationAndRole(organizationID, roleId, offset, limit, requestedAttributes,
                        getTenantId());
    }

    @Override
    public void deleteOrganizationsUserRoleMapping(String organizationId, String userId, String roleId,
                                                   boolean includeSubOrgs)
            throws OrganizationUserRoleMgtException, OrganizationManagementException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        OrganizationMgtDao organizationMgtDao = new OrganizationMgtDaoImpl();
        CacheBackedOrganizationMgtDAO cacheBackedOrganizationMgtDAO =
                new CacheBackedOrganizationMgtDAO(organizationMgtDao);
        List<String> organizationListToBeDeleted = new ArrayList<>();
        organizationListToBeDeleted.add(organizationId);

        // Traverse the sub organizations and added as the organizations to be checked for deleting the mentioned role mapping.
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
                                                                                   int hybridRoleId,
                                                                                   List<UserRoleMappingUser> usersList) {

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
}
