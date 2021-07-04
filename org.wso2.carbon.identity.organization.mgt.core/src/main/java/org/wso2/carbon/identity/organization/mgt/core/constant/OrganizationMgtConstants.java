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

/**
 * Organization Mgt Constants.
 */
public class OrganizationMgtConstants {

    public static final String ROOT = "ROOT";
    public static final String USER_STORE_DOMAIN = "USER_STORE_DOMAIN";
    public static final String RDN = "RDN";
    public static final String DN = "DN";
    public static final String PRIMARY = "PRIMARY";
    public static final String DN_PLACE_HOLDER = "ou=%s,";
    public static final String SCIM2_USER_RESOURCE_BASE_PATH = "/t/%s/scim2/Users/%s";
    public static final String ORGANIZATION_RESOURCE_BASE_PATH =
            "/t/%s/api/identity/organization-mgt/v1.0/organizations/%s";
    public static final String ACCOUNT_DISABLED_CLAIM_URI = "http://wso2.org/claims/identity/accountDisabled";

    public static final String CASCADED_CHILD_ORG_LIST_CACHE_KEY_FORMAT = "cascaded-children-%s";
    public static final String INSERT_ROLES_WITH_STORED_PROCEDURE = "useSpForInsertOrgRoleMapping";
    public static final String IS_VIEWS_IN_USE = "useViewsForSelectOps";

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

    // Permission strings
    public static final String PERMISSION = "/permission";
    public static final String ADMIN_PERMISSION = "/permission/admin";
    public static final String ADMIN_MANAGE_PERMISSION = "/permission/admin/manage";
    public static final String ADMIN_MANAGE_IDENTITY_PERMISSION = "/permission/admin/manage/identity";
    // organization permissions
    public static final String ORGANIZATION_BASE_PERMISSION = "/permission/admin/manage/identity/organizationmgt";
    public static final String ORGANIZATION_ADMIN_PERMISSION =
            "/permission/admin/manage/identity/organizationmgt/admin";
    public static final String ORGANIZATION_CREATE_PERMISSION =
            "/permission/admin/manage/identity/organizationmgt/create";
    public static final String ORGANIZATION_VIEW_PERMISSION = "/permission/admin/manage/identity/organizationmgt/view";
    public static final String ORGANIZATION_UPDATE_PERMISSION =
            "/permission/admin/manage/identity/organizationmgt/update";
    public static final String ORGANIZATION_DELETE_PERMISSION =
            "/permission/admin/manage/identity/organizationmgt/delete";
    // User permissions
    public static final String USER_MGT_BASE_PERMISSION = "/permission/admin/manage/identity/usermgt";
    public static final String USER_MGT_CREATE_PERMISSION = "/permission/admin/manage/identity/usermgt/create";
    public static final String USER_MGT_VIEW_PERMISSION = "/permission/admin/manage/identity/usermgt/view";
    public static final String USER_MGT_LIST_PERMISSION = "/permission/admin/manage/identity/usermgt/list";
    public static final String USER_MGT_UPDATE_PERMISSION = "/permission/admin/manage/identity/usermgt/update";
    public static final String USER_MGT_DELETE_PERMISSION = "/permission/admin/manage/identity/usermgt/delete";
    // Role permissions
    public static final String ROLE_MGT_BASE_PERMISSION = "/permission/admin/manage/identity/rolemgt";
    public static final String ROLE_MGT_CREATE_PERMISSION = "/permission/admin/manage/identity/rolemgt/create";
    public static final String ROLE_MGT_VIEW_PERMISSION = "/permission/admin/manage/identity/rolemgt/view";
    public static final String ROLE_MGT_UPDATE_PERMISSION = "/permission/admin/manage/identity/rolemgt/update";
    public static final String ROLE_MGT_DELETE_PERMISSION = "/permission/admin/manage/identity/rolemgt/delete";
    // User role management permissions
    public static final String USER_ROLE_MGT_BASE_PERMISSION = "/permission/admin/manage/identity/userrolemgt";
    public static final String USER_ROLE_MGT_VIEW_PERMISSION = "/permission/admin/manage/identity/userrolemgt/view";
    public static final String USER_ROLE_MGT_CREATE_PERMISSION = "/permission/admin/manage/identity/userrolemgt/create";
    public static final String USER_ROLE_MGT_DELETE_PERMISSION = "/permission/admin/manage/identity/userrolemgt/delete";
    public static final String USER_ROLE_MGT_UPDATE_PERMISSION = "/permission/admin/manage/identity/userrolemgt/update";

    public static final String UI_EXECUTE = "ui.execute";

    /**
     *  <IS_HOME>/repository/resources/conf/templates/repository/conf/identity/identity.xml.j2
     *
     *     <!--Organization management properties-->
     *     <OrganizationMgt>
     *         <OrgNameClaimUri>{{organization.mgt.org_name_claim_uri}}</OrgNameClaimUri>
     *         <OrgIdClaimUri>{{organization.mgt.org_id_claim_uri}}</OrgIdClaimUri>
     *         <filterUsersByOrganizationName>{{organization.mgt.filter_by_org_name}}</filterUsersByOrganizationName>
     *     </OrganizationMgt>
     *
     *  <IS_HOME>>/repository/conf/deployment.toml
     *
     *      [organization.mgt]
     *      org_name_claim_uri = "http://wso2.org/claims/organizationName"
     *      org_id_claim_uri = "http://wso2.org/claims/organizationId"
     *      filter_by_org_name = "false"
     */
    // Organization mgt claim details
    public static final String ORGANIZATION_ID_CLAIM_URI = "OrganizationMgt.OrgIdClaimUri";
    public static final String ORGANIZATION_ID_DEFAULT_CLAIM_URI = "http://wso2.org/claims/organizationId";
    public static final String ORGANIZATION_NAME_CLAIM_URI = "OrganizationMgt.OrgNameClaimUri";
    public static final String ORGANIZATION_NAME_DEFAULT_CLAIM_URI = "http://wso2.org/claims/organization";
    public static final String FILTER_USERS_BY_ORG_NAME = "OrganizationMgt.filterUsersByOrganizationName";

    /**
     * Error Messages.
     */
    public enum ErrorMessages {

        // Client errors (ORG-60001 - ORG-60999)
        ADD_REQUEST_UNEXPECTED_DN_PARAMETER("ORG-60001", "DN defined in the organization create request.",
                "DN parameter is only acceptable in '/import' requests."),
        ADD_REQUEST_REQUIRED_FIELDS_MISSING("ORG-60002", "Missing required fields.",
                "Missing parameters : %s"),
        ADD_REQUEST_MISSING_ATTRIBUTE_KEY("ORG-60003", "Attribute key is missing.",
                "Attribute keys cannot be empty."),
        ADD_REQUEST_DUPLICATE_ATTRIBUTE_KEYS("ORG-60004", "Attribute keys are duplicated.",
                "Attribute keys cannot be duplicated."),
        ADD_REQUEST_MISSING_USER_STORE_CONFIG_KEY_OR_VALUE("ORG-60005", "Missing user store config key/value.",
                "User store config keys/values cannot be empty."),
        ADD_REQUEST_INVALID_PARENT_ORGANIZATION("ORG-60006", "Invalid parent organization.",
                "Defined parent organization doesn't exist in this tenant."),
        ADD_REQUEST_DISABLED_PARENT_ORGANIZATION("ORG-60007", "Parent organization is disabled.",
                "Defined parent organization is not ACTIVE."),
        ADD_REQUEST_INCOMPATIBLE_USER_STORE_DOMAIN("ORG-60008", "Incompatible user store domains.",
                "%s"),
        ADD_REQUEST_INCOMPATIBLE_USER_STORE_MANAGER("ORG-60009", "Incompatible user store manager.", "%s"),
        IMPORT_REQUEST_REQUIRED_FIELDS_MISSING("ORG-60010", "Missing required fields.",
                "RDN parameter is mandatory to import an organization."),
        LIST_REQUEST_BAD_FILTER("ORG-60011", "Bad filter", "Bad filter"),
        LIST_REQUEST_INVALID_SORT_PARAMETER("ORG-60012", "Invalid sorting parameter.", "%s"),
        LIST_REQUEST_INVALID_FILTER_PARAMETER("ORG-60013", "Invalid filter parameter",
                "Error passing the filter condition."),
        LIST_REQUEST_INVALID_PAGINATION_PARAMETER("ORG-60014", "Invalid pagination parameters.",
                "'limit' should be greater than 0 and 'offset' should be greater than -1"),
        LIST_REQUEST_INVALID_DATE_FILTER("ORG-60015", "Invalid filter parameter.", "%s"),
        GET_REQUEST_ORGANIZATION_ID_UNDEFINED("ORG-60016", "Empty organization ID.",
                "Provided organization ID is empty."),
        PATCH_REQUEST_ORGANIZATION_ID_UNDEFINED("ORG-60017", "Empty organization ID.",
                "Provided organization ID is empty."),
        PATCH_REQUEST_OPERATION_UNDEFINED("ORG-60018", "Missing patch operation.",
                "Patch operation is not defined."),
        PATCH_REQUEST_INVALID_OPERATION("ORG-60019", "Invalid patch operation.", "%s"),
        PATCH_REQUEST_PATH_UNDEFINED("ORG-60020", "Empty patch path.", "Patch path is not defined."),
        PATCH_REQUEST_INVALID_PATH("ORG-60021", "Invalid patch path.", "%s"),
        PATCH_REQUEST_VALUE_UNDEFINED("ORG-60022", "Missing required value.",
                "Value is mandatory for 'add' and 'replace' operations."),
        PATCH_REQUEST_INVALID_REMOVE_OPERATION("ORG-60023", "Cannot remove mandatory fields.", "%s"), // 2 usages
        PATCH_REQUEST_INVALID_STATUS("ORG-60024", "Invalid organization status.", "%s"),
        PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_CHILD("ORG-60025",
                "Cannot disable with active child organization/s.",
                "Organization has one or more active child organization/s."),
        PATCH_REQUEST_CANNOT_DISABLE_WITH_ACTIVE_USERS("ORG-60026", "Cannot disable with active user/s.",
                "Organization has one or more active user/s."),
        PATCH_REQUEST_CANNOT_ACTIVATE_WITH_DISABLED_PARENT("ORG-60027", "Cannot activate under a disabled parent.",
                "Cannot activate the organization as its parent organization is not ACTIVE."),
        PATCH_REQUEST_INVALID_PARENT("ORG-60028", "Invalid parent organizations.",
                "Provided parent ID doesn't represent an ACTIVE organization."),
        PATCH_REQUEST_NAME_UNAVAILABLE("ORG-60029", "Organization name unavailable.",
                "Provided organization name already exists."),
        PATCH_REQUEST_ATTRIBUTE_KEY_UNDEFINED("ORG-60030", "Missing attribute key", "%s"),
        PATCH_REQUEST_INVALID_ATTRIBUTE_KEY("ORG-60031", "Invalid attribute key", "%s"),
        DELETE_REQUEST_ORGANIZATION_ID_UNDEFINED("ORG-60032", "Empty organization ID",
                "Provided organization ID is empty."),
        DELETE_REQUEST_ACTIVE_ORGANIZATION("ORG-60033", "Organization is active",
                "Organization is not in the disabled status."),
        DELETE_REQUEST_CANNOT_DELETE_WITH_ACTIVE_CHILD("ORG-60034", "Cannot delete with active children",
                "Organization has one or more child organization/s."),
        DELETE_REQUEST_CANNOT_DELETE_WITH_USERS("ORG-60035", "Cannot delete with user/s",
                "Organization has one or more user/s."),
        DELETE_REQUEST_UNSUPPORTED_USER_STORE_MANAGER("ORG-60036", "Unsupported user store manager", "%s"),
        GET_CHILDREN_REQUEST_ORGANIZATION_UNDEFINED("ORG-60037", "Empty organization ID",
                "Provided organization ID is empty."),
        GET_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED("ORG-60038", "Empty organization ID",
                "Provided organization ID is empty."),
        PATCH_USER_STORE_CONFIGS_ORGANIZATION_UNDEFINED("ORG-60039", "Empty organization ID",
                "Provided organization ID is empty."),
        INVALID_USER_STORE_DOMAIN("ORG-60040", "Invalid user store domain", "%s"),
        UNSUPPORTED_USER_STORE_DOMAIN("ORG-60041", "Unsupported user store domain", "%s"),
        LIST_REQUEST_FILTER_TOO_LONG("ORG-60042", "Filter too long", "%s"), // Duplicate this everywhere. fix later
        GET_ID_BY_NAME_REQUEST_ORGANIZATION_UNDEFINED("ORG-60043", "Empty organization ID",
                "Provided organization ID is empty"),
        ATTRIBUTE_VALIDATION_INVALID_ATTRIBUTE("ORG-60044", "Invalid attribute", "%s"),
        ADD_REQUEST_UNAUTHORIZED_PARENT("ORG-60045", "Unauthorized parent organization",
                "User is not authorized to create organizations under this parent."),
        ADD_REQUEST_NAME_CONFLICT("ORG-60046", "Organization already exists",
                "Provided organization name already exists under this tenant."),
        ADD_REQUEST_RDN_CONFLICT("ORG-60047", "RDN is not available",
                "Provided RDN is not available under this parent organization"),
        GET_REQUEST_INVALID_ORGANIZATION("ORG-60048", "Invalid organization",
                "Provided organization doesn't exist in this tenant"), // 404
        GET_ORG_BY_NAME_REQUEST_INVALID_ORGANIZATION("ORG-60049", "Invalid organization",
                "Provided organization doesn't exist in this tenant."), // 404
        PATCH_REQUEST_INVALID_ORGANIZATION("ORG-60050", "Invalid organization",
                "Provided organization doesn't exist in this tenant."), // 404
        DELETE_REQUEST_INVALID_ORGANIZATION("ORG-60051", "Invalid organization",
                "Provided organization doesn't exist in this tenant."), // 404
        GET_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION("ORG-60052", "Invalid organization",
                "Provided organization doesn't exist in this tenant."), // 404
        GET_CHILDREN_REQUEST_INVALID_ORGANIZATION("ORG-60053", "Invalid organization",
                "Provided organization doesn't exist in this tenant"), // 404
        PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_ORGANIZATION("ORG-60054", "Invalid organization",
                "Provided organization doesn't exist in this tenant"), // 404
        PATCH_USER_STORE_CONFIGS_REQUEST_OPERATION_UNDEFINED("ORG-60055", "Patch operation is not defined.",
                "Patch operation is not defined."),
        PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_OPERATION("ORG-60056", "Invalid patch operation",
                "Configuration patch may only contain 'replace' operation."),
        PATCH_USER_STORE_CONFIGS_REQUEST_PATH_UNDEFINED("ORG-60057", "Patch path undefined",
                "Patch operation path is not defined."),
        PATCH_USER_STORE_CONFIGS_REQUEST_INVALID_PATH("ORG-60058", "Invalid patch path", "%s"),
        PATCH_USER_STORE_CONFIGS_REQUEST_VALUE_UNDEFINED("ORG-60059", "Patch value undefined",
                "Patch operation value is not defined."),
        PATCH_USER_STORE_CONFIGS_REQUEST_RDN_UNAVAILABLE("ORG-60060", "LDAP directory unavailable", "%s"),
        INVALID_REQUEST("ORG-60061", "Invalid request", "Error while processing the request."),

        // Server errors (ORG-65001 - ORG-65999)
        ERROR_CODE_ORGANIZATION_ADD_ERROR("ORG-65001", "Error while creating the organization : %s", ""),
        ERROR_CODE_ORGANIZATION_IMPORT_ERROR("ORG-65002", "Error while importing the organization : %s", ""),
        ERROR_CODE_ORGANIZATION_GET_ERROR("ORG-65003", "Error while retrieving/searching the organizations : %s", ""),
        ERROR_CODE_ORGANIZATION_GET_BY_ID_ERROR("ORG-65004", "Error while retrieving the organization : %s", ""),
        ERROR_CODE_ORGANIZATION_DELETE_ERROR("ORG-65005", "Error while deleting the organization : %s", ""),
        ERROR_CODE_ORGANIZATION_PATCH_ERROR("ORG-65006", "Error while patching the organization : %s", ""),
        ERROR_CODE_ORGANIZATION_GET_CHILDREN_ERROR("ORG-65007",
                "Error while retrieving the child organizations : %s", ""),
        ERROR_CODE_ORGANIZATION_GET_CONFIGS_ERROR("ORG-65008",
                "Error while retrieving the organization configs : %s", ""),
        ERROR_CODE_ORGANIZATION_PATCH_CONFIGS_ERROR("ORG-65009",
                "Error while patching the organization configs : %s", ""),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_ID_ERROR("ORG-65010",
                "Error while checking if the organization id exist : %s", ""),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_BY_NAME_ERROR("ORG-65011",
                "Error while checking if the organization name exist : %s", ""),
        ERROR_CODE_USER_STORE_CONFIGURATIONS_ERROR("ORG-65012", "User store configurations error : %s", ""),
        ERROR_CODE_CHECK_ATTRIBUTE_EXIST_ERROR("ORG-65013",
                "Error while checking if the attribute exist : %s", ""),
        ERROR_CODE_ORGANIZATION_GET_ID_BY_NAME_ERROR("ORG-65014",
                "Error while retrieving organization Id by name : %s", ""),
        ERROR_CODE_EVENTING_ERROR("ORG-65015", "Error while handling the event : %s", ""),
        ERROR_CODE_USER_STORE_OPERATIONS_ERROR("ORG-65016", "Error accessing user store : %s", ""),
        ERROR_CODE_CHECK_RDN_AVAILABILITY_ERROR("ORG-65017", "Error checking RDN availability : %s", ""),
        ERROR_CODE_USER_ROLE_ORG_AUTHORIZATION_ERROR("ORG-65018", "Error while authorizing : %s", ""),
        ERROR_CODE_ORG_MGT_SERVER_CONFIG_ERROR("ORG-65019",
                "Organization Management Server configuration error : %s", ""),
        ERROR_CODE_RETRIEVING_AUTHORIZED_ORGANIZATION_LIST_ERROR("ORG-65020",
                "Error while retrieving authorized organizations list : %s", ""),
        ERROR_CODE_INITIALIZATION_ERROR("ORG-65021",
                "Error while initializing the organization mgt component : %s", ""),

        ERROR_CODE_UNEXPECTED("ORG-65022", "Unexpected Error", "");

        private final String code;
        private final String message;
        private final String description;

        ErrorMessages(String code, String message, String description) {

            this.code = code;
            this.message = message;
            this.description = description;
        }

        public String getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }

        public String getDescription() {

            return description;
        }
    }

    /**
     * Forbidden Error Messages
     */
    public enum ForbiddenErrorMessages {

        ORG_60045
    }

    /**
     * Not Found Error Messages
     */
    public enum NotFoundErrorMessages {

        ORG_60048, ORG_60049, ORG_60050, ORG_60051, ORG_60052, ORG_60053, ORG_60054
    }

    /**
     * Conflict Error Messages
     */
    public enum ConflictErrorMessages {

        ORG_60046, ORG_60047, ORG_60029
    }
}
