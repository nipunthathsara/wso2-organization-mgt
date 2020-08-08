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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.util.JdbcUtils;
import org.wso2.carbon.identity.organization.mgt.core.util.Utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_DELETE_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INSERT_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_QUERY_LENGTH_EXCEEDED_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_ALL_ORG_IDS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_USER_STORE_CONFIGS_BY_ORG_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_ORGANIZATION_BY_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ATTR_ATTR_KEY_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ATTR_ATTR_VALUE_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_ATTRIBUTES_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_DIRECTORY_INFO_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_CHILD_ORG_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_STORE_CONFIGS_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES_CONCLUDE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_OR_UPDATE_USER_STORE_CONFIG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORDER_BY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_CREATED_TIME_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_HAS_ATTRIBUTE_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_LAST_MODIFIED_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_NAME_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_PARENT_ID_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORG_STATUS_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PAGINATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getMaximumQueryLengthInBytes;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

public class OrganizationMgtDaoImpl implements OrganizationMgtDao {

    private static final Log log = LogFactory.getLog(OrganizationMgtDaoImpl.class);
    private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC));

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
                        preparedStatement.setString(++parameterIndex, organization.getDescription());
                        preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                        preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                        preparedStatement.setInt(++parameterIndex, organization.hasAttributes() ? 1 : 0);
                        preparedStatement.setInt(++parameterIndex, organization.isActive() ? 1 : 0);
                        preparedStatement.setString(++parameterIndex, organization.getParentId());
                    },
                    organization,
                    false
            );
            if (organization.hasAttributes()) {
                insertOrganizationAttributes(jdbcTemplate, organization);
            }
            insertOrUpdateUserStoreConfigs(jdbcTemplate, organization);
            organization.setCreated(currentTime.toInstant().toString());
            organization.setLastModified(currentTime.toInstant().toString());
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_INSERT_ORGANIZATION_ERROR, "Name - " + organization.getName()
                    + " Tenant Id - " + organization.getTenantId(), e);
        }
    }

    @Override
    public void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            // Delete organization from IDN_ORG table
            jdbcTemplate.executeUpdate(DELETE_ORGANIZATION_BY_ID,
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
            // Delete organization attributes
            jdbcTemplate.executeUpdate(DELETE_ATTRIBUTES_BY_ORG_ID,
                    preparedStatement -> preparedStatement.setString(1, organizationId)
            );
            // Delete RDN and DN entries for the organization
            jdbcTemplate.executeUpdate(DELETE_DIRECTORY_INFO_BY_ORG_ID,
                    preparedStatement -> preparedStatement.setString(1, organizationId)
            );
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_DELETE_ORGANIZATION_ERROR, "Id - " + organizationId, e);
        }
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

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        List<OrganizationRowDataCollector> organizationRowDataCollectors;
        try {
            //TODO query is done, do the rest.
            organizationRowDataCollectors = jdbcTemplate.executeQuery(GET_ORGANIZATION_BY_ID,
                    (resultSet, rowNumber) -> {
                        OrganizationRowDataCollector collector = new OrganizationRowDataCollector();
                        collector.setId(organizationId);
                        collector.setTenantId(tenantId);
                        collector.setName(resultSet.getString(ORG_NAME_COLUMN_NAME));
                        collector.setCreatedTime(resultSet.getTimestamp(ORG_CREATED_TIME_COLUMN_NAME, calendar));
                        collector.setLastModified(resultSet.getTimestamp(ORG_LAST_MODIFIED_COLUMN_NAME, calendar));
                        collector.setHasAttribute(resultSet.getInt(ORG_HAS_ATTRIBUTE_COLUMN_NAME) == 1 ? true : false);
                        collector.setStatus(resultSet.getInt(ORG_STATUS_COLUMN_NAME) == 1 ? true : false);
                        collector.setParentId(resultSet.getString(ORG_PARENT_ID_COLUMN_NAME));
                        collector.setAttributeKey(resultSet.getString(ATTR_ATTR_KEY_COLUMN_NAME));
                        collector.setAttributeValue(resultSet.getString(ATTR_ATTR_VALUE_COLUMN_NAME));
//                        collector.setDn(resultSet.getString(UM_DN_COLUMN_NAME));
//                        collector.setRdn(resultSet.getString(UM_RDN_COLUMN_NAME));
                        return collector;
                    },
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, organizationId);
                        preparedStatement.setInt(++parameterIndex, tenantId);
                    }
            );
            Organization organization = (organizationRowDataCollectors == null || organizationRowDataCollectors.size() == 0) ?
                    null : buildOrganizationFromRawData(organizationRowDataCollectors);
            return findChildOrganizations(jdbcTemplate, organization);
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVE_ORGANIZATION_BY_ID_ERROR, organizationId, e);
        }
    }

    @Override
    public Map<String, UserStoreConfig> getUserStoreConfigsByOrgId(int tenantId, String organizationId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            List<UserStoreConfig> userStoreConfigs = jdbcTemplate.executeQuery(GET_USER_STORE_CONFIGS_BY_ORG_ID,
                    (resultSet, rowNumber) -> {
                        UserStoreConfig config = new UserStoreConfig();
                        config.setId(resultSet.getString(VIEW_CONFIG_ID));
                        config.setKey(resultSet.getString(VIEW_CONFIG_KEY));
                        config.setValue(resultSet.getString(VIEW_CONFIG_VALUE));
                        return config;
                    },
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
            System.out.println("gg");
            return userStoreConfigs.stream().collect(Collectors.toMap(UserStoreConfig::getKey, config -> config));

        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVE_USER_STORE_CONFIGS_BY_ORG_ID_ERROR, organizationId, e);
        }
    }

    @Override
    public List<Organization> getOrganizations(int tenantId, int offset, int limit, String sortBy, String sortOrder)
            throws OrganizationManagementException {

        boolean paginationReq = offset >= 0 && limit > 0;
        // Ascending if not specified otherwise.
        sortOrder = sortOrder != null && "DESC".equals(sortOrder.trim().toUpperCase()) ? "DESC" : "ASC";
        boolean sortingReq = sortBy != null;

        StringBuilder sb = new StringBuilder();
        sb.append(GET_ALL_ORGANIZATION_IDS);
        if (sortingReq) {
            sb.append(String.format(ORDER_BY, sortBy, sortOrder));
        }
        if (paginationReq) {
            sb.append(String.format(PAGINATION, offset, limit));
        }
        String query = sb.toString();

        // Get organization IDs
        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        List<String> orgIds;
        try {
            orgIds = jdbcTemplate.executeQuery(query,
                    (resultSet, rowNumber) ->
                            resultSet.getString(VIEW_ID),
                    preparedStatement -> {
                        preparedStatement.setInt(1, tenantId);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVE_ALL_ORG_IDS_ERROR,
                    "Error while retrieving organization IDs. ", e);
        }
        return null;
    }

    private void insertOrganizationAttributes(JdbcTemplate template, Organization organization)
            throws DataAccessException, OrganizationManagementClientException {

        String query = buildQueryForAttributes(organization);
        template.executeInsert(query,
                preparedStatement -> {
                    int parameterIndex = 0;
                    for (Map.Entry<String, Attribute> entry : organization.getAttributes().entrySet()) {
                        preparedStatement.setString(++parameterIndex, generateUniqueID());
                        preparedStatement.setString(++parameterIndex, organization.getId());
                        preparedStatement.setString(++parameterIndex, entry.getValue().getKey());
                        preparedStatement.setString(++parameterIndex, entry.getValue().getValue());
                    }
                },
                organization,
                false
        );
    }

    private void insertOrUpdateUserStoreConfigs(JdbcTemplate template, Organization organization)
            throws DataAccessException {

        for (Map.Entry<String, UserStoreConfig> entry : organization.getUserStoreConfigs().entrySet()) {
            template.executeInsert(INSERT_OR_UPDATE_USER_STORE_CONFIG,
                    preparedStatement -> {
                        int parameterIndex = 0;
                        // On update, unique ID and Org ID will not be updated
                        preparedStatement.setString(++parameterIndex, generateUniqueID());
                        preparedStatement.setString(++parameterIndex, organization.getId());
                        preparedStatement.setString(++parameterIndex, entry.getValue().getKey());
                        preparedStatement.setString(++parameterIndex, entry.getValue().getValue());
                    },
                    organization,
                    false
            );
        }
    }

    private Organization findChildOrganizations(JdbcTemplate template, Organization organization)
            throws DataAccessException {

        template.executeQuery(FIND_CHILD_ORG_IDS,
                (resultSet, rowNumber) -> {
//                    organization.getChildren().add(resultSet.getString(ORG_ID_COLUMN_NAME));
                    return null;
                },
                preparedStatement -> {
                    preparedStatement.setString(1, organization.getId());
                });
        return organization;
    }

    private String buildQueryForAttributes(Organization organization) throws OrganizationManagementClientException {

        StringBuilder sb = new StringBuilder();
        sb.append(INSERT_ATTRIBUTES);
        for (int i = 0; i < organization.getAttributes().size(); i++) {
            sb.append(INSERT_ATTRIBUTE);
        }
        // Multiple insertions require a 'SELECT 1 FROM Dual' at the end
        sb.append(INSERT_ATTRIBUTES_CONCLUDE);
        if (sb.toString().getBytes().length > getMaximumQueryLengthInBytes()) {
            if (log.isDebugEnabled()) {
                log.debug("Error building SQL query for the attribute insert. Number of attributes: " +
                        organization.getAttributes().size() + " exceeds the maximum query length: " +
                        MAX_QUERY_LENGTH_IN_BYTES_SQL);
            }
            throw Utils.handleClientException(ERROR_CODE_QUERY_LENGTH_EXCEEDED_ERROR, null);
        }
        return sb.toString();
    }

    private Organization buildOrganizationFromRawData(List<OrganizationRowDataCollector> organizationRowDataCollectors) {

        Organization organization = new Organization();
        organizationRowDataCollectors.forEach(collector -> {
            if (organization.getId() == null) {
                organization.setId(collector.getId());
                organization.setName(collector.getName());
                organization.setTenantId(collector.getTenantId());
                organization.setCreated(collector.getCreatedTime().toString());
                organization.setLastModified(collector.getLastModified().toString());
                organization.setParentId(collector.getParentId());
                organization.setHasAttributes(collector.hasAttribute());
                organization.setActive(collector.isStatus());
//                organization.setRdn(collector.getRdn());
//                organization.setDn(collector.getDn());
            }
//            if (organization.hasAttribute() && !organization.getAttributes().contains(collector.getAttributeKey())) {
//                organization.getAttributes()
//                        .add(new Attribute(collector.getAttributeKey(), collector.getAttributeValue()));
//            }
        });
        return organization;
    }
}
