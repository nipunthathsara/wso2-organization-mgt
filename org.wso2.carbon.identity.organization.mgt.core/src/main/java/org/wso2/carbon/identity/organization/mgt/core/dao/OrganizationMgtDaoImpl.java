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
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.util.JdbcUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.time.ZoneOffset.UTC;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INSERT_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

public class OrganizationMgtDaoImpl implements OrganizationMgtDao {

    private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC));

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException {

        Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            jdbcTemplate.executeInsert(INSERT_ORGANIZATION,
                    preparedStatement -> {
                        int parameterIndex = 1;
                        preparedStatement.setString(parameterIndex, organization.getId());
                        preparedStatement.setInt(++parameterIndex, organization.getTenantId());
                        preparedStatement.setString(++parameterIndex, organization.getName());
                        preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                        preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                        preparedStatement.setInt(++parameterIndex, organization.hasAttribute() ? 1 : 0);
                        preparedStatement.setInt(++parameterIndex, organization.getStatus() ? 1 : 0);
                        preparedStatement.setString(++parameterIndex, organization.getParentId());
                    },
                    organization,
                    false
            );
            if (organization.hasAttribute()) {
                insertOrganizationAttributes(jdbcTemplate, organization);
            }
            organization.setCreated(currentTime.toInstant().toString());
            organization.setLastModified(currentTime.toInstant().toString());
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_INSERT_ORGANIZATION_ERROR, "Name - " + organization.getName()
                    + " Tenant Id - " + organization.getTenantId(), e);
        }
    }

    @Override
    public void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException {

    }

    @Override
    public boolean isOrganizationExistByName(int tenantId, String name) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int orgCount = jdbcTemplate.fetchSingleRecord(CHECK_ORGANIZATION_EXIST_BY_NAME,
                    (resultSet, rowNumber) ->
                            resultSet.getInt(COUNT_COLUMN_NAME),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, name);
                    });
            return orgCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR,
                    "Name - " + name + " Tenant id - " + tenantId, e);
        }
    }

    @Override
    public boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            int orgCount = jdbcTemplate.fetchSingleRecord(CHECK_ORGANIZATION_EXIST_BY_ID,
                    (resultSet, rowNumber) ->
                            resultSet.getInt(COUNT_COLUMN_NAME),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, id);
                    }
            );
            return orgCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR,
                    "Id - " + id + " Tenant id - " + tenantId, e);
        }
    }

    @Override
    public Organization getOrganization(int tenantId, String organizationId) throws OrganizationManagementException {
        return null;
    }

    private void insertOrganizationAttributes(JdbcTemplate template, Organization organization)
            throws DataAccessException, OrganizationManagementClientException {

        String query = buildQueryForAttributes(organization);
        template.executeInsert(query,
                preparedStatement -> {
                    int parameterIndex = 0;
                    for (Attribute attribute : organization.getAttributes()) {
                        preparedStatement.setString(++parameterIndex, generateUniqueID());
                        preparedStatement.setString(++parameterIndex, organization.getId());
                        preparedStatement.setString(++parameterIndex, attribute.getKey());
                        preparedStatement.setString(++parameterIndex, attribute.getValue());
                    }
                },
                organization,
                false
        );
    }

    private String buildQueryForAttributes(Organization organization) throws OrganizationManagementClientException {

        return null;

    }
}
