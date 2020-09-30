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
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMapping;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ADD_USER_ROLE_ORG_MAPPING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_GROUP_ID_FROM_ROLE_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_HYBRID_ID_FROM_ROLE_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_AUTHORIZED_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.IS_USER_AUTHORIZED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_HYBRID_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getNewIdentityTemplate;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getNewTemplate;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

public class OrganizationAuthorizationDaoImpl implements OrganizationAuthorizationDao {

    @Override
    public boolean isUserAuthorized(String userId, String organizationId, String permission)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        String basePermission = permission.contains(USER_MGT_BASE_PERMISSION) ? USER_MGT_BASE_PERMISSION :
                (permission.contains(ROLE_MGT_BASE_PERMISSION) ? ROLE_MGT_BASE_PERMISSION :
                        (permission.contains(ORGANIZATION_BASE_PERMISSION) ? ORGANIZATION_BASE_PERMISSION : ""));
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

    @Override
    public OrganizationUserRoleMapping getAuthorizedUserRole(String userId, String organizationId, String permission)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            return jdbcTemplate.fetchSingleRecord(GET_USER_AUTHORIZED_ROLE, (resultSet, rowNumber) -> {
                OrganizationUserRoleMapping orgUserRoleMapping = new OrganizationUserRoleMapping();
                orgUserRoleMapping.setRoleId(resultSet.getString(UM_ROLE_ID_COLUMN));
                orgUserRoleMapping.setHybridRoleId(resultSet.getInt(UM_HYBRID_ROLE_ID_COLUMN));
                return orgUserRoleMapping;
            }, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setString(++parameterIndex, userId);
                preparedStatement.setString(++parameterIndex, organizationId);
                preparedStatement.setString(++parameterIndex, permission);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error obtaining authorized role information. User : " + userId + ", organization id : "
                            + organizationId + ", permission : " + permission, e);
        }
    }

    @Override
    public void addOrganizationAndUserRoleMapping(String userId, String roleId, int hybridRoleId, int tenantId,
            String organizationId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeInsert(ADD_USER_ROLE_ORG_MAPPING, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setString(++parameterIndex, generateUniqueID());
                preparedStatement.setString(++parameterIndex, userId);
                preparedStatement.setString(++parameterIndex, roleId);
                preparedStatement.setInt(++parameterIndex, hybridRoleId);
                preparedStatement.setInt(++parameterIndex, tenantId);
                preparedStatement.setString(++parameterIndex, organizationId);
            }, new OrganizationUserRoleMapping(), false);
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error while adding org-authorization mapping entry. Organization : " + organizationId
                            + ", userId : " + userId + ", roleId : " + roleId + ", hybridRoleId : " + hybridRoleId, e);
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
}
