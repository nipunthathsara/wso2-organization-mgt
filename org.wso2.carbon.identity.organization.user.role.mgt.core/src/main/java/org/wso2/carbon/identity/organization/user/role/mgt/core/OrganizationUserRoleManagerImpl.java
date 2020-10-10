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

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAO;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAOImpl;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;
import org.wso2.carbon.identity.scim2.common.DAO.GroupDAO;
import org.wso2.carbon.identity.scim2.common.exceptions.IdentitySCIMException;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ADD_NONE_INTERNAL_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ROLE_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleServerException;

/**
 * Organization User Role Manager Impl
 */
public class OrganizationUserRoleManagerImpl implements OrganizationUserRoleManager {

    @Override
    public void addOrganizationAndUserRoleMappings(String organizationId, List<UserRoleMapping> userRoleMappings)
            throws OrganizationUserRoleMgtException {

//        validateAddRoleMappingRequest(organizationUserRoleMappings);
        GroupDAO groupDAO = new GroupDAO();
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();

        for (UserRoleMapping userRoleMapping : userRoleMappings) {
            try {
                String groupName = groupDAO.getGroupNameById(getTenantId(), userRoleMapping.getRoleId());
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
                Integer hybridRoleId = organizationUserRoleMgtDAO.getRoleIdBySCIMGroupName(roleName, getTenantId());
                userRoleMapping.setHybridRoleId(hybridRoleId);
            } catch (IdentitySCIMException e) {
                throw new OrganizationUserRoleMgtServerException(e);
            }
        }

        //@TODO check for mapping existance
        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        for (UserRoleMapping userRoleMapping : userRoleMappings) {
            for (String userID : userRoleMapping.getUserIds()) {
                OrganizationUserRoleMapping organizationUserRoleMapping = new OrganizationUserRoleMapping();
                organizationUserRoleMapping.setOrganizationId(organizationId);
                organizationUserRoleMapping.setRoleId(userRoleMapping.getRoleId());
                organizationUserRoleMapping.setHybridRoleId(userRoleMapping.getHybridRoleId());
                organizationUserRoleMapping.setUserId(userID);
                organizationUserRoleMappings.add(organizationUserRoleMapping);
            }
        }

        organizationUserRoleMgtDAO.addOrganizationAndUserRoleMappings(organizationUserRoleMappings, getTenantId());
    }

    @Override
    public void patchOrganizationAndUserRoleMapping(String organizationId, List<Operation> operations)
            throws OrganizationUserRoleMgtException {

    }

    @Override
    public List<User> getUsersByOrganizationAndRole(String organizationID, String roleId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO.getUserIdsByOrganizationAndRole(organizationID, roleId, getTenantId());
    }

    @Override
    public void deleteOrganizationAndUserRoleMapping(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        organizationUserRoleMgtDAO.deleteOrganizationAndUserRoleMapping(organizationId, userId, roleId, getTenantId());
    }

    @Override
    public List<Role> getRolesByOrganizationAndUser(String organizationId, String userId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO.getRolesByOrganizationAndUser(organizationId, userId, getTenantId());
    }

    @Override
    public boolean isOrganizationAndUserRoleMappingExists(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .isOrganizationAndUserRoleMappingExists(organizationId, userId, roleId, getTenantId());
    }

//    private void validateAddRoleMappingRequest(UserRoleMapping organizationUserRoleMapping) {
//
//        // Check required fields.
//        if (StringUtils.isBlank(organizationUserRoleMapping.getUserId())) {
//
//        }
//        if (StringUtils.isBlank(String.valueOf(organizationUserRoleMapping.getRoleId()))) {
//
//        }
//    }

    private int getTenantId() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    }
}
