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

package org.wso2.carbon.identity.organization.user.role.mgt.core.constant;

/**
 * SQL Constants.
 */
public class SQLConstants {

    public static final String COUNT_COLUMN_NAME = "COUNT(1)";
    public static final String INSERT_ALL = "INSERT ALL ";
    public static final String VIEW_ID_COLUMN = "UM_ID";
    public static final String VIEW_USER_ID_COLUMN = "UM_USER_ID";
    public static final String VIEW_ROLE_ID_COLUMN = "UM_ROLE_ID";
    public static final String VIEW_ROLE_NAME_COLUMN = "UM_ROLE_NAME";
    public static final String VIEW_INHERIT_COLUMN = "INHERIT";
    public static final String VIEW_ASSIGNED_AT_COLUMN = "ASSIGNED_AT";
    public static final String VIEW_ASSIGNED_AT_NAME_COLUMN = "NAME";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String ORG_ID_ADDING = "ORG_ID = ?";
    public static final String ASSIGNED_AT_ADDING = "ASSIGNED_AT = ?";
    public static final String INHERIT_ADDING = "INHERIT = ?";

    public static final String INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING =
            "INTO UM_USER_ROLE_ORG " +
                    "(UM_ID, UM_USER_ID, UM_ROLE_ID, UM_HYBRID_ROLE_ID, UM_TENANT_ID, ORG_ID, ASSIGNED_AT, INHERIT) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
    public static final String INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING_USING_SP =
            "{call add_org_user_role_mapping(?,?,?,?,?,?)}";

    public static final String SELECT_DUMMY_RECORD = "SELECT 1 FROM DUAL";
    public static final String INSERT_ORGANIZATION_USER_ROLE_MAPPING =
            "INSERT INTO\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "    (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_TENANT_ID, ORG_ID)\n" +
                    "VALUES\n" +
                    "    (?, ?, ?, ?, ?)";
    public static final String DELETE_ORGANIZATION_USER_ROLE_MAPPINGS_ASSIGNED_AT_ORG_LEVEL =
            "DELETE\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ? AND ASSIGNED_AT = ?";
    public static final String DELETE_ALL_ORGANIZATION_USER_ROLE_MAPPINGS_BY_USERID =
            "DELETE\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_TENANT_ID = ?";
    public static final String GET_ORGANIZATION_USER_ROLE_MAPPING =
            "SELECT\n" +
                    "    COUNT(1)\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ? AND ORG_ID = ?";
    public static final String UPDATE_ORGANIZATION_USER_ROLE_MAPPING_INHERIT_PROPERTY =
            "UPDATE\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "SET\n" +
                    "    INHERIT = ?\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND ORG_ID = ? AND ASSIGNED_AT = ? AND UM_TENANT_ID = ?";
    public static final String GET_DIRECTLY_ASSIGNED_ORGANIZATION_USER_ROLE_MAPPING_INHERITANCE =
            "SELECT\n" +
                    "    INHERIT\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ? AND ORG_ID = ? AND ASSIGNED_AT = ?";
    public static final String GET_ORGANIZATION_BY_USER_AND_ROLE =
            "SELECT\n" +
                    "    ORG_ID\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ?";
    public static final String GET_ROLE_ID_BY_SCIM_GROUP_NAME =
            "SELECT\n" +
                    "    UM_ID\n" +
                    "FROM\n" +
                    "    UM_HYBRID_ROLE\n" +
                    "WHERE\n" +
                    "    UM_ROLE_NAME = ? AND UM_TENANT_ID = ?";
    public static final String  GET_USERS_BY_ORG_AND_ROLE =
            "SELECT\n" +
                    "    URO.UM_USER_ID, URO.INHERIT,  URO.ASSIGNED_AT, UO.NAME \n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG URO LEFT JOIN UM_ORG UO ON URO.ASSIGNED_AT = UO.ID\n" +
                    "WHERE\n" +
                    "    URO.ORG_ID = ? AND URO.UM_ROLE_ID = ? AND URO.UM_TENANT_ID = ?";
    //TODO: may be doing with a join will improve perf
    public static final String GET_ROLES_BY_ORG_AND_USER =
            "SELECT\n" +
                    "    DISTINCT UM_ROLE_ID, UM_ROLE_NAME\n" +
                    "FROM\n" +
                    "    ORG_AUTHZ_VIEW\n" +
                    "WHERE\n" +
                    "    ORG_ID = ? AND UM_USER_ID = ? AND UM_TENANT_ID = ?";
    //TODO: may be group by clause will help
    public static final String GET_ROLES_WITH_INHERITANCE_BY_ORG_AND_USER =
            "SELECT\n" +
                    "    DISTINCT UM_ROLE_ID, UM_ROLE_NAME\n" +
                    "FROM\n" +
                    "    ORG_AUTHZ_VIEW\n" +
                    "WHERE\n" +
                    "    ORG_ID = ? AND UM_USER_ID = ? AND UM_TENANT_ID = ?";
    public static final String PAGINATION =
            "%n OFFSET" +
                    "%n   %s ROWS" +
                    "%n FETCH NEXT" +
                    "%n   %s ROWS ONLY";
    public static final String UPSERT_UM_USER_ROLE_ORG_BASE = "MERGE INTO UM_USER_ROLE_ORG T USING ";
    public static final String UNION_ALL = " UNION ALL ";
    public static final String UM_USER_ROLE_ORG_DATA = "SELECT ? UM_ID, ? UM_USER_ID, ? UM_ROLE_ID, " +
            "? UM_HYBRID_ROLE_ID, ? UM_TENANT_ID, ? ORG_ID, ? ASSIGNED_AT, ? INHERIT from dual";
    public static final String UPSERT_UM_USER_ROLE_ORG_END = " S ON \n" +
            "(T.UM_USER_ID = S.UM_USER_ID AND T.UM_HYBRID_ROLE_ID = S.UM_HYBRID_ROLE_ID AND " +
            "T.UM_TENANT_ID = S.UM_TENANT_ID AND T.ORG_ID = S.ORG_ID AND T.ASSIGNED_AT = S.ASSIGNED_AT AND " +
            "T.INHERIT = S.INHERIT)\n" +
            "WHEN NOT MATCHED THEN INSERT (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_HYBRID_ROLE_ID, UM_TENANT_ID, " +
            "ORG_ID, ASSIGNED_AT, INHERIT)\n" +
            "VALUES (S.UM_ID, S.UM_USER_ID, S.UM_ROLE_ID, S.UM_HYBRID_ROLE_ID, S.UM_TENANT_ID, S.ORG_ID, " +
            "S.ASSIGNED_AT, S.INHERIT)";
}
