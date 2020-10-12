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

import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;

import java.util.List;

/**
 * Organization and user role manager service interface.
 */
public interface OrganizationUserRoleManager {

    void addOrganizationAndUserRoleMappings(String organizationId, List<UserRoleMapping> userRoleMappings)
            throws OrganizationUserRoleMgtException;

    void patchOrganizationAndUserRoleMapping(String organizationId, List<Operation> operations)
            throws OrganizationUserRoleMgtException;

    List<User> getUsersByOrganizationAndRole(String organizationID, String roleId)
            throws OrganizationUserRoleMgtException;

    void deleteOrganizationAndUserRoleMapping(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException;

    List<Role> getRolesByOrganizationAndUser(String organizationId, String userId)
            throws OrganizationUserRoleMgtException;

    boolean isOrganizationAndUserRoleMappingExists(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException;
}
