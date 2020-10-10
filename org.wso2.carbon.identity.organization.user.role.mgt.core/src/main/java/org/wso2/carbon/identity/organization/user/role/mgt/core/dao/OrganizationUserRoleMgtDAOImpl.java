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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_HYBRID_ROLE_ID_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ROLES_BY_ORG_AND_USER;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ROLE_ID_BY_SCIM_GROUP_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_USERS_BY_ORG_AND_ROLE;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.INSERT_ALL;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.SELECT_DUMMY_RECORD;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.VIEW_ID_COLUMN;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.VIEW_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.VIEW_ROLE_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.VIEW_USER_ID_COLUMN;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.getNewTemplate;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.handleServerException;

/**
 * Organization User Role Mgt DAO Implementation.
 */
public class OrganizationUserRoleMgtDAOImpl implements OrganizationUserRoleMgtDAO {

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public void addOrganizationAndUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings,
                                                   Integer tenantID)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        String orgId = organizationUserRoleMappings.get(0).getOrganizationId();
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
                        }
                    }, organizationUserRoleMappings, false);
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR, orgId, e);
        }
    }

    @Override
    public List<User> getUserIdsByOrganizationAndRole(String organizationId, String roleId, Integer tenantID)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        List<String> userIds = null;
        List<User> users = new ArrayList<>();
        try {
            userIds = jdbcTemplate.executeQuery(GET_USERS_BY_ORG_AND_ROLE,
                    (resultSet, rowNumber) ->
                            resultSet.getString(VIEW_USER_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, organizationId);
                        preparedStatement.setString(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantID);
                    });
            for (String userId : userIds) {
                AbstractUserStoreManager userStoreManager =
                        (AbstractUserStoreManager) Utils.getUserStoreManager(tenantID);
                org.wso2.carbon.user.core.common.User user = userStoreManager.getUser(userId, null);
                users.add(new User(user.getUserID(), user.getUsername()));
            }
        } catch (UserStoreException e) {
            //TODO
            throw new OrganizationUserRoleMgtServerException(e);
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR), roleId,
                            organizationId);
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR.getCode(), e);

        }
        return users;
    }

    @Override
    public void deleteOrganizationAndUserRoleMapping(String organizationId, String userId, String roleId,
                                                     Integer tenantId) throws OrganizationUserRoleMgtException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(DELETE_ORGANIZATION_USER_ROLE_MAPPING,
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setString(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR), roleId,
                            userId, organizationId);
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR.getCode(), e);
        }
    }

    @Override
    public List<Role> getRolesByOrganizationAndUser(String organizationId, String userId, Integer tenantID)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        List<Role> roles;
        try {
            roles = jdbcTemplate.executeQuery(GET_ROLES_BY_ORG_AND_USER,
                    (resultSet, rowNumber) -> new Role(resultSet.getString(VIEW_ROLE_ID_COLUMN),
                            "Internal/" + resultSet.getString(VIEW_ROLE_NAME_COLUMN)),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, organizationId);
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setInt(++parameterIndex, tenantID);
                    });
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR), userId,
                            organizationId);
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR.getCode(), e);
        }

        return roles;
    }

    @Override
    public boolean isOrganizationAndUserRoleMappingExists(String organizationId, String userId, String roleId,
                                                          Integer tenantId)
            throws OrganizationUserRoleMgtException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        int mappingsCount = 0;
        try {
            mappingsCount = jdbcTemplate.fetchSingleRecord(GET_ORGANIZATION_USER_ROLE_MAPPING,
                    (resultSet, rowNumber) ->
                            resultSet.getInt(COUNT_COLUMN_NAME),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setString(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR), roleId,
                            userId, organizationId);
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR.getCode(), e);
        }
        return mappingsCount > 0;
    }

    @Override
    public Integer getRoleIdBySCIMGroupName(String roleName, Integer tenantId)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            return jdbcTemplate.fetchSingleRecord(GET_ROLE_ID_BY_SCIM_GROUP_NAME,
                    (resultSet, rowNumber) ->
                            resultSet.getInt(VIEW_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, roleName);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                    });
        } catch (DataAccessException e) {
           throw handleServerException(ERROR_CODE_HYBRID_ROLE_ID_RETRIEVING_ERROR, roleName);
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
