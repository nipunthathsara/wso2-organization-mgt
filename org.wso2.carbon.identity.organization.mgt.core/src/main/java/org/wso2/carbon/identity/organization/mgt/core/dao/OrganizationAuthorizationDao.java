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

import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMapping;

import java.util.List;
import java.util.Map;

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
     * Add multiple entries to the 'UM_USER_ROLE_ORG' table.
     *
     * @param organizationUserRoleMappings A list of organizationUserRole mappings.
     * @param tenantID                     Tenant id.
     * @throws OrganizationManagementException
     */
    void addOrganizationAndUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings,
                                            int tenantID) throws OrganizationManagementException;

    /**
     * Add multiple entries to the 'UM_USER_ROLE_ORG' table using stored procedures.
     *
     * @param organizationUserRoleMappings A list of organizationUserRole mappings.
     * @param tenantID                     Tenant id.
     * @throws OrganizationManagementException
     */
    default void addOrganizationAndUserRoleMappingsWithSp (
            List<OrganizationUserRoleMapping> organizationUserRoleMappings, int tenantID)
            throws OrganizationManagementServerException {

    }
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

    /**
     * Find permissions for the authenticated user over the provided list of organizations.
     *
     * @param template
     * @param userId
     * @param organizationIds
     * @param listAsAdmin
     * @return
     * @throws OrganizationManagementException
     */
    Map<String, List<String>> findUserPermissionsForOrganizations(JdbcTemplate template, String userId,
                                                                  List<String> organizationIds, boolean listAsAdmin)
            throws OrganizationManagementException;

    /**
     * Get the organization user role mappings need to de delegated to a newly created organization.
     * This will return all inherit = true role mappings for any user and any role mapping assigned to
     * new organization creator.
     *
     * @param parentOrganizationId     Organization id of the parent organization id.
     * @param newOrganizationCreatorID User id of the new organization creator.
     * @param tenantId                 Tenant id.
     * @return A list of organization for the given organization.
     * @throws OrganizationManagementException
     */
    List<OrganizationUserRoleMapping> getDelegatingOrganizationUserRoleMappingsToNewOrg(String parentOrganizationId,
                                                                                        String newOrganizationCreatorID,
                                                                                        int tenantId)
            throws OrganizationManagementException;

    /**
     * Find permissions for the authenticated user over all the organizations.
     *
     * @param template JDBC template to be used.
     * @param userId   Authenticated user's ID.
     * @return List of permissions assigned to the user across all organizations.
     * @throws OrganizationManagementException if any errors occurred.
     */
    List<String> findUserPermissions(JdbcTemplate template, String userId)
            throws OrganizationManagementException;

    /**
     * Get list of organization IDs/Names, over which the provided user has the provided permission.
     *
     * @param userId        User id.
     * @param tenantId      Tenant id.
     * @param permission    Permission string
     * @param listByName    return list of authorized organization names(Defaults to organization list of IDs)
     * @return              List of IDs/Names of the authorized organizations.
     * @throws OrganizationManagementException
     */
    List<String> findAuthorizedOrganizationsList(String userId, int tenantId, String permission, boolean listByName)
            throws OrganizationManagementException;
}
