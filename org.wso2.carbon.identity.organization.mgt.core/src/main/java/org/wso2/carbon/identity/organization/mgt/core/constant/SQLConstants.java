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

import org.pdfbox.pdmodel.common.filespecification.PDSimpleFileSpecification;
import org.wso2.carbon.identity.core.util.IdentityUtil;

public class SQLConstants {

    // TODO Using the same config from the configuration store here.
    public static final String MAX_QUERY_LENGTH_IN_BYTES_SQL =
            IdentityUtil.getProperty("ConfigurationStore.MaximumQueryLengthInBytes");
    public static final String CHECK_ORGANIZATION_EXIST_BY_NAME =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    IDN_ORG\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND NAME = ?";
    public static final String CHECK_ORGANIZATION_EXIST_BY_ID =
            "SELECT\n" +
            "    COUNT(1)\n" +
            "FROM\n" +
            "    IDN_ORG\n" +
            "WHERE\n" +
            "    TENANT_ID = ? AND ID = ?";
    public static final String COUNT_COLUMN_NAME = "COUNT(1)";
    public static final String ORG_ID_COLUMN_NAME = "O.ID";
    public static final String ORG_NAME_COLUMN_NAME = "O.NAME";
    public static final String ORG_TENANT_ID_COLUMN_NAME = "O.TENANT_ID";
    public static final String ORG_CREATED_TIME_COLUMN_NAME = "O.CREATED_TIME";
    public static final String ORG_LAST_MODIFIED_COLUMN_NAME = "O.LAST_MODIFIED";
    public static final String ORG_HAS_ATTRIBUTE_COLUMN_NAME = "O.HAS_ATTRIBUTE";
    public static final String ORG_STATUS_COLUMN_NAME = "O.STATUS";
    public static final String ORG_PARENT_ID_COLUMN_NAME = "O.PARENT_ID";
    public static final String ATTR_ATTR_KEY_COLUMN_NAME = "A.ATTR_KEY";
    public static final String ATTR_ATTR_VALUE_COLUMN_NAME = "A.ATTR_VALUE";
    public static final String VIEW_ID = "ID";
    public static final String VIEW_TENANT_ID = "TENANT_ID";
    public static final String VIEW_NAME = "NAME";
    public static final String VIEW_DESCRIPTION = "DESCRIPTION";
    public static final String VIEW_CREATED_TIME = "CREATED_TIME";
    public static final String VIEW_LAST_MODIFIED = "LAST_MODIFIED";
    public static final String VIEW_HAS_ATTRIBUTES = "HAS_ATTRIBUTES";
    public static final String VIEW_ACTIVE = "ACTIVE";
    public static final String VIEW_PARENT_ID = "PARENT_ID";
    public static final String VIEW_ATTR_ID = "ATTR_ID";
    public static final String VIEW_ATTR_KEY = "ATTR_KEY";
    public static final String VIEW_ATTR_VALUE = "ATTR_VALUE";
    public static final String VIEW_CONFIG_ID = "CONFIG_ID";
    public static final String VIEW_CONFIG_KEY = "CONFIG_KEY";
    public static final String VIEW_CONFIG_VALUE = "CONFIG_VALUE";
    // View returns null for non matching entries upon join. Hence, NULL check.
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
            "    IDN_ORG\n" +
            "    (ID, TENANT_ID, NAME, DESCRIPTION, CREATED_TIME, LAST_MODIFIED, HAS_ATTRIBUTES, ACTIVE, PARENT_ID)\n" +
            "VALUES\n" +
            "    (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_ATTRIBUTES =
            "INSERT ALL\n";
    public static final String INSERT_ATTRIBUTE =
            "    INTO IDN_ORG_ATTRIBUTES (ID, ORG_ID, ATTR_KEY, ATTR_VALUE)\n" +
            "    VALUES (?, ?, ?, ?)\n";
    public static final String INSERT_ATTRIBUTES_CONCLUDE =
            "SELECT 1 FROM dual";
    public static final String INSERT_OR_UPDATE_USER_STORE_CONFIG =
        "MERGE INTO\n" +
        "    IDN_ORG_USERSTORE_CONFIGS C\n" +
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
    public static final String DELETE_ORGANIZATION_BY_ID =
            "DELETE\n" +
            "FROM\n" +
            "    IDN_ORG O\n" +
            "WHERE\n" +
            "    O.ID = ? AND O.TENANT_ID = ?;";
    public static final String DELETE_ATTRIBUTES_BY_ORG_ID =
            "DELETE\n" +
            "FROM\n" +
            "    IDN_ORG_ATTRIBUTES A\n" +
            "WHERE\n" +
            "    A.ORG_ID = ?";
    public static final String DELETE_DIRECTORY_INFO_BY_ORG_ID =
            "DELETE" +
                    "FROM" +
                    "   UM_USERSTORE_ORG_HIERARCHY U" +
                    "WHERE" +
                    "   U.ORG_ID = ?";
    public static final String GET_ORGANIZATION_BY_ID =
            "SELECT\n" +
            "    DISTINCT V.ID, V.NAME, V.DESCRIPTION, V.PARENT_ID, V.ACTIVE, V.LAST_MODIFIED, V.CREATED_TIME, V.HAS_ATTRIBUTES, V.ATTR_ID, V.ATTR_KEY, V.ATTR_VALUE\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.TENANT_ID = ? AND V.ID = ? AND V.ATTR_ID IS NOT NULL";
    public static final String FIND_CHILD_ORG_IDS =
            "SELECT\n" +
            "    O.ID\n" +
            "FROM\n" +
            "    IDN_ORG O\n" +
            "WHERE\n" +
            "    O.PARENT_ID = ?";
    public static final String GET_ALL_ORGANIZATION_IDS =
            "SELECT\n" +
            "    DISTINCT V.ID\n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.TENANT_ID = ?";
    public static final String ORDER_BY =
            "\nORDER BY" +
            "   \n%s %s";
    public static final String PAGINATION =
            "\nOFFSET" +
            "   \n%s ROWS" +
            "\nFETCH NEXT" +
            "   \n%s ROWS ONLY";
    public static final String GET_BASIC_ORGANIZATIONS_BY_IDS =
            "SELECT\n" +
            "    V.ID,\n" +
            "    V.NAME,\n" +
            "    V.CREATED_TIME,\n" +
            "    V.LAST_MODIFIED,\n" +
            "    V.HAS_ATTRIBUTE,\n" +
            "    V.STATUS,\n" +
            "    V.PARENT_ID \n" +
            "FROM\n" +
            "    ORG_MGT_VIEW V\n" +
            "WHERE\n" +
            "    V.TENANT_ID = 1234 AND V.ID IN ('1')";
}
