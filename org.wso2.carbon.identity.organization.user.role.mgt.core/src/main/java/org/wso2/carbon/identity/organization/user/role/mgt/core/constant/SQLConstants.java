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

public class SQLConstants {

    public static final String COUNT_COLUMN_NAME = "COUNT(1)";
    public static final String INSERT_ALL = "INSERT ALL ";
    public static final String INSERT_INTO_ORGANIZATION_USER_ROLE_MAPPING =
            "INTO UM_USER_ROLE_ORG (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_TENANT_ID, ORG_ID) VALUES (?, ?, ?, ?, ?) ";
    public static final String SELECT_DUMMY_RECORD = "SELECT 1 FROM DUAL";
    public static final String INSERT_ORGANIZATION_USER_ROLE_MAPPING =
            "INSERT INTO\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "    (UM_ID, UM_USER_ID, UM_ROLE_ID, UM_TENANT_ID, ORG_ID)\n" +
                    "VALUES\n" +
                    "    (?, ?, ?, ?, ?)";
    public static final String DELETE_ORGANIZATION_USER_ROLE_MAPPING =
            "DELETE\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ? AND ORG_ID = ?";
    public static final String GET_ORGANIZATION_USER_ROLE_MAPPING =
            "SELECT\n" +
                    "    COUNT(1)\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ? AND ORG_ID = ?";
    public static final String GET_ORGANIZATION_BY_USER_AND_ROLE =
            "SELECT\n" +
                    "    ORG_ID\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    UM_USER_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ?";
    public static final String GET_USERS_BY_ORG_AND_ROLE =
            "SELECT\n" +
                    "    UM_USER_ID\n" +
                    "FROM\n" +
                    "    UM_USER_ROLE_ORG\n" +
                    "WHERE\n" +
                    "    ORG_ID = ? AND UM_ROLE_ID = ? AND UM_TENANT_ID = ?";
}
