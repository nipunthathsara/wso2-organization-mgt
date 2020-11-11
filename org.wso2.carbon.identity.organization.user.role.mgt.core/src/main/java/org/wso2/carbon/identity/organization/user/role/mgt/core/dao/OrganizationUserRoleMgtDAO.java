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

package org.wso2.carbon.identity.organization.user.role.mgt.core.dao;

import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;

import java.util.List;

/**
 * Organization User Role Mgt DAO.
 */
public interface OrganizationUserRoleMgtDAO {

    void addOrganizationUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings, int tenantID)
            throws OrganizationUserRoleMgtException;

    List<User> getUserIdsByOrganizationAndRole(String organizationID, String roleId, int offset, int limit,
                                               List<String> requestedAttributes, int tenantID, String filter)
            throws OrganizationUserRoleMgtServerException;

    void deleteOrganizationsUserRoleMapping(List<String> organizationIds, String userId, String roleId, int tenantId)
            throws OrganizationUserRoleMgtException;

    List<Role> getRolesByOrganizationAndUser(String organizationID, String userId, int tenantID)
            throws OrganizationUserRoleMgtServerException;

    boolean isOrganizationUserRoleMappingExists(String organizationId, String userId, String roleId,
                                                   int tenantId)
            throws OrganizationUserRoleMgtException;

    Integer getRoleIdBySCIMGroupName(String roleName, int tenantId) throws OrganizationUserRoleMgtServerException;
}
