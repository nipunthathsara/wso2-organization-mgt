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

package org.wso2.carbon.identity.organization.mgt.core.constant;

import org.wso2.carbon.identity.core.util.IdentityUtil;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_BASE_PERMISSION;

public class SQLConstants {

    public static final String MAX_QUERY_LENGTH_IN_BYTES_SQL =
            IdentityUtil.getProperty("ConfigurationStore.MaximumQueryLengthInBytes");
    public static final String CHECK_ORGANIZATION_EXIST_BY_NAME =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    UM_ORG\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND NAME = ?";
    public static final String CHECK_ORGANIZATION_EXIST_BY_ID =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    UM_ORG\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND ID = ?";
    public static final String CHECK_ATTRIBUTE_EXIST_BY_KEY =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND ID = ? AND ATTR_KEY = ?";
    public static final String CHECK_ORG_HAS_ATTRIBUTES =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    UM_ORG_ATTRIBUTES\n" +
            "WHERE\n" +
            "    ORG_ID = ?";
    public static final String UPDATE_HAS_ATTRIBUTES_FIELD =
            "UPDATE\n" +
            "    UM_ORG\n" +
            "SET\n" +
            "    HAS_ATTRIBUTES = ?\n" +
            "WHERE\n" +
            "    ID = ?";
    public static final String GET_ORGANIZATION_ID_BY_NAME =
            "SELECT\n" +
            "    ID\n" +
            "FROM\n" +
            "    UM_ORG\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND NAME = ?";
    public static final String COUNT_COLUMN = "COUNT(1)";
    public static final String VIEW_ID_COLUMN = "ID";
    public static final String VIEW_TENANT_ID__COLUMN = "TENANT_ID";
    public static final String VIEW_NAME_COLUMN = "NAME";
    public static final String VIEW_DISPLAY_NAME_COLUMN = "DISPLAY_NAME";
    public static final String VIEW_DESCRIPTION_COLUMN = "DESCRIPTION";
    public static final String VIEW_CREATED_TIME_COLUMN = "CREATED_TIME";
    public static final String VIEW_LAST_MODIFIED_COLUMN = "LAST_MODIFIED";
    public static final String VIEW_CREATED_BY_COLUMN = "CREATED_BY";
    public static final String VIEW_LAST_MODIFIED_BY_COLUMN = "LAST_MODIFIED_BY";
    public static final String VIEW_HAS_ATTRIBUTES_COLUMN = "HAS_ATTRIBUTES";
    public static final String VIEW_STATUS_COLUMN = "STATUS";
    public static final String VIEW_PARENT_ID_COLUMN = "PARENT_ID";
    public static final String VIEW_PARENT_NAME_COLUMN = "PARENT_NAME";
    public static final String VIEW_PARENT_DISPLAY_NAME_COLUMN = "PARENT_DISPLAY_NAME";
    public static final String VIEW_ATTR_ID_COLUMN = "ATTR_ID";
    public static final String VIEW_ATTR_KEY_COLUMN = "ATTR_KEY";
    public static final String VIEW_ATTR_VALUE_COLUMN = "ATTR_VALUE";
    public static final String VIEW_CONFIG_ID_COLUMN = "CONFIG_ID";
    public static final String VIEW_CONFIG_KEY_COLUMN = "CONFIG_KEY";
    public static final String VIEW_CONFIG_VALUE_COLUMN = "CONFIG_VALUE";
    public static final String UM_ROLE_ID_COLUMN = "UM_ROLE_ID";
    public static final String UM_HYBRID_ROLE_ID_COLUMN = "UM_HYBRID_ROLE_ID";

    public static final String UM_ID_COLUMN = "UM_ID";
    public static final String ID_COLUMN = "ID";

    public static final String LIKE_SYMBOL = "%";
    // View returns null for non matching entries upon join. Hence, the NULL check.
    // View returns duplicate CONFIG_IDs if the  #attributes > #configs. Hence, DISTINCT
    public static final String GET_USER_STORE_CONFIGS_BY_ORG_ID =
            "SELECT\n" +
            "    DISTINCT V.CONFIG_ID, V.CONFIG_KEY, V.CONFIG_VALUE\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.TENANT_ID = ? AND V.ID = ? AND V.CONFIG_ID IS NOT NULL";
    public static final String INSERT_ORGANIZATION =
            "INSERT INTO \n" +
            "    UM_ORG\n" +
            "    (ID, TENANT_ID, NAME, DISPLAY_NAME, DESCRIPTION, CREATED_TIME, LAST_MODIFIED, CREATED_BY, LAST_MODIFIED_BY, HAS_ATTRIBUTES, STATUS, PARENT_ID)\n" +
            "VALUES\n" +
            "    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_ATTRIBUTES =
            "INSERT ALL\n";
    public static final String INSERT_ATTRIBUTE =
            "    INTO UM_ORG_ATTRIBUTES (ID, ORG_ID, ATTR_KEY, ATTR_VALUE)\n" +
            "    VALUES (?, ?, ?, ?)\n";
    public static final String INSERT_ATTRIBUTES_CONCLUDE =
            "SELECT 1 FROM dual";
    public static final String INSERT_OR_UPDATE_USER_STORE_CONFIG =
        "MERGE INTO\n" +
        "    UM_ORG_USERSTORE_CONFIGS C\n" +
        "USING\n" +
        "    (SELECT ? ID, ? ORG_ID, ? ATTR_KEY, ? ATTR_VALUE FROM dual) N\n" +
        "ON \n" +
        "    (N.ORG_ID = C.ORG_ID AND N.ATTR_KEY = C.ATTR_KEY)\n" +
        "WHEN MATCHED THEN\n" +
        "UPDATE\n" +
        "    SET C.ATTR_VALUE = N.ATTR_VALUE\n" +
        "WHEN NOT MATCHED THEN\n" +
        "INSERT\n" +
        "    (C.ID, C.ORG_ID, C.ATTR_KEY, C.ATTR_VALUE)\n" +
        "    VALUES (N.ID, N.ORG_ID, N.ATTR_KEY, N.ATTR_VALUE)";
    public static final String INSERT_OR_UPDATE_ATTRIBUTE =
            "MERGE INTO\n" +
            "    UM_ORG_ATTRIBUTES A\n" +
            "USING\n" +
            "    (SELECT ? ID, ? ORG_ID, ? ATTR_KEY, ? ATTR_VALUE FROM dual) N\n" +
            "ON \n" +
            "    (N.ORG_ID = A.ORG_ID AND N.ATTR_KEY = A.ATTR_KEY)\n" +
            "WHEN MATCHED THEN\n" +
            "UPDATE\n" +
            "    SET A.ATTR_VALUE = N.ATTR_VALUE\n" +
            "WHEN NOT MATCHED THEN\n" +
            "INSERT\n" +
            "    (A.ID, A.ORG_ID, A.ATTR_KEY, A.ATTR_VALUE)\n" +
            "    VALUES (N.ID, N.ORG_ID, N.ATTR_KEY, N.ATTR_VALUE)";
    public static final String REMOVE_ATTRIBUTE =
            "DELETE\n" +
            "FROM\n" +
            "    UM_ORG_ATTRIBUTES\n" +
            "WHERE\n" +
            "    ORG_ID = ? AND ATTR_KEY = ?";
    public static final String DELETE_ORGANIZATION_BY_ID =
            "DELETE\n" +
            "FROM\n" +
            "    UM_ORG O\n" +
            "WHERE\n" +
            "    O.TENANT_ID = ? AND O.ID = ?";
    public static final String GET_ORGANIZATION_BY_ID =
            "SELECT\n" +
            "    DISTINCT V.ID, V.NAME, V.DISPLAY_NAME, V.DESCRIPTION, V.PARENT_ID, V.PARENT_NAME, V.PARENT_DISPLAY_NAME,\n" +
            "    V.STATUS, V.CREATED_TIME, V.LAST_MODIFIED, V.CREATED_BY, V.LAST_MODIFIED_BY, V.HAS_ATTRIBUTES, V.ATTR_ID,\n" +
            "    V.ATTR_KEY, V.ATTR_VALUE\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.TENANT_ID = ? AND V.ID = ?";
    public static final String FIND_AUTHORIZED_CHILD_ORG_IDS =
            "SELECT\n" +
            "    DISTINCT ID\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW\n" +
            "WHERE\n" +
            "    PARENT_ID = ?  AND UM_USER_ID = ? AND UM_ROLE_ID IN (#)";
    public static final String FIND_CHILD_ORG_IDS =
            "SELECT\n" +
            "    O.ID\n" +
            "FROM\n" +
            "    UM_ORG O\n" +
            "WHERE\n" +
            "    O.PARENT_ID = ?";
    // ORDER BY with DISTINCT requires to have the columns in the SELECT clause.
    public static final String GET_ALL_ORGANIZATION_IDS =
            "SELECT\n" +
            "    DISTINCT ID, NAME, DISPLAY_NAME, DESCRIPTION, CREATED_TIME, LAST_MODIFIED, CREATED_BY, LAST_MODIFIED_BY, STATUS, PARENT_NAME, PARENT_DISPLAY_NAME\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND UM_USER_ID = ? AND UM_ROLE_ID IN (#)";
    public static final String ORDER_BY =
            "\nORDER BY" +
            "\n   %s %s";
    public static final String PAGINATION =
            "\nOFFSET" +
            "\n   %s ROWS" +
            "\nFETCH NEXT" +
            "\n   %s ROWS ONLY";
    public static final String GET_ORGANIZATIONS_BY_IDS =
            "SELECT\n" +
            "    DISTINCT V.ID,\n" +
            "    V.NAME,\n" +
            "    V.DISPLAY_NAME,\n" +
            "    V.DESCRIPTION,\n" +
            "    V.STATUS,\n" +
            "    V.PARENT_ID, \n" +
            "    V.PARENT_NAME, \n" +
            "    V.PARENT_DISPLAY_NAME,\n" +
            "    V.CREATED_TIME,\n" +
            "    V.LAST_MODIFIED,\n" +
            "    V.CREATED_BY,\n" +
            "    V.LAST_MODIFIED_BY,\n" +
            "    V.HAS_ATTRIBUTES,\n" +
            "    V.ATTR_KEY,\n" +
            "    V.ATTR_VALUE\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.ID IN (?)";
    public static final String PATCH_ORGANIZATION =
            "UPDATE\n" +
            "    UM_ORG\n" +
            "SET ";
    public static final String PATCH_ORGANIZATION_CONCLUDE =
            " = ?\n" +
            "WHERE\n" +
            "    ID = ?";
    public static final String PATCH_USER_STORE_CONFIG =
            "UPDATE\n" +
            "    UM_ORG_USERSTORE_CONFIGS\n" +
            "SET\n" +
            "    ATTR_VALUE = ?\n" +
            "WHERE\n" +
            "    ORG_ID = ? AND ATTR_KEY = ?";
    public static final String UPDATE_ORGANIZATION_METADATA =
            "UPDATE\n" +
            "    UM_ORG\n" +
            "SET\n" +
            "    LAST_MODIFIED = ?, LAST_MODIFIED_BY = ?\n" +
            "WHERE\n" +
            "    ID = ?";
    public static final String CHECK_RDN_AVAILABILITY =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND PARENT_ID = ? AND CONFIG_KEY = 'RDN' AND CONFIG_VALUE = ?";
    public static final String GET_ROLE_IDS_FOR_PERMISSION =
            "SELECT\n" +
            "    UM_ROLE_ID\n" +
            "FROM\n" +
            "    ORG_AUTHZ_VIEW\n" +
            "WHERE\n" +
            "    UM_RESOURCE_ID = ? OR UM_RESOURCE_ID = '" + ORGANIZATION_BASE_PERMISSION + "'";
    public static final String IS_USER_AUTHORIZED =
            "SELECT" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    ORG_AUTHZ_VIEW\n" +
            "WHERE\n" +
            "    UM_USER_ID = ? AND ORG_ID = ? AND (UM_RESOURCE_ID = ? OR UM_RESOURCE_ID = '" + ORGANIZATION_BASE_PERMISSION + "')";
    public static final String GET_USER_AUTHORIZED_ROLE =
            "SELECT" +
            "    UM_ROLE_ID\n" +
            "FROM\n" +
            "    ORG_AUTHZ_VIEW\n" +
            "WHERE\n" +
            "    UM_USER_ID = ? AND ORG_ID = ? AND (UM_RESOURCE_ID = ? OR UM_RESOURCE_ID = '" + ORGANIZATION_BASE_PERMISSION + "')";
    public static final String ADD_USER_ROLE_ORG_MAPPING =
            "INSERT INTO\n" +
            "    UM_USER_ROLE_ORG\n" +
            "    (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_HYBRID_ROLE_ID, UM_TENANT_ID, ORG_ID)\n" +
            "VALUES\n" +
            "    (?, ?, ?, ?, ?, ?)";
    public static final String FIND_HYBRID_ID_FROM_ROLE_NAME =
            "SELECT\n" +
            "    UM_ID\n" +
            "FROM\n" +
            "    UM_HYBRID_ROLE\n" +
            "WHERE\n" +
            "    UM_ROLE_NAME = ? AND UM_TENANT_ID = ?";
    public static final String FIND_GROUP_ID_FROM_ROLE_NAME =
            "SELECT\n" +
            "    ATTR_VALUE\n" +
            "FROM\n" +
            "    IDN_SCIM_GROUP\n" +
            "WHERE\n" +
            "    ROLE_NAME = ? AND TENANT_ID = ? AND ATTR_NAME = 'urn:ietf:params:scim:schemas:core:2.0:id'";
}
