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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationPermission;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMapping;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVING_AUTHORIZED_ORGANIZATION_LIST_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_SQL_QUERY_LIMIT_EXCEEDED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ADMIN_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_LIST_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_ROLE_MGT_CREATE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_ROLE_MGT_DELETE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_ROLE_MGT_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_GROUP_ID_FROM_ROLE_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_HYBRID_ID_FROM_ROLE_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_LIST_OF_AUTHORIZED_ORGANIZATION_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_ORGANIZATIONS_PERMISSIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_PERMISSIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_ROLE_ORG_MAPPINGS_DELEGATE_TO_NEW_ORG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ALL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.IS_USER_AUTHORIZED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.SELECT_DUMMY_RECORD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ASSIGNED_AT_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_HYBRID_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_INHERIT_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_RESOURCE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_UM_USER_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ORG_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.dissemblePermissionString;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getMaximumQueryLengthInBytes;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getNewIdentityTemplate;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getNewTemplate;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

/**
 * Authorization DAO for organization mgt.
 */
public class OrganizationAuthorizationDaoImpl implements OrganizationAuthorizationDao {

    private static final Log log = LogFactory.getLog(OrganizationAuthorizationDaoImpl.class);

    @Override
    public boolean isUserAuthorized(String userId, String organizationId, String permission)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        String basePermission = permission.contains(USER_MGT_BASE_PERMISSION) ? USER_MGT_BASE_PERMISSION :
                (permission.contains(ROLE_MGT_BASE_PERMISSION) ? ROLE_MGT_BASE_PERMISSION :
                        (permission.contains(ORGANIZATION_BASE_PERMISSION) ? ORGANIZATION_BASE_PERMISSION
                                : permission));
        try {
            int mappingsCount = jdbcTemplate
                    .fetchSingleRecord(IS_USER_AUTHORIZED, (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN),
                            preparedStatement -> {
                                int parameterIndex = 0;
                                preparedStatement.setString(++parameterIndex, userId);
                                preparedStatement.setString(++parameterIndex, organizationId);
                                preparedStatement.setString(++parameterIndex, permission);
                                preparedStatement.setString(++parameterIndex, basePermission);
                            });
            return mappingsCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error while checking if the user is authorized. User : " + userId + ", organization id : "
                            + organizationId + ", permission : " + permission, e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public void addOrganizationAndUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings,
                                                   int tenantID) throws OrganizationManagementServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeInsert(buildQueryForMultipleInserts(organizationUserRoleMappings.size()),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        for (OrganizationUserRoleMapping organizationUserRoleMapping : organizationUserRoleMappings) {
                            preparedStatement.setString(++parameterIndex, generateUniqueID());
                            preparedStatement.setString(++parameterIndex, organizationUserRoleMapping.getUserId());
                            preparedStatement.setString(++parameterIndex, organizationUserRoleMapping.getRoleId());
                            preparedStatement.setInt(++parameterIndex, organizationUserRoleMapping.getHybridRoleId());
                            preparedStatement.setInt(++parameterIndex, tenantID);
                            preparedStatement
                                    .setString(++parameterIndex, organizationUserRoleMapping.getOrganizationId());
                            preparedStatement.setString(++parameterIndex,
                                    organizationUserRoleMapping.getAssignedOrganizationLevel());
                            preparedStatement
                                    .setInt(++parameterIndex, organizationUserRoleMapping.isCascadedRole() ? 1 : 0);
                        }
                    }, organizationUserRoleMappings, false);
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error while adding org-authorization mapping entries.", e);
        }
    }

    public int findHybridRoleIdFromRoleName(String role, int tenantId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            return jdbcTemplate.fetchSingleRecord(FIND_HYBRID_ID_FROM_ROLE_NAME,
                    (resultSet, rowNumber) -> resultSet.getInt(UM_ID_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, role);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error obtaining UM id for the hybrid role : " + role + ", tenant id : " + tenantId, e);
        }
    }

    public String findGroupIdFromRoleName(String role, int tenantId) throws OrganizationManagementException {

        // This method is querying the Identity database.
        JdbcTemplate jdbcTemplate = getNewIdentityTemplate();
        try {
            return jdbcTemplate.fetchSingleRecord(FIND_GROUP_ID_FROM_ROLE_NAME,
                    (resultSet, rowNumber) -> resultSet.getString(ATTR_VALUE_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, role);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error obtaining group id for the hybrid role : " + role + ", tenant id : " + tenantId, e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public Map<String, List<String>> findUserPermissionsForOrganizations(JdbcTemplate template, String userId,
            List<String> organizationIds, boolean listAsAdmin) throws OrganizationManagementException {

        String query = GET_USER_ORGANIZATIONS_PERMISSIONS;
        StringJoiner sj = new StringJoiner(",");
        for (String id : organizationIds) {
            sj.add("'" + id + "'");
        }
        // Can not perform this in a prepared statement due to character escaping.
        // System generated list of organization IDs. No security concern here.
        query = query.replace("#", sj.toString());
        validateQueryLength(query);

        List<OrganizationPermission> permissions;
        try {
            permissions = template.executeQuery(query, (resultSet, rowNumber) -> {
                OrganizationPermission permission = new OrganizationPermission();
                permission.setOrganizationId(resultSet.getString(VIEW_ORG_ID_COLUMN));
                permission.setPermission(resultSet.getString(UM_RESOURCE_ID_COLUMN));
                return permission;
            }, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setString(++parameterIndex, userId);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ERROR,
                    "error collecting permissions for user : " + userId, e);
        }

        // Initiate an empty map to hold list of permissions against organization id
        Map<String, List<String>> userOrgPermissions = organizationIds.stream()
                .collect(Collectors.toMap(String::toString, permission -> new ArrayList<>()));
        // Populate the map with the results fetched from the db
        for (OrganizationPermission permission : permissions) {
            // Dissemble the base permissions to leaf permissions
            List<String> leafPermissions = dissemblePermissionString(permission.getPermission());
            // Add the leaf permissions to the map only if it's not already added
            leafPermissions.forEach(leafPermission -> {
                List<String> list = userOrgPermissions.get(permission.getOrganizationId());
                if (!list.contains(leafPermission)) {
                    list.add(leafPermission);
                }
            });
        }
        // If listing as admin, below permissions will be implicitly granted for all the organizations
        if (listAsAdmin) {
            userOrgPermissions.forEach((id, permissionsList) -> {
                if (!permissionsList.contains(ORGANIZATION_VIEW_PERMISSION)) {
                    permissionsList.add(ORGANIZATION_VIEW_PERMISSION);
                }
                if (!permissionsList.contains(USER_MGT_LIST_PERMISSION)) {
                    permissionsList.add(USER_MGT_LIST_PERMISSION);
                }
                if (!permissionsList.contains(USER_MGT_VIEW_PERMISSION)) {
                    permissionsList.add(USER_MGT_VIEW_PERMISSION);
                }
                if (!permissionsList.contains(ROLE_MGT_VIEW_PERMISSION)) {
                    permissionsList.add(ROLE_MGT_VIEW_PERMISSION);
                }
                if (!permissionsList.contains(ORGANIZATION_ADMIN_PERMISSION)) {
                    permissionsList.add(ORGANIZATION_ADMIN_PERMISSION);
                }
                if (!permissionsList.contains(USER_ROLE_MGT_VIEW_PERMISSION)) {
                    permissionsList.add(USER_ROLE_MGT_VIEW_PERMISSION);
                }
                if (!permissionsList.contains(USER_ROLE_MGT_CREATE_PERMISSION)) {
                    permissionsList.add(USER_ROLE_MGT_CREATE_PERMISSION);
                }
                if (!permissionsList.contains(USER_ROLE_MGT_DELETE_PERMISSION)) {
                    permissionsList.add(USER_ROLE_MGT_DELETE_PERMISSION);
                }
            });
        }
        return userOrgPermissions;
    }

    @Override
    public List<OrganizationUserRoleMapping> getDelegatingOrganizationUserRoleMappingsToNewOrg(
            String parentOrganizationId, String newOrganizationCreatorID, int tenantId)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            return jdbcTemplate.executeQuery(GET_USER_ROLE_ORG_MAPPINGS_DELEGATE_TO_NEW_ORG,
                    (resultSet, rowNumber) -> new OrganizationUserRoleMapping(parentOrganizationId,
                            resultSet.getString(UM_UM_USER_ID_COLUMN), resultSet.getString(UM_ROLE_ID_COLUMN),
                            resultSet.getInt(UM_HYBRID_ROLE_ID_COLUMN), resultSet.getInt(UM_INHERIT_COLUMN) == 1,
                            resultSet.getString(UM_ASSIGNED_AT_COLUMN)),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, parentOrganizationId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, newOrganizationCreatorID);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error obtaining organizationUserRole mappings for organization : " + parentOrganizationId +
                            ", tenant id : " + tenantId, e);
        }
    }

    @Override
    public List<String> findUserPermissions(JdbcTemplate template, String userId)
            throws OrganizationManagementException {

        List<String> permissions;
        try {
            permissions = template.executeQuery(GET_USER_PERMISSIONS, (resultSet, rowNumber) ->
                    resultSet.getString(UM_RESOURCE_ID_COLUMN), preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setString(++parameterIndex, userId);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ERROR,
                    "error collecting permissions for user : " + userId, e);
        }
        return permissions;
    }

    @Override
    public List<String> findAuthorizedOrganizationsList(String userId, int tenantId, String permission)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        String basePermission = permission.contains(USER_MGT_BASE_PERMISSION) ? USER_MGT_BASE_PERMISSION :
                (permission.contains(ROLE_MGT_BASE_PERMISSION) ? ROLE_MGT_BASE_PERMISSION :
                        (permission.contains(ORGANIZATION_BASE_PERMISSION) ? ORGANIZATION_BASE_PERMISSION :
                                permission));
        try {
            return jdbcTemplate.executeQuery(GET_LIST_OF_AUTHORIZED_ORGANIZATION_IDS,
                    (resultSet, rowNumber) ->
                            resultSet.getString(VIEW_ORG_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setString(++parameterIndex, basePermission);
                        preparedStatement.setString(++parameterIndex, permission);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVING_AUTHORIZED_ORGANIZATION_LIST_ERROR,
                    "userid : " + userId + ", tenantid : " + tenantId + ", permission : " + permission, e);
        }
    }

    private void validateQueryLength(String query) throws OrganizationManagementClientException {

        if (query.getBytes(StandardCharsets.UTF_8).length > getMaximumQueryLengthInBytes()) {
            if (log.isDebugEnabled()) {
                log.debug("Error building SQL query. Get organizations expression " + "query length: " + query.length()
                        + " exceeds the maximum limit: " + MAX_QUERY_LENGTH_IN_BYTES_SQL);
            }
            throw handleClientException(ERROR_CODE_SQL_QUERY_LIMIT_EXCEEDED,
                    "Query length exceeded the maximum limit.");
        }
    }

    private String buildQueryForMultipleInserts(Integer numberOfMapings) {

        StringBuilder sb = new StringBuilder();
        sb.append(INSERT_ALL);

        for (int i = 0; i < numberOfMapings; i++) {
            sb.append(INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING);
        }
        sb.append(SELECT_DUMMY_RECORD);
        return sb.toString();
    }
}
