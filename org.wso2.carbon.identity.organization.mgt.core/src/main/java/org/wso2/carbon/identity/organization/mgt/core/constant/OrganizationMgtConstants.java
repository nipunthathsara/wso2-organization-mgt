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

public class OrganizationMgtConstants {

    public static final String ROOT = "ROOT";
    public static final String USER_STORE_DOMAIN = "USER_STORE_DOMAIN";
    public static final String RDN = "RDN";
    public static final String DN = "DN";
    public static final String PRIMARY = "PRIMARY";
    public static final String UNIQUE_ID_READ_WRITE_LDAP_USER_STORE_CLASS_NAME = "org.wso2.carbon.user.core.ldap.UniqueIDReadWriteLDAPUserStoreManager";
    public static final String READ_WRITE_LDAP_USER_STORE_CLASS_NAME = "org.wso2.carbon.user.core.ldap.ReadWriteLDAPUserStoreManager";

    public static final String PATCH_OP_ADD = "add";
    public static final String PATCH_OP_REMOVE = "remove";
    public static final String PATCH_OP_REPLACE = "replace";
    public static final String PATCH_PATH_ORG_NAME = "/name";
    public static final String PATCH_PATH_ORG_DESCRIPTION = "/description";
    public static final String PATCH_PATH_ORG_ACTIVE = "/active";
    public static final String PATCH_PATH_ORG_PARENT_ID = "/parentId";
    public static final String PATCH_PATH_ORG_ATTRIBUTES = "/attributes/";

    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_NAME = "name";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION = "description";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID = "parentId";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_ACTIVE = "active";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY = "attributeKey";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE = "attributeValue";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_KEY = "userStoreConfigKey";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_VALUE = "userStoreConfigValue";

    public enum ErrorMessages {

        ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID("ORGMGT_00001", "Invalid organization creation request : %s."),
        ERROR_CODE_ORGANIZATION_ALREADY_EXISTS_ERROR("ORGMGT_00002", "Organization already exists : %s."),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR("ORGMGT_00003", "Error while checking if the organization exist : %s."),
        ERROR_CODE_INSERT_ORGANIZATION_ERROR("ORGMGT_00004", "Error creating the organization : %s."),
        ERROR_CODE_QUERY_LENGTH_EXCEEDED_ERROR("CONFIGM_00005", "SQL query length too large."),
        ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR("CONFIGM_00006", "Invalid organization ID : %s."),
        ERROR_CODE_DELETE_ORGANIZATION_ERROR("CONFIGM_00007", "Error while deleting the organization : %s."),
        ERROR_CODE_RETRIEVE_USER_STORE_CONFIGS_BY_ORG_ID_ERROR("ORGMGT_00008", "Error retrieving user store configs by organization Id : %s."),
        ERROR_CODE_RETRIEVE_ORGANIZATION_BY_ID_ERROR("ORGMGT_00009", "Error retrieving organization by Id : %s."),
        ERROR_CODE_ORG_ID_NOT_FOUND("ORGMGT_00010", "Organization ID not found within this tenant : %s."),
        ERROR_CODE_INVALID_SORTING("ORGMGT_00011", "Invalid sorting arguments : %s."),
        ERROR_CODE_INVALID_PAGINATION("ORGMGT_00012", "Invalid pagination arguments : %s."),
        ERROR_CODE_RETRIEVE_ORGANIZATIONS_ERROR("ORGMGT_00013", "Error retrieving organizations : %s."),
        ERROR_CODE_USER_STORE_ACCESS_ERROR("ORGMGT_00014", "Error accessing user store : %s."),
        ERROR_CODE_RETRIEVING_CHILD_ORGANIZATION_IDS_ERROR("ORGMGT_00015", "Error retrieving child organization of organization ID : %s."),
        ERROR_CODE_PATCH_OPERATION_ERROR("ORGMGT_00016", "Error performing the patch operations : %s."),
        ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR("ORGMGT_00017", "Error while checking if the attribute exist : %s."),
        ERROR_CODE_SEARCH_ORGANIZATION_ERROR("ORGMGT_00018", "Error while searching organizations : %s."),

        ERROR_CODE_UNEXPECTED("ORGMGT_00050", "Unexpected Error");

        private final String code;
        private final String message;

        ErrorMessages(String code, String message) {

            this.code = code;
            this.message = message;
        }

        public String getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }
    }
}
