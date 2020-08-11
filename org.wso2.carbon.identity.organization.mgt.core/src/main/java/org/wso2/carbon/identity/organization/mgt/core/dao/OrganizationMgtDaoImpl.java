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

import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_DELETE_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INSERT_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_QUERY_LENGTH_EXCEEDED_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_ORGANIZATIONS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_USER_STORE_CONFIGS_BY_ORG_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVE_ORGANIZATION_BY_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_RETRIEVING_CHILD_ORGANIZATION_IDS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_ATTRIBUTES_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_DIRECTORY_INFO_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_CHILD_ORG_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATIONS_BY_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_STORE_CONFIGS_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES_CONCLUDE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_OR_UPDATE_USER_STORE_CONFIG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORDER_BY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PAGINATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ACTIVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_HAS_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getMaximumQueryLengthInBytes;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
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
            organizationRowDataCollectors = jdbcTemplate.executeQuery(GET_ORGANIZATION_BY_ID,
                    (resultSet, rowNumber) -> {
                        OrganizationRowDataCollector collector = new OrganizationRowDataCollector();
                        collector.setId(organizationId);
                        collector.setName(resultSet.getString(VIEW_NAME));
                        collector.setDescription(resultSet.getString(VIEW_DESCRIPTION));
                        collector.setParentId(resultSet.getString(VIEW_PARENT_ID));
                        collector.setActive(resultSet.getInt(VIEW_ACTIVE) == 1 ? true : false);
                        collector.setLastModified(resultSet.getTimestamp(VIEW_LAST_MODIFIED, calendar));
                        collector.setCreated(resultSet.getTimestamp(VIEW_CREATED_TIME, calendar));
                        collector.setHasAttributes(resultSet.getInt(VIEW_HAS_ATTRIBUTES) == 1 ? true : false);
                        collector.setAttributeId(resultSet.getString(VIEW_ATTR_ID));
                        collector.setAttributeKey(resultSet.getString(VIEW_ATTR_KEY));
                        collector.setAttributeValue(resultSet.getString(VIEW_ATTR_VALUE));
                        return collector;
                    },
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                    }
            );
            return (organizationRowDataCollectors == null || organizationRowDataCollectors.size() == 0) ?
                    null : buildOrganizationFromRawData(organizationRowDataCollectors);
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
        List<Organization> organizations = new ArrayList<>();
        try {
            orgIds = jdbcTemplate.executeQuery(query,
                    (resultSet, rowNumber) ->
                            resultSet.getString(VIEW_ID),
                    preparedStatement -> {
                        preparedStatement.setInt(1, tenantId);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVE_ORGANIZATIONS_ERROR,
                    "Error while retrieving organization IDs. ", e);
        }
        if (orgIds.isEmpty()) {
            return organizations;
        }

        // Get organizations by IDs
        query = GET_ORGANIZATIONS_BY_IDS;
        StringJoiner sj = new StringJoiner(",");
        for (String id : orgIds) {
            sj.add("'" + id + "'");
        }
        // Can not perform this in a prepared statement due to character escaping.
        // This query only expects a list of organization IDs(server generated) to be retrieved. Hence, no security vulnerability.
        query = query.replace("?", sj.toString());
        validateQueryLength(query);
        try {
            organizations = jdbcTemplate.executeQuery(query,
                    (resultSet, rowNumber) -> {
                        Organization organization = new Organization();
                        organization.setId(resultSet.getString(VIEW_ID));
                        organization.setName(resultSet.getString(VIEW_NAME));
                        organization.setDescription(resultSet.getString(VIEW_DESCRIPTION));
                        organization.setParentId(resultSet.getString(VIEW_PARENT_ID));
                        organization.setActive(resultSet.getInt(VIEW_ACTIVE) == 1 ? true : false);
                        organization.setLastModified(resultSet.getTimestamp(VIEW_LAST_MODIFIED, calendar).toString());
                        organization.setCreated(resultSet.getTimestamp(VIEW_CREATED_TIME, calendar).toString());
                        return organization;
                    });
            return organizations;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVE_ORGANIZATIONS_ERROR, "Error while constructing organizations by IDs", e);
        }
    }

    public List<String> getChildOrganizationIds(String organizationId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = JdbcUtils.getNewTemplate();
        try {
            List<String> childOrganizationIds = jdbcTemplate.executeQuery(FIND_CHILD_ORG_IDS,
                    (resultSet, rowNumber) -> resultSet.getString(VIEW_ID),
                    preparedStatement -> {
                        preparedStatement.setString(1, organizationId);
                    });
            return childOrganizationIds;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_RETRIEVING_CHILD_ORGANIZATION_IDS_ERROR, organizationId, e);
        }
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
                organization.setDescription(collector.getDescription());
                organization.setParentId(collector.getParentId());
                organization.setActive(collector.isActive());
                organization.setLastModified(collector.getLastModified().toString());
                organization.setCreated(collector.getCreated().toString());
                organization.setHasAttributes(collector.hasAttributes());
            }
            if (organization.hasAttributes() && !organization.getAttributes().containsKey(collector.getAttributeKey())) {
                organization.getAttributes()
                        .put(collector.getAttributeKey(),
                                new Attribute(collector.getAttributeKey(), collector.getAttributeValue()));
            }
        });
        return organization;
    }

    private void validateQueryLength(String query) throws OrganizationManagementClientException {
        if (query.getBytes().length > getMaximumQueryLengthInBytes()) {
            if (log.isDebugEnabled()) {
                log.debug("Error building SQL query. Get organizations expression " +
                        "query length: " + query.length() + " exceeds the maximum limit: " +
                        MAX_QUERY_LENGTH_IN_BYTES_SQL);
            }
            throw handleClientException(ERROR_CODE_RETRIEVE_ORGANIZATIONS_ERROR, "Query length exceeded the maximum limit.");
        }
    }
}
