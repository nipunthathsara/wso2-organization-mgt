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
 * Organization User Role Mgt Constants.
 */
public class OrganizationUserRoleMgtConstants {

    public static final String PATCH_OP_REPLACE = "replace";
    /**
     * Error messages.
     */
    public enum ErrorMessages {

        // Client errors (ORGPERMMGT_00001-ORGPERMMGT_00019)
        ERROR_CODE_ADD_NONE_INTERNAL_ERROR("ORGPERMMGT_00001",
                "Only internal roles are allowed. Role : %s is not an internal role."),
        ERROR_CODE_INVALID_ROLE_ID_ERROR("ORGPERMMGT_00002", "Invalid role id: %s"),
        ERROR_CODE_INVALID_USER_GET_REQUEST_FOR_ORG_ROLE("ORGPERMMGT_00003",
                "Invalid users search/get request for an organization's role: %s"),
        ERROR_NO_ROLE_MAPPING_FOUND("ORGPERMMGT_00004",
                "No organization user role mapping for an organization: %s, user: %s, role: %s"),
        ERROR_CODE_NON_EXISTING_USERID_ERROR("ORGPERMMGT_00005", "No user exists with user id: %s"),
        ERROR_CODE_INVALID_ROLE_ERROR("ORGPERMMGT_00006", "Invalid role id: %s"),
        ERROR_NO_DIRECTLY_ASSIGNED_ROLE_MAPPING_FOUND("ORGPERMMGT_00007",
                "No organization user role mapping for an organization: %s, user: %s, role: %s, " +
                        "directly assigned at organization: %s"),
        ERROR_CODE_INVALID_ORGANIZATION_USER_ROLE_PATCH_REQUEST("ORGPERMMGT_00008",
                "Invalid organization user role patch request : %s"),
        ERROR_CODE_INVALID_ORGANIZATION_USER_ROLE_POST_REQUEST("ORGPERMMGT_00009",
                "Invalid organization user role post request : %s"),

        // Server errors (ORGPERMMGT_00020-ORGPERMMGT_00040)
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR("ORGPERMMGT_00020",
                "Error while creating the role mappings"),
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR("ORGPERMMGT_00021",
                "Error while deleting the role : %s, for user : %s for organizations"),
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR("ORGPERMMGT_00022",
                "Error while retrieving the role : %s, for user : %s for organization : %s"),
        ERROR_CODE_HYBRID_ROLE_ID_RETRIEVING_ERROR("ORGPERMMGT_00023",
                "Error while retrieving the hybrid role id for role : %s"),
        ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR("ORGPERMMGT_00025",
                "Error while retrieving users for role: %s , organization : $s"),
        ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR("ORGPERMMGT_00026",
                "Error while retrieving roles for user: %s , organization : $s"),
        ERROR_CODE_EVENTING_ERROR("ORGPERMMGT_00027", "Error while handling the event : %s"),
        ERROR_CODE_USER_STORE_OPERATIONS_ERROR("ORGPERMMGT_00028", "Error accessing user store : %s"),
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_PER_USER_ERROR("ORGPERMMGT_00029",
                "Error while deleting organization user role mappings for user : %s"),;

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
