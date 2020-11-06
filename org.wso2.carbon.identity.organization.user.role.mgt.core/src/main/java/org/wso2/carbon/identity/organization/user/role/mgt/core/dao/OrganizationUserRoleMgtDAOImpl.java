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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.usermgt.AbstractOrganizationMgtUserStoreManager;
import org.wso2.carbon.identity.organization.mgt.core.util.Utils;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.internal.OrganizationUserRoleMgtDataHolder;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Role;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.User;
import org.wso2.carbon.identity.scim2.common.impl.IdentitySCIMManager;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_HYBRID_ROLE_ID_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.AND;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_USER_ROLE_MAPPING_WITHOUT_ORG;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ROLES_BY_ORG_AND_USER;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ROLE_ID_BY_SCIM_GROUP_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_USERS_BY_ORG_AND_ROLE;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.OR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.ORG_ID_ADDING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.PAGINATION;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.UM_USER_ROLE_ORG_DATA;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.UNION_ALL;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.UPSERT_UM_USER_ROLE_ORG_BASE;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.UPSERT_UM_USER_ROLE_ORG_END;
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
    public void addOrganizationUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings,
                                                   int tenantID)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            // Will be added only if the particular mapping is not existing.
            jdbcTemplate.executeInsert(buildQueryForMultipleUpsert(organizationUserRoleMappings.size()),
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
            throw handleServerException(ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR, "", e);
        }
    }

    @SuppressFBWarnings({"SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING", "SIC_INNER_SHOULD_BE_STATIC_ANON"})
    @Override
    public List<User> getUserIdsByOrganizationAndRole(String organizationId, String roleId, int offset, int limit,
                                                          List<String> requestedAttributes, int tenantID)
            throws OrganizationUserRoleMgtServerException {

        boolean paginationReq = offset > -1 && limit > 0;
        JdbcTemplate jdbcTemplate = getNewTemplate();
        List<String> userIds = null;
        List<User> users = new ArrayList<>();

        // Build query.
        StringBuilder sb = new StringBuilder();
        // Base query.
        sb.append(GET_USERS_BY_ORG_AND_ROLE);
        if (paginationReq) {
            sb.append(String.format(PAGINATION, offset, limit));
        }

        try {
            userIds = jdbcTemplate.executeQuery(sb.toString(),
                    (resultSet, rowNumber) ->
                            resultSet.getString(VIEW_USER_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, organizationId);
                        preparedStatement.setString(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantID);
                    });
            for (String userId : userIds) {

                String userStoreDomain = Utils.getOrganizationManager().getUserStoreConfigs(organizationId)
                        .get(USER_STORE_DOMAIN).getValue();

                RealmConfiguration matchingRealmConfig = Utils.getMatchingRealmConfiguration(tenantID, userStoreDomain);
                if (matchingRealmConfig == null) {
                    throw handleServerException(ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR,
                            "Couldn't find realm configurations for the user store domain : " + userStoreDomain);
                }

                UserStoreManager userStoreManager = OrganizationUserRoleMgtDataHolder.getInstance()
                                .getRealmService().getUserRealm(matchingRealmConfig).getUserStoreManager();
                ((AbstractOrganizationMgtUserStoreManager) userStoreManager).doGetUserList(organizationId, userIds,
                        "DEFAULT", limit, offset);

                // Obtain the user store manager.
                UserManager userManager = IdentitySCIMManager.getInstance().getUserManager();
                // Create charon-SCIM user endpoint and hand-over the request.
                UserResourceManager userResourceManager = new UserResourceManager();
                SCIMResponse scimResponse =
                        userResourceManager.get(userId, userManager, requestedAttributes.stream().collect(
                                Collectors.joining(",")), null);
                Map<String, Object> attributes;
                ObjectMapper mapper = new ObjectMapper();
                attributes = mapper.readValue(scimResponse.getResponseMessage(),
                        new TypeReference<Map<String, Object>>() {
                        });
                users.add(new User(attributes));
            }
        } catch (CharonException | IOException e) {
            //TODO
            throw new OrganizationUserRoleMgtServerException(e);
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR), roleId,
                            organizationId);
            throw new OrganizationUserRoleMgtServerException(
                    message, ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR.getCode(), e);
        } catch (OrganizationManagementException | UserStoreException e) {
            //todo
        }
        return users;
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public void deleteOrganizationsUserRoleMapping(List<String> organizationIds, String userId, String roleId,
                                                     int tenantId) throws OrganizationUserRoleMgtException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(buildQueryForMultipleRoleMappingDeletion(organizationIds.size()),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setString(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        for (String organizationId: organizationIds) {
                            preparedStatement.setString(++parameterIndex, organizationId);
                        }
                    });
        } catch (DataAccessException e) {
            String message =
                    String.format(String.valueOf(ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR), roleId,
                            userId);
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR.getCode(), e);
        }
    }

    @Override
    public List<Role> getRolesByOrganizationAndUser(String organizationId, String userId, int tenantID)
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
    public boolean isOrganizationUserRoleMappingExists(String organizationId, String userId, String roleId,
                                                          int tenantId)
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
    public Integer getRoleIdBySCIMGroupName(String roleName, int tenantId)
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

    private String buildQueryForMultipleUpsert(int numberOfMapings) {

        StringBuilder sb = new StringBuilder();
        sb.append(UPSERT_UM_USER_ROLE_ORG_BASE);
        sb.append("(");

        for (int i = 0; i < numberOfMapings; i++) {
            sb.append(UM_USER_ROLE_ORG_DATA);
            if (i != numberOfMapings - 1) {
                sb.append(UNION_ALL);
            }
        }
        sb.append(")");
        sb.append(UPSERT_UM_USER_ROLE_ORG_END);
        return sb.toString();
    }

    private String buildQueryForMultipleRoleMappingDeletion(int numberOfOrganizations) {

        StringBuilder sb = new StringBuilder();
        sb.append(DELETE_ORGANIZATION_USER_ROLE_MAPPING_WITHOUT_ORG);
        sb.append(AND).append("(");
        for (int i = 0; i < numberOfOrganizations; i++) {
            sb.append(ORG_ID_ADDING);
            if (i != numberOfOrganizations - 1) {
                sb.append(OR);
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
