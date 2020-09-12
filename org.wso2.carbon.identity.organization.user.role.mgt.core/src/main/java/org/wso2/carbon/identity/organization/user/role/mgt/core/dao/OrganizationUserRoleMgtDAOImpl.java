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

import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.util.Utils;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;

import java.util.List;

import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_AND_USER_ROLE_MAPPING_ADD_ERROR;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.GET_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.INSERT_ALL;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.INSERT_ORGANIZATION_USER_ROLE_MAPPING;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.constant.SQLConstants.SELECT_DUMMY_RECORD;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.user.role.mgt.core.util.Utils.getNewTemplate;

public class OrganizationUserRoleMgtDAOImpl implements OrganizationUserRoleMgtDAO {

    @Override
    public void addOrganizationAndUserRoleMappings(List<OrganizationUserRoleMapping> organizationUserRoleMappings,
                                                   Integer tenantID)
            throws OrganizationUserRoleMgtServerException {

        JdbcTemplate jdbcTemplate = getNewTemplate();

        try {
            jdbcTemplate.executeInsert(buildQueryForMultipleInserts(organizationUserRoleMappings.size()),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        for (OrganizationUserRoleMapping organizationUserRoleMapping : organizationUserRoleMappings) {
                            preparedStatement.setString(++parameterIndex, generateUniqueID());
                            preparedStatement.setString(++parameterIndex, organizationUserRoleMapping.getUserId());
                            preparedStatement.setInt(++parameterIndex, organizationUserRoleMapping.getRoleId());
                            preparedStatement.setInt(++parameterIndex, tenantID);
                            preparedStatement
                                    .setString(++parameterIndex, organizationUserRoleMapping.getOrganizationId());
                        }
                    }, organizationUserRoleMappings, false);
        } catch (DataAccessException e) {
            String message = String.format(String.valueOf(ERROR_CODE_ORGANIZATION_AND_USER_ROLE_MAPPING_ADD_ERROR));
            throw new OrganizationUserRoleMgtServerException(message,
                    ERROR_CODE_ORGANIZATION_AND_USER_ROLE_MAPPING_ADD_ERROR.getCode(), e);
        }
    }

    @Override
    public List<Organization> getOrganizationsByUserAndRole(String userId, Integer roleId) {

        return null;
    }

    @Override
    public List<String> getUserIdsByOrganizationAndRole(String organizationID, Integer roleId) {

        return null;
    }

    @Override
    public void deleteOrganizationAndUserRoleMapping(String organizationId, String userId, Integer roleId,
                                                     Integer tenantId) {

        JdbcTemplate jdbcTemplate = Utils.getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(DELETE_ORGANIZATION_USER_ROLE_MAPPING,
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setInt(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
        } catch (DataAccessException e) {
            //@TODO add error
        }
    }

    @Override
    public boolean isOrganizationAndUserRoleMappingExists(String organizationId, String userId, Integer roleId,
                                                          Integer tenantId) {

        JdbcTemplate jdbcTemplate = Utils.getNewTemplate();
        try {
            int mappingsCount = jdbcTemplate.fetchSingleRecord(GET_ORGANIZATION_USER_ROLE_MAPPING,
                    (resultSet, rowNumber) ->
                            resultSet.getInt(COUNT_COLUMN_NAME),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, userId);
                        preparedStatement.setInt(++parameterIndex, roleId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    }
                                                              );
            return mappingsCount > 0;
        } catch (DataAccessException e) {
            //@TODO add error
            return false;
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
