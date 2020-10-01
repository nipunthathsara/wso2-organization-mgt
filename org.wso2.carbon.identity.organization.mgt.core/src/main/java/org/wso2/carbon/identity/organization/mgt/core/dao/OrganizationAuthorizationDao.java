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

package org.wso2.carbon.identity.organization.mgt.core.dao;

import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMapping;

/**
 * This interface is used to query 'UM_USER_ROLE_ORG' table and 'ORG_AUTHZ_VIEW' view which are also being accessed
 * by the 'OrganizationUserRoleMgtDAO' interface of the 'org.wso2.carbon.identity.organization.user.role.mgt.core'
 * module.
 */
public interface OrganizationAuthorizationDao {

    /**
     * Check if the user is granted with the provided permission over the provided organization
     *
     * @param userId
     * @param organizationId
     * @param permission
     * @return
     * @throws OrganizationManagementException
     */
    boolean isUserAuthorized(String userId, String organizationId, String permission)
            throws OrganizationManagementException;

    /**
     * Add an entry to the 'UM_USER_ROLE_ORG' table.
     *
     * @param userId
     * @param roleId
     * @param hybridRoleId
     * @param tenantId
     * @param organizationId
     * @throws OrganizationManagementException
     */
    void addOrganizationAndUserRoleMapping(String userId, String roleId, int hybridRoleId, int tenantId,
            String organizationId) throws OrganizationManagementException;

    /**
     * Find the 'UM_ID' by 'UM_ROLE_NAME' from the 'UM_HYBRID_ROLE' table.
     *
     * @param role
     * @param tenantId
     * @return
     * @throws OrganizationManagementException
     */
    int findHybridRoleIdFromRoleName(String role, int tenantId) throws OrganizationManagementException;

    /**
     * Find the 'ID' (SCIM group id) from the 'IDN_SCIM_GROUP' table using the role name.
     *
     * @param role
     * @param tenantId
     * @return
     * @throws OrganizationManagementException
     */
    String findGroupIdFromRoleName(String role, int tenantId) throws OrganizationManagementException;
}
