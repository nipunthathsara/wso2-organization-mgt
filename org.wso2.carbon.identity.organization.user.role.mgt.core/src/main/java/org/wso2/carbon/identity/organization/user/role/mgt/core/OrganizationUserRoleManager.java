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

import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;

import java.util.List;


/**
 * Organization and user role manager service interface.
 */
public interface OrganizationUserRoleManager {

    void addOrganizationUserRoleMappings(String organizationId, UserRoleMapping userRoleMappings)
            throws OrganizationUserRoleMgtException, OrganizationManagementException;

    List<User> getUsersByOrganizationAndRole(String organizationID, String roleId, int offset, int limit,
                                             List<String> requestedAttributes, String filter)
            throws OrganizationUserRoleMgtException;

    void deleteOrganizationsUserRoleMapping(String organizationId, String userId, String roleId, boolean includeSubOrgs)
            throws OrganizationUserRoleMgtException, OrganizationManagementException;

    List<Role> getRolesByOrganizationAndUser(String organizationId, String userId)
            throws OrganizationUserRoleMgtException;

    boolean isOrganizationUserRoleMappingExists(String organizationId, String userId, String roleId)
            throws OrganizationUserRoleMgtException;
}
