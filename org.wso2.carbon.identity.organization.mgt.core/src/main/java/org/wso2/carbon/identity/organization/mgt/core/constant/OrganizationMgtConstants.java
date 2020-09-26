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
    public static final String DN_PLACE_HOLDER = "ou=%s,";
    public static final String SCIM2_USER_RESOURCE_BASE_PATH = "/t/%s/scim2/Users/%s";
    public static final String ORGANIZATION_RESOURCE_BASE_PATH = "/t/%s/api/identity/organization-mgt/v1.0/organizations/%s";
    public static final String ACCOUNT_DISABLED_CLAIM_URI = "http://wso2.org/claims/identity/accountDisabled";
    /**
     *          <OrganizationMgt>
     *             <AttributeValidatorClass>org.wso2.carbon.identity.organization.mgt.core.validator.AttributeValidatorImpl</AttributeValidatorClass>
     *         </OrganizationMgt>
     */
    public static final String ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME = "org-mgt-attributes.properties";
    public static final String ORGANIZATION_ATTRIBUTE_VALIDATOR = "OrganizationMgt.AttributeValidatorClass";
    public static final String DEFAULT_ATTRIBUTE_VALIDATOR_CLASS = "org.wso2.carbon.identity.organization.mgt.core.validator.AttributeValidatorImpl";

    public static final String PATCH_OP_ADD = "add";
    public static final String PATCH_OP_REMOVE = "remove";
    public static final String PATCH_OP_REPLACE = "replace";
    public static final String PATCH_PATH_ORG_NAME = "/name";
    public static final String PATCH_PATH_ORG_DISPLAY_NAME = "/displayName";
    public static final String PATCH_PATH_ORG_DESCRIPTION = "/description";
    public static final String PATCH_PATH_ORG_STATUS = "/status";
    public static final String PATCH_PATH_ORG_PARENT_ID = "/parent/id";
    public static final String PATCH_PATH_ORG_ATTRIBUTES = "/attributes/";

    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_NAME = "name";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_DISPLAY_NAME = "displayName";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION = "description";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_STATUS = "status";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID = "parentId";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_NAME = "parentName";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_DISPLAY_NAME = "parentDisplayName";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_CREATED = "created";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED = "lastModified";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_CREATED_BY_ID = "createdBy";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED_BY_ID = "lastModifiedBy";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY = "attributeKey";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE = "attributeValue";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_KEY = "userStoreConfigKey";
    public static final String ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_VALUE = "userStoreConfigValue";

    public static final String ORGANIZATION_BASE_PERMISSION = "/permission/admin/organizations";
    public static final String ORGANIZATION_VIEW_PERMISSION = "/permission/admin/organizations/view";
    public static final String ORGANIZATION_EDIT_PERMISSION = "/permission/admin/organizations/edit";
    public static final String ORGANIZATION_CREATE_PERMISSION = "/permission/admin/organizations/create";
    public static final String ORGANIZATION_DELETE_PERMISSION = "/permission/admin/organizations/delete";
    public static final String UI_EXECUTE = "ui.execute";

    public enum ErrorMessages {

        // Client errors (ORGMGT_00001-ORGMGT_00019)
        ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST("ORGMGT_00001", "Invalid organization add request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_IMPORT_REQUEST("ORGMGT_00002", "Invalid organization import request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_GET_REQUEST("ORGMGT_00003", "Invalid organization search/get request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_GET_BY_ID_REQUEST("ORGMGT_00004", "Invalid get organization by ID request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_PATCH_REQUEST("ORGMGT_00005", "Invalid organization patch request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_DELETE_REQUEST("ORGMGT_00006", "Invalid organization delete request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_CHILDREN_GET_REQUEST("ORGMGT_00007", "Invalid get child organizations request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_CONFIG_GET_REQUEST("ORGMGT_00008", "Invalid get organization configs request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_CONFIG_PATCH_REQUEST("ORGMGT_00009", "Invalid patch organization config request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_USER_STORE_CONFIGURATIONS("ORGMGT_00010", "Invalid user store configurations : %s"),
        ERROR_CODE_SQL_QUERY_LIMIT_EXCEEDED("ORGMGT_00011", "Request caused an SQL query limit exceed : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_GET_ID_BY_NAME_REQUEST("ORGMGT_00012", "Invalid get organization Id by name request : %s"),
        ERROR_CODE_INVALID_ATTRIBUTES("ORGMGT_00013", "Attribute validation failed : %s"),
        ERROR_CODE_UNAUTHORIZED_ACTION("ORGMGT_00014", "Unauthorized action : %s"),

        // Server errors (ORGMGT_00020-ORGMGT_00040)
        ERROR_CODE_ORGANIZATION_ADD_ERROR("ORGMGT_00020", "Error while creating the organization : %s"),
        ERROR_CODE_ORGANIZATION_IMPORT_ERROR("ORGMGT_00021", "Error while importing the organization : %s"),
        ERROR_CODE_ORGANIZATION_GET_ERROR("ORGMGT_00022", "Error while retrieving/searching the organizations : %s"),
        ERROR_CODE_ORGANIZATION_GET_BY_ID_ERROR("ORGMGT_00023", "Error while retrieving the organization : %s"),
        ERROR_CODE_ORGANIZATION_DELETE_ERROR("ORGMGT_00024", "Error while deleting the organization : %s"),
        ERROR_CODE_ORGANIZATION_PATCH_ERROR("ORGMGT_00025", "Error while patching the organization : %s"),
        ERROR_CODE_ORGANIZATION_GET_CHILDREN_ERROR("ORGMGT_00026", "Error while retrieving the child organizations : %s"),
        ERROR_CODE_ORGANIZATION_GET_CONFIGS_ERROR("ORGMGT_00027", "Error while retrieving the organization configs : %s"),
        ERROR_CODE_ORGANIZATION_PATCH_CONFIGS_ERROR("ORGMGT_00028", "Error while patching the organization configs : %s"),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_ID_ERROR("ORGMGT_00029", "Error while checking if the organization id exist : %s"),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_NAME_ERROR("ORGMGT_00030", "Error while checking if the organization name exist : %s"),
        ERROR_CODE_USER_STORE_CONFIGURATIONS_ERROR("ORGMGT_00031", "User store configurations error : %s"),
        ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR("ORGMGT_00032", "Error while checking if the attribute exist : %s"),
        ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR("ORGMGT_00033", "Error while retrieving organization Id by name : %s"),
        ERROR_CODE_ATTRIBUTE_VALIDATION_ERROR("ORGMGT_00034", "Error while validating attributes : %s"),
        ERROR_CODE_USER_STORE_OPERATIONS_ERROR("ORGMGT_00035", "Error accessing user store : %s"),
        ERROR_CODE_CHECK_RDN_AVAILABILITY_ERROR("ORGMGT_00036", "Error checking RDN availability : %s"),
        ERROR_CODE_CHECK_USER_AUTHORIZED_ERROR("ORGMGT_00037", "Error while checking if the user is authorized : %s"),

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
