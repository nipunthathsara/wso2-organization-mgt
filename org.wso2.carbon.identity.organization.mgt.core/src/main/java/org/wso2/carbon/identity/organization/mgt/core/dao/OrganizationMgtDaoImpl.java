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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.database.utils.jdbc.exceptions.TransactionException;
import org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.exception.PrimitiveConditionValidationException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Metadata;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationSearchBean;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.identity.organization.mgt.core.search.PlaceholderSQL;
import org.wso2.carbon.identity.organization.mgt.core.search.PrimitiveConditionValidator;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType.PrimitiveOperator.ENDS_WITH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType.PrimitiveOperator.STARTS_WITH;
import static org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType.PrimitiveOperator.SUBSTRING;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.DN_PLACE_HOLDER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_NAME_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_CHECK_RDN_AVAILABILITY_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_DELETE_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_BY_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_CHILDREN_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_CONFIGS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_PATCH_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.LIST_REQUEST_FILTER_TOO_LONG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.LIST_REQUEST_INVALID_FILTER_PARAMETER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_ADD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REMOVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REPLACE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_DISPLAY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.RDN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ATTRIBUTE_EXIST_BY_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORGANIZATION_EXIST_BY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_ORG_HAS_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.CHECK_RDN_AVAILABILITY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.COUNT_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DEFAULT_CONDITION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.DELETE_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_AUTHORIZED_CHILD_ORG_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.FIND_CHILD_ORG_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_AUTHORIZATION_ORGANIZATION_IDS_WITH_JOIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATIONS_BY_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATION_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ORGANIZATION_ID_BY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ROLE_IDS_FOR_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ROLE_IDS_FOR_PERMISSION_WITHOUT_VIEW;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_STORE_CONFIGS_BY_ORG_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_USER_STORE_CONFIGS_BY_ORG_ID_WITHOUT_VIEWS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ATTRIBUTES_CONCLUDE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_OR_UPDATE_ATTRIBUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INSERT_OR_UPDATE_USER_STORE_CONFIG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.INTERSECT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.LIKE_SYMBOL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.ORDER_BY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PAGINATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PATCH_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PATCH_ORGANIZATION_CONCLUDE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.PATCH_USER_STORE_CONFIG;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.REMOVE_ATTRIBUTE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UM_ROLE_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UPDATE_HAS_ATTRIBUTES_FIELD;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.UPDATE_ORGANIZATION_METADATA;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_BY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_HAS_ATTRIBUTES_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_BY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_ID_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_STATUS_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.WITH_FILTERED_ORG_INFO_AS;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getMaximumQueryLengthInBytes;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getNewTemplate;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.isViewsInUse;

import static java.time.ZoneOffset.UTC;

/**
 * Organization mgt dao implementation.
 */
public class OrganizationMgtDaoImpl implements OrganizationMgtDao {

    private static final Log log = LogFactory.getLog(OrganizationMgtDaoImpl.class);
    private static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC));

    @Override
    public void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException {

        Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());
        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeInsert(INSERT_ORGANIZATION, preparedStatement -> {
                int parameterIndex = 1;
                preparedStatement.setString(parameterIndex, organization.getId());
                preparedStatement.setInt(++parameterIndex, organization.getTenantId());
                preparedStatement.setString(++parameterIndex, organization.getName());
                preparedStatement.setString(++parameterIndex, organization.getDisplayName());
                preparedStatement.setString(++parameterIndex, organization.getDescription());
                preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                preparedStatement.setString(++parameterIndex, organization.getMetadata().getCreatedBy().getId());
                preparedStatement.setString(++parameterIndex, organization.getMetadata().getLastModifiedBy().getId());
                preparedStatement.setInt(++parameterIndex, organization.hasAttributes() ? 1 : 0);
                preparedStatement.setString(++parameterIndex, organization.getStatus().toString());
                preparedStatement.setString(++parameterIndex, organization.getParent().getId());
            }, organization, false);
            if (organization.hasAttributes()) {
                insertOrganizationAttributes(jdbcTemplate, organization);
            }
            insertOrUpdateUserStoreConfigs(jdbcTemplate, organization);
            organization.getMetadata().setCreated(currentTime.toInstant().toString());
            organization.getMetadata().setLastModified(currentTime.toInstant().toString());
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_ADD_ERROR,
                    "Organization name " + organization.getName() + ", Tenant Id " + organization.getTenantId(), e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public List<Organization> getOrganizations(Condition condition, int tenantId, int offset, int limit, String sortBy,
            String sortOrder, List<String> requestedAttributes, String userId, boolean includePermissions,
            boolean listAsAdmin)
            throws OrganizationManagementException {

        PlaceholderSQL placeholderSQL = buildQuery(condition, offset, limit, sortBy, sortOrder, listAsAdmin);
        JdbcTemplate jdbcTemplate = getNewTemplate();
        String query = placeholderSQL.getQuery();
        StringJoiner sj = new StringJoiner(",");
        // Check permissions for non-admin users
        if (!listAsAdmin) {
            // Get list of roles with the required permission
            List<String> roleIds = getRoleListForPermission(jdbcTemplate, ORGANIZATION_VIEW_PERMISSION);
            if (roleIds.isEmpty()) {
                return new ArrayList<>();
            }
            for (String id : roleIds) {
                sj.add("'" + id + "'");
            }
            // Can not perform this in a prepared statement due to character escaping.
            // System generated list of role IDs. No security concern here.
            query = query.replace("#", sj.toString());
            if (log.isDebugEnabled()) {
                log.debug("Get matching organization IDs query with role IDs : " + query);
            }
            validateQueryLength(query);
        }

        // Get organization IDs
        List<String> orgIds;
        try {
            orgIds = jdbcTemplate.executeQuery(query,
                    (resultSet, rowNumber) -> resultSet.getString(VIEW_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        // Populate tenant ID
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        if (!listAsAdmin) {
                            // Populate user id for non-admin users
                            preparedStatement.setString(++parameterIndex, userId);
                        }
                        // Populate generated conditions if any
                        for (int count = 0;
                             placeholderSQL.getData() != null && count < placeholderSQL.getData().size(); count++) {
                            if (placeholderSQL.getData().get(count).getClass().equals(Integer.class)) {
                                preparedStatement
                                        .setInt(++parameterIndex, (Integer) placeholderSQL.getData().get(count));
                            } else if (placeholderSQL.getData().get(count).getClass().equals(Timestamp.class)) {
                                preparedStatement
                                        .setTimestamp(++parameterIndex,
                                                (Timestamp) placeholderSQL.getData().get(count), calendar);
                            } else {
                                // Append '%' if 'contains', 'startswith' and 'endswith' operators are being used
                                String data = (String) placeholderSQL.getData().get(count);
                                if (STARTS_WITH.equals(placeholderSQL.getOperators().get(count))) {
                                    data = data.concat(LIKE_SYMBOL);
                                } else if (ENDS_WITH.equals(placeholderSQL.getOperators().get(count))) {
                                    data = LIKE_SYMBOL.concat(data);
                                } else if (SUBSTRING.equals(placeholderSQL.getOperators().get(count))) {
                                    data = LIKE_SYMBOL.concat(data).concat(LIKE_SYMBOL);
                                }
                                preparedStatement.setString(++parameterIndex, data);
                            }
                        }
                        });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ERROR, "Error while retrieving organization IDs.",
                    e);
        }
        if (orgIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Get organizations by IDs
        query = GET_ORGANIZATIONS_BY_IDS;
        sj = new StringJoiner(",");
        for (String id : orgIds) {
            sj.add("'" + id + "'");
        }
        // Can not perform this in a prepared statement due to character escaping.
        // This query only expects a list of organization IDs(server generated) to be retrieved.
        // Hence, no security vulnerability.
        query = query.replace("?", sj.toString());
        validateQueryLength(query);
        List<OrganizationRowDataCollector> organizationRowDataCollectors;
        try {
            organizationRowDataCollectors = jdbcTemplate.executeQuery(query, (resultSet, rowNumber) -> {
                OrganizationRowDataCollector collector = new OrganizationRowDataCollector();
                collector.setId(resultSet.getString(VIEW_ID_COLUMN));
                collector.setName(resultSet.getString(VIEW_NAME_COLUMN));
                collector.setDisplayName(resultSet.getString(VIEW_DISPLAY_NAME_COLUMN));
                collector.setDescription(resultSet.getString(VIEW_DESCRIPTION_COLUMN));
                collector.setParentId(resultSet.getString(VIEW_PARENT_ID_COLUMN));
                collector.setParentName(resultSet.getString(VIEW_PARENT_NAME_COLUMN));
                collector.setParentDisplayName(resultSet.getString(VIEW_PARENT_DISPLAY_NAME_COLUMN));
                collector.setStatus(Organization.OrgStatus.valueOf(resultSet.getString(VIEW_STATUS_COLUMN)));
                collector.setLastModified(resultSet.getTimestamp(VIEW_LAST_MODIFIED_COLUMN, calendar));
                collector.setCreated(resultSet.getTimestamp(VIEW_CREATED_TIME_COLUMN, calendar));
                collector.setCreatedBy(resultSet.getString(VIEW_CREATED_BY_COLUMN));
                collector.setLastModifiedBy(resultSet.getString(VIEW_LAST_MODIFIED_BY_COLUMN));
                collector.setHasAttributes(resultSet.getInt(VIEW_HAS_ATTRIBUTES_COLUMN) == 1 ? true : false);
                collector.setAttributeKey(resultSet.getString(VIEW_ATTR_KEY_COLUMN));
                collector.setAttributeValue(resultSet.getString(VIEW_ATTR_VALUE_COLUMN));
                return collector;
            });
            // Build organizations from the DB results.
            // Append only the requested attributes to the organizations.
            Map<String, Organization> organizationMap = (organizationRowDataCollectors == null
                    || organizationRowDataCollectors.size() == 0) ?
                    new HashMap<>() :
                    buildOrganizationsFromRawData(organizationRowDataCollectors, requestedAttributes);
            // Populate each organization with permissions if required
            if (includePermissions) {
                Map<String, List<String>> userOrgPermissions = OrganizationMgtDataHolder.getInstance()
                        .getOrganizationAuthDao().findUserPermissionsForOrganizations(jdbcTemplate, userId, orgIds,
                                listAsAdmin);
                organizationMap.forEach((id, org) -> org.setPermissions(userOrgPermissions.get(id)));
            }
            // When sorting is required, organization IDs were fetched sorted from the DB. But the collected
            // organizations may not.
            // Therefore, sort the organization as per the order of their IDs.
            return sortBy != null ?
                    sortCollectedOrganizations(organizationMap, orgIds) :
                    new ArrayList<>(organizationMap.values());
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ERROR,
                    "Error while constructing organizations by IDs", e);
        }
    }

    @Override
    public Organization getOrganization(int tenantId, String organizationId, String userId, boolean getAsAdmin)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        List<OrganizationRowDataCollector> organizationRowDataCollectors;
        try {
            organizationRowDataCollectors = jdbcTemplate
                    .executeQuery(GET_ORGANIZATION_BY_ID,
                            (resultSet, rowNumber) -> {
                                OrganizationRowDataCollector collector = new OrganizationRowDataCollector();
                                collector.setId(organizationId);
                                collector.setName(resultSet.getString(VIEW_NAME_COLUMN));
                                collector.setDisplayName(resultSet.getString(VIEW_DISPLAY_NAME_COLUMN));
                                collector.setDescription(resultSet.getString(VIEW_DESCRIPTION_COLUMN));
                                collector.setParentId(resultSet.getString(VIEW_PARENT_ID_COLUMN));
                                collector.setParentName(resultSet.getString(VIEW_PARENT_NAME_COLUMN));
                                collector.setParentDisplayName(resultSet.getString(VIEW_PARENT_DISPLAY_NAME_COLUMN));
                                collector.setStatus(Organization.OrgStatus.valueOf(resultSet.getString(VIEW_STATUS_COLUMN)));
                                collector.setLastModified(resultSet.getTimestamp(VIEW_LAST_MODIFIED_COLUMN, calendar));
                                collector.setCreated(resultSet.getTimestamp(VIEW_CREATED_TIME_COLUMN, calendar));
                                collector.setCreatedBy(resultSet.getString(VIEW_CREATED_BY_COLUMN));
                                collector.setLastModifiedBy(resultSet.getString(VIEW_LAST_MODIFIED_BY_COLUMN));
                                collector.setHasAttributes(resultSet.getInt(VIEW_HAS_ATTRIBUTES_COLUMN) == 1 ? true : false);
                                collector.setAttributeId(resultSet.getString(VIEW_ATTR_ID_COLUMN));
                                collector.setAttributeKey(resultSet.getString(VIEW_ATTR_KEY_COLUMN));
                                collector.setAttributeValue(resultSet.getString(VIEW_ATTR_VALUE_COLUMN));
                                return collector;
                            },
                            preparedStatement -> {
                                int parameterIndex = 0;
                                preparedStatement.setInt(++parameterIndex, tenantId);
                                preparedStatement.setString(++parameterIndex, organizationId);
                            }
                    );
            // Populate each organization with permissions if required
            boolean includePermissions = userId != null;
            List<String> permissions = null;
            if (includePermissions) {
                permissions = OrganizationMgtDataHolder.getInstance().getOrganizationAuthDao()
                        .findUserPermissionsForOrganizations(
                                jdbcTemplate,
                                userId,
                                Arrays.asList(organizationId),
                                getAsAdmin
                        ).get(organizationId);
            }
            return (organizationRowDataCollectors == null || organizationRowDataCollectors.size() == 0) ?
                    null :
                    buildOrganizationFromRawData(organizationRowDataCollectors, includePermissions, permissions);
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_BY_ID_ERROR, organizationId, e);
        }
    }

    @Override
    public void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            // Delete organization from UM_ORG table and cascade the deletion to other two tables
            jdbcTemplate.executeUpdate(DELETE_ORGANIZATION_BY_ID, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setInt(++parameterIndex, tenantId);
                preparedStatement.setString(++parameterIndex, organizationId);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_DELETE_ERROR, "Organization Id " + organizationId, e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public List<String> getChildOrganizationIds(String organizationId, String userId)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        // This API can either be called internally (disabling organization)
        // or by GET '/api/identity/organization-mgt/v1.0/organizations/{parent_id}/children' API call
        // We should only filter for authorized organizations for the latter
        boolean isInternalCall = userId == null;
        String query = isInternalCall ? FIND_CHILD_ORG_IDS : FIND_AUTHORIZED_CHILD_ORG_IDS;
        if (!isInternalCall) {
            // Get list of roles with the required permission
            List<String> roleIds = getRoleListForPermission(jdbcTemplate, ORGANIZATION_VIEW_PERMISSION);
            if (roleIds.isEmpty()) {
                return new ArrayList<>();
            }
            StringJoiner sj = new StringJoiner(",");
            for (String id : roleIds) {
                sj.add("'" + id + "'");
            }
            // Can not perform this in a prepared statement due to character escaping.
            // System generated list of role IDs. No security concern here.
            query = query.replace("#", sj.toString());
            if (log.isDebugEnabled()) {
                log.debug("Get matching child organization IDs query with role IDs : " + query);
            }
            validateQueryLength(query);
        }
        try {
            List<String> childOrganizationIds = jdbcTemplate.executeQuery(
                    query,
                    (resultSet, rowNumber) -> resultSet.getString(VIEW_ID_COLUMN),
                    preparedStatement -> {
                               int parameterIndex = 0;
                                preparedStatement.setString(++parameterIndex, organizationId);
                                if (!isInternalCall) {
                                    preparedStatement.setString(++parameterIndex, userId);
                                }
                    });
            return childOrganizationIds;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_CHILDREN_ERROR, "Organization Id " + organizationId,
                    e);
        }
    }

    @Override
    public List<String> getAllCascadedChildOrganizationIds(String organizationId)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            List<String> childOrganizationIds = jdbcTemplate.executeQuery(
                    SQLConstants.FIND_ALL_CHILD_ORG_IDS,
                    (resultSet, rowNumber) -> resultSet.getString(VIEW_ID_COLUMN),
                    preparedStatement ->
                        preparedStatement.setString(1, organizationId));
            return childOrganizationIds;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_CHILDREN_ERROR, "Organization Id " + organizationId,
                    e);
        }
    }

    @Override
    public Map<String, UserStoreConfig> getUserStoreConfigsByOrgId(int tenantId, String organizationId)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            List<UserStoreConfig> userStoreConfigs = jdbcTemplate
                    .executeQuery(
                        isViewsInUse() ? GET_USER_STORE_CONFIGS_BY_ORG_ID :
                                GET_USER_STORE_CONFIGS_BY_ORG_ID_WITHOUT_VIEWS,
                        (resultSet, rowNumber) -> {
                            UserStoreConfig config = new UserStoreConfig();
                            config.setId(resultSet.getString(VIEW_CONFIG_ID_COLUMN));
                            config.setKey(resultSet.getString(VIEW_CONFIG_KEY_COLUMN));
                            config.setValue(resultSet.getString(VIEW_CONFIG_VALUE_COLUMN));
                            return config;
                        },
                        preparedStatement -> {
                            int parameterIndex = 0;
                            preparedStatement.setInt(++parameterIndex, tenantId);
                            preparedStatement.setString(++parameterIndex, organizationId);
                        });
            return userStoreConfigs.stream().collect(Collectors.toMap(UserStoreConfig::getKey, config -> config));

        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_CONFIGS_ERROR, organizationId, e);
        }
    }

    @Override
    public boolean isOrganizationExistByName(int tenantId, String name) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            int orgCount = jdbcTemplate.fetchSingleRecord(CHECK_ORGANIZATION_EXIST_BY_NAME,
                    (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, name);
                    });
            return orgCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_NAME_ERROR,
                    "Organization name " + name + ", Tenant id " + tenantId, e);
        }
    }

    @Override
    public boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            int orgCount = jdbcTemplate.fetchSingleRecord(CHECK_ORGANIZATION_EXIST_BY_ID,
                    (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, id);
                    });
            return orgCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_ID_ERROR,
                    "Organization ID " + id + ", Tenant id " + tenantId, e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    @Override
    public void patchOrganization(String organizationId, List<Operation> operations)
            throws OrganizationManagementException {

        try {
            // Create a new jdbc template.
            JdbcTemplate jdbcTemplate = getNewTemplate();
            // Create a HashMap for primary fields.
            Map<String, Operation> primaryFieldsOperationsMap = new HashMap<>();
            // Classify the attributes according to add, replace and remove and process using Lists.
            List<Operation> addOrReplaceAttributeOperations = new ArrayList<>();
            List<Operation> removeAttributeOperations = new ArrayList<>();

            // Assign operations to respective lists/map.
            assignOperations(operations, addOrReplaceAttributeOperations, removeAttributeOperations,
                    primaryFieldsOperationsMap);

            // Create a transaction.
            jdbcTemplate.withTransaction(template -> {
                if (CollectionUtils.isNotEmpty(addOrReplaceAttributeOperations)) {
                    template.executeBatchInsert(INSERT_OR_UPDATE_ATTRIBUTE, preparedStatement -> {
                        for (Operation operation : addOrReplaceAttributeOperations) {
                            String attributeKey = operation.getPath().replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
                            int parameterIndex = 0;
                            preparedStatement.setString(++parameterIndex, generateUniqueID());
                            preparedStatement.setString(++parameterIndex, organizationId);
                            preparedStatement.setString(++parameterIndex, attributeKey);
                            preparedStatement.setString(++parameterIndex, operation.getValue());
                            preparedStatement.addBatch();
                            if (log.isDebugEnabled()) {
                                log.debug("operation value :" + operation.getValue() + " is added to the batch.");
                            }
                        }
                    }, new Attribute());
                }
                if (CollectionUtils.isNotEmpty(removeAttributeOperations)) {
                    for (Operation operation : removeAttributeOperations) {
                        String attributeKey = operation.getPath().replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
                        template.executeUpdate(REMOVE_ATTRIBUTE, preparedStatement -> {
                            int parameterIndex = 0;
                            preparedStatement.setString(++parameterIndex, organizationId);
                            preparedStatement.setString(++parameterIndex, attributeKey);
                        });
                        if (log.isDebugEnabled()) {
                            log.debug("Organization operation : " + operation.getValue());
                        }
                    }
                }

                for (Map.Entry<String, Operation> entry : primaryFieldsOperationsMap.entrySet()) {
                    String query = entry.getKey();
                    Operation operation = entry.getValue();
                    template.executeUpdate(query, preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex,
                                operation.getOp().equals(PATCH_OP_REMOVE) ? null : operation.getValue());
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
                    if (log.isDebugEnabled()) {
                        log.debug("Organization operation : " + operation.getValue());
                    }
                }
                return null;
            });
            validateHasAttributesField(jdbcTemplate, organizationId);
        } catch (TransactionException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                    "Error while patching the organization", e);
        }
    }

    @Override
    public String getOrganizationIdByName(int tenantId, String organizationName)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            return jdbcTemplate.fetchSingleRecord(GET_ORGANIZATION_ID_BY_NAME,
                    (resultSet, rowNumber) -> resultSet.getString(VIEW_ID_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationName);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR,
                    "Organization name " + organizationName + ", tenant id " + tenantId, e);
        }
    }

    @Override
    public boolean isAttributeExistByKey(int tenantId, String organizationId, String attributeKey)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            int attrCount = jdbcTemplate.fetchSingleRecord(CHECK_ATTRIBUTE_EXIST_BY_KEY,
                    (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setInt(++parameterIndex, tenantId);
                        preparedStatement.setString(++parameterIndex, organizationId);
                        preparedStatement.setString(++parameterIndex, attributeKey);
                    });
            return attrCount > 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR,
                    "Error while checking if the the Attribute key exist : " + attributeKey, e);
        }
    }

    @Override
    public void modifyOrganizationMetadata(String organizationId, Metadata metadata)
            throws OrganizationManagementException {

        Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());
        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            jdbcTemplate.executeUpdate(UPDATE_ORGANIZATION_METADATA, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setTimestamp(++parameterIndex, currentTime, calendar);
                preparedStatement.setString(++parameterIndex, metadata.getLastModifiedBy().getId());
                preparedStatement.setString(++parameterIndex, organizationId);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                    "Error while updating organization metadata : " + organizationId, e);
        }
    }

    @Override
    public void patchUserStoreConfigs(String organizationId, Operation operation)
            throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        if (RDN.equals(operation.getPath())) {
            // Set both RDN and DN appropriately
            Map<String, UserStoreConfig> userStoreConfigs = getUserStoreConfigsByOrgId(
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(), organizationId);
            String dn = userStoreConfigs.get(DN).getValue();
            dn = dn.replace(String.format(DN_PLACE_HOLDER, userStoreConfigs.get(RDN).getValue()),
                    String.format(DN_PLACE_HOLDER, operation.getValue()));
            if (log.isDebugEnabled()) {
                log.debug("New DN of the organization ID : " + organizationId + " is : " + dn);
            }
            try {
                updateUserStoreConfig(jdbcTemplate, organizationId, RDN, operation.getValue());
                updateUserStoreConfig(jdbcTemplate, organizationId, DN, dn);
            } catch (DataAccessException e) {
                throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                        "Error while updating user store configs for the organization ID : " + organizationId, e);
            }
        }
    }

    private void updateUserStoreConfig(JdbcTemplate template, String organizationId, String key, String value)
            throws DataAccessException {

        template.executeUpdate(PATCH_USER_STORE_CONFIG, preparedStatement -> {
            int parameterIndex = 0;
            preparedStatement.setString(++parameterIndex, value);
            preparedStatement.setString(++parameterIndex, organizationId);
            preparedStatement.setString(++parameterIndex, key);
        });
    }

    public boolean isRdnAvailable(String rdn, String parentId, int tenantId) throws OrganizationManagementException {

        JdbcTemplate jdbcTemplate = getNewTemplate();
        try {
            int matchingEntries = jdbcTemplate
                    .fetchSingleRecord(CHECK_RDN_AVAILABILITY,
                            (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN),
                            preparedStatement -> {
                                int parameterIndex = 0;
                                preparedStatement.setInt(++parameterIndex, tenantId);
                                preparedStatement.setString(++parameterIndex, parentId);
                                preparedStatement.setString(++parameterIndex, rdn);
                            });
            return matchingEntries == 0;
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_RDN_AVAILABILITY_ERROR,
                    "Error while checking if the RDN is available : " + rdn + ", parent id : " + parentId, e);
        }
    }

    private void patchAttribute(JdbcTemplate template, String organizationId, Operation operation)
            throws DataAccessException, OrganizationManagementException {

        String attributeKey = operation.getPath().replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim();
        // Insert or update attribute
        if (operation.getOp().equals(PATCH_OP_ADD) || operation.getOp().equals(PATCH_OP_REPLACE)) {
            template.executeInsert(INSERT_OR_UPDATE_ATTRIBUTE, preparedStatement -> {
                int parameterIndex = 0;
                // On update, unique ID and Org ID will not be updated
                preparedStatement.setString(++parameterIndex, generateUniqueID());
                preparedStatement.setString(++parameterIndex, organizationId);
                preparedStatement.setString(++parameterIndex, attributeKey);
                preparedStatement.setString(++parameterIndex, operation.getValue());
            }, new Attribute(), false);
        } else {
            // Remove attribute
            template.executeUpdate(REMOVE_ATTRIBUTE, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setString(++parameterIndex, organizationId);
                preparedStatement.setString(++parameterIndex, operation.getPath());
            });
        }
        validateHasAttributesField(template, organizationId);
    }

    private void validateHasAttributesField(JdbcTemplate template, String organizationId)
            throws OrganizationManagementException {

        int attrCount;
        try {
            attrCount = template.fetchSingleRecord(CHECK_ORG_HAS_ATTRIBUTES,
                    (resultSet, rowNumber) -> resultSet.getInt(COUNT_COLUMN), preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, organizationId);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR,
                    "Error while checking if the organization has any attributes for the organization Id : "
                            + organizationId, e);
        }
        try {
            template.executeUpdate(UPDATE_HAS_ATTRIBUTES_FIELD, preparedStatement -> {
                int parameterIndex = 0;
                preparedStatement.setInt(++parameterIndex, attrCount > 0 ? 1 : 0);
                preparedStatement.setString(++parameterIndex, organizationId);
            });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR,
                    "Error while updating HAS_ATTRIBUTES field of the organization Id " + organizationId, e);
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    private void insertOrganizationAttributes(JdbcTemplate template, Organization organization)
            throws DataAccessException, OrganizationManagementClientException {

        String query = buildQueryForAttributes(organization);
        template.executeInsert(query, preparedStatement -> {
            int parameterIndex = 0;
            for (Map.Entry<String, Attribute> entry : organization.getAttributes().entrySet()) {
                preparedStatement.setString(++parameterIndex, generateUniqueID());
                preparedStatement.setString(++parameterIndex, organization.getId());
                preparedStatement.setString(++parameterIndex, entry.getValue().getKey());
                preparedStatement.setString(++parameterIndex, entry.getValue().getValue());
            }
        }, organization, false);
    }

    private void insertOrUpdateUserStoreConfigs(JdbcTemplate template, Organization organization)
            throws DataAccessException {

        for (Map.Entry<String, UserStoreConfig> entry : organization.getUserStoreConfigs().entrySet()) {
            template.executeInsert(INSERT_OR_UPDATE_USER_STORE_CONFIG, preparedStatement -> {
                int parameterIndex = 0;
                // On update, unique ID and Org ID will not be updated
                preparedStatement.setString(++parameterIndex, generateUniqueID());
                preparedStatement.setString(++parameterIndex, organization.getId());
                preparedStatement.setString(++parameterIndex, entry.getValue().getKey());
                preparedStatement.setString(++parameterIndex, entry.getValue().getValue());
            }, organization, false);
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
        if (sb.toString().getBytes(StandardCharsets.UTF_8).length > getMaximumQueryLengthInBytes()) {
            if (log.isDebugEnabled()) {
                log.debug("Error building SQL query for the attribute insert. Number of attributes: " + organization
                        .getAttributes().size() + " exceeds the maximum query length: "
                        + MAX_QUERY_LENGTH_IN_BYTES_SQL);
            }
            throw handleClientException(LIST_REQUEST_FILTER_TOO_LONG,
                    "Too much attributes for the creation request. Try patching.");
        }
        return sb.toString();
    }

    private Organization buildOrganizationFromRawData(List<OrganizationRowDataCollector> organizationRowDataCollectors,
            boolean includePermissions, List<String> permissions) {

        Organization organization = new Organization();
        organizationRowDataCollectors.forEach(collector -> {
            if (organization.getId() == null) {
                organization.setId(collector.getId());
                organization.setName(collector.getName());
                organization.setDisplayName(collector.getDisplayName());
                organization.setDescription(collector.getDescription());
                organization.getParent().setId(collector.getParentId());
                organization.getParent().setName(collector.getParentName());
                organization.getParent().setDisplayName(collector.getParentDisplayName());
                organization.setStatus(collector.getStatus());
                organization.getMetadata().setCreated(collector.getCreated().toString());
                organization.getMetadata().setLastModified(collector.getLastModified().toString());
                organization.getMetadata().getCreatedBy().setId(collector.getCreatedBy());
                organization.getMetadata().getLastModifiedBy().setId(collector.getLastModifiedBy());
                organization.setHasAttributes(collector.hasAttributes());
            }
            if (organization.hasAttributes() && collector.getAttributeKey() != null && !organization.getAttributes()
                    .containsKey(collector.getAttributeKey())) {
                organization.getAttributes().put(collector.getAttributeKey(),
                        new Attribute(collector.getAttributeKey(), collector.getAttributeValue()));
            }
            if (includePermissions && permissions != null) {
                organization.setPermissions(permissions);
            }
        });
        return organization;
    }

    private Map<String, Organization> buildOrganizationsFromRawData(
            List<OrganizationRowDataCollector> organizationRowDataCollectors, List<String> requestedAttributes) {

        Map<String, Organization> organizationMap = new HashMap<>();
        for (OrganizationRowDataCollector collector : organizationRowDataCollectors) {
            Organization organization;
            if (organizationMap.containsKey(collector.getId())) {
                organization = organizationMap.get(collector.getId());
            } else {
                organization = new Organization();
                organization.setId(collector.getId());
                organization.setName(collector.getName());
                organization.setDisplayName(collector.getDisplayName());
                organization.setDescription(collector.getDescription());
                organization.getParent().setId(collector.getParentId());
                organization.getParent().setName(collector.getParentName());
                organization.getParent().setDisplayName(collector.getParentDisplayName());
                organization.setStatus(collector.getStatus());
                organization.getMetadata().setCreated(collector.getCreated().toString());
                organization.getMetadata().setLastModified(collector.getLastModified().toString());
                organization.getMetadata().getCreatedBy().setId(collector.getCreatedBy());
                organization.getMetadata().getLastModifiedBy().setId(collector.getLastModifiedBy());
                organization.setHasAttributes(collector.hasAttributes());
                organizationMap.put(organization.getId(), organization);
            }
            // Populate with attributes if any
            // Populate attributes if requested specifically, or requested all the attributes '*'
            if (organization.hasAttributes() && collector.getAttributeKey() != null && (
                    requestedAttributes.contains(collector.getAttributeKey()) || requestedAttributes.contains("*"))
                    && !organization.getAttributes().containsKey(collector.getAttributeKey())) {
                organization.getAttributes().put(collector.getAttributeKey(),
                        new Attribute(collector.getAttributeKey(), collector.getAttributeValue()));
            }
        }
        return organizationMap;
    }

    private void validateQueryLength(String query) throws OrganizationManagementClientException {

        if (query.getBytes(StandardCharsets.UTF_8).length > getMaximumQueryLengthInBytes()) {
            if (log.isDebugEnabled()) {
                log.debug("Error building SQL query. Get organizations expression " + "query length: " + query.length()
                        + " exceeds the maximum limit: " + MAX_QUERY_LENGTH_IN_BYTES_SQL);
            }
            throw handleClientException(LIST_REQUEST_FILTER_TOO_LONG,
                    "Query length exceeded the maximum limit.");
        }
    }

    private PlaceholderSQL buildQuery(Condition condition, int offset, int limit, String sortBy, String sortOrder,
            boolean listAsAdmin)
            throws OrganizationManagementException {

        boolean paginationReq = offset > -1 && limit > 0;
        boolean searchReq = condition != null;
        // Ascending if not specified otherwise.
        sortOrder = sortOrder != null && "DESC".equals(sortOrder.trim().toUpperCase(Locale.ENGLISH)) ? "DESC" : "ASC";

        PlaceholderSQL placeholderSQL;
        try {
            placeholderSQL = searchReq ?
                    condition.buildQuery(new PrimitiveConditionValidator(new OrganizationSearchBean()), false) :
                    new PlaceholderSQL();
        } catch (PrimitiveConditionValidationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error passing the condition ", e);
            }
            throw handleClientException(LIST_REQUEST_INVALID_FILTER_PARAMETER, null);
        }
        StringBuilder queryBuilder = new StringBuilder();
        // Base query with tenant id search condition
        queryBuilder.append(GET_ALL_ORGANIZATION_IDS)
                .append(DEFAULT_CONDITION);

        // Check organization permissions for non admin users
        if (!listAsAdmin) {
            queryBuilder.insert(0, WITH_FILTERED_ORG_INFO_AS);
            queryBuilder.append(GET_ALL_AUTHORIZATION_ORGANIZATION_IDS_WITH_JOIN);
        }
        // Append generated search conditions
        if (searchReq) {
            queryBuilder.append("\n").append(INTERSECT).append("\n");
            queryBuilder.append(placeholderSQL.getQuery());
        }
        // Append sorting condition
        queryBuilder = new StringBuilder(String.format(ORDER_BY, queryBuilder.toString(), sortBy, sortOrder));
        // Append pagination condition
        if (paginationReq) {
            queryBuilder.append(String.format(PAGINATION, offset, limit));
        }
        placeholderSQL.setQuery(queryBuilder.toString());
        if (log.isDebugEnabled()) {
            log.debug("Built query : " + placeholderSQL.getQuery());
        }
        return placeholderSQL;
    }

    private List<Organization> sortCollectedOrganizations(Map<String, Organization> organizationMap,
            List<String> organizationIds) {

        List<Organization> organizations = new ArrayList<>();
        for (String id : organizationIds) {
            organizations.add(organizationMap.get(id));
        }
        return organizations;
    }

    private List<String> getRoleListForPermission(JdbcTemplate jdbcTemplate, String permission)
            throws OrganizationManagementServerException {

        List<String> roleIds;
        String basePermission = permission.contains(USER_MGT_BASE_PERMISSION) ? USER_MGT_BASE_PERMISSION :
                (permission.contains(ROLE_MGT_BASE_PERMISSION) ? ROLE_MGT_BASE_PERMISSION :
                        (permission.contains(ORGANIZATION_BASE_PERMISSION) ? ORGANIZATION_BASE_PERMISSION :
                                permission));
        try {
            roleIds = jdbcTemplate.executeQuery(isViewsInUse() ? GET_ROLE_IDS_FOR_PERMISSION :
                            GET_ROLE_IDS_FOR_PERMISSION_WITHOUT_VIEW,
                    (resultSet, rowNumber) -> resultSet.getString(UM_ROLE_ID_COLUMN),
                    preparedStatement -> {
                        int parameterIndex = 0;
                        preparedStatement.setString(++parameterIndex, permission);
                        preparedStatement.setString(++parameterIndex, basePermission);
                    });
        } catch (DataAccessException e) {
            throw handleServerException(ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR,
                    "Error obtaining authorized list of roles for the permission : " + permission, e);
        }
        return roleIds;
    }

    private void assignOperations(List<Operation> operations, List<Operation> addOrReplaceAttributeOperations,
                                  List<Operation> removeAttributeOperations,
                                  Map<String, Operation> primaryFieldsOperationsMap) {
        // Iterate and add to respective lists/map.
        for (Operation operation : operations) {
            String path = operation.getPath();
            if (path.startsWith(PATCH_PATH_ORG_ATTRIBUTES)) {
                if (operation.getOp().equals(PATCH_OP_ADD) || operation.getOp().equals(PATCH_OP_REPLACE)) {
                    addOrReplaceAttributeOperations.add(operation);
                } else {
                    removeAttributeOperations.add(operation);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(PATCH_ORGANIZATION);
                if (path.equals(PATCH_PATH_ORG_NAME)) {
                    sb.append(VIEW_NAME_COLUMN);
                } else if (path.equals(PATCH_PATH_ORG_DISPLAY_NAME)) {
                    sb.append(VIEW_DISPLAY_NAME_COLUMN);
                } else if (path.equals(PATCH_PATH_ORG_DESCRIPTION)) {
                    sb.append(VIEW_DESCRIPTION_COLUMN);
                } else if (path.equals(PATCH_PATH_ORG_STATUS)) {
                    sb.append(VIEW_STATUS_COLUMN);
                } else if (path.equals(PATCH_PATH_ORG_PARENT_ID)) {
                    sb.append(VIEW_PARENT_ID_COLUMN);
                }
                sb.append(PATCH_ORGANIZATION_CONCLUDE);
                String query = sb.toString();
                if (log.isDebugEnabled()) {
                    log.debug("Organization primary field patch query : " + query);
                }
                primaryFieldsOperationsMap.put(query, operation);
            }
        }
    }
}
