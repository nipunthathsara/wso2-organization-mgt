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

    /**
     * Error messages.
     */
    public enum ErrorMessages {

        // Client errors (ORGPERMMGT_00001-ORGPERMMGT_00019)
        ERROR_CODE_ADD_NONE_INTERNAL_ERROR("ORGPERMMGT_00001",
                "Only internal roles are allowed. Role : %s is not an internal role."),
        ERROR_CODE_INVALID_ROLE_ID_ERROR("ORGPERMMGT_00002", "Invalid role id: %s"),

        // Server errors (ORGPERMMGT_00020-ORGPERMMGT_00040)
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_ADD_ERROR("ORGPERMMGT_00020",
                "Error while creating the role mapping for organization : %s"),
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_DELETE_ERROR("ORGPERMMGT_00021",
                "Error while deleting the role : %s, for user : %s for organization : %s"),
        ERROR_CODE_ORGANIZATION_USER_ROLE_MAPPINGS_RETRIEVING_ERROR("ORGPERMMGT_00022",
                "Error while retrieving the role : %s, for user : %s for organization : %s"),
        ERROR_CODE_HYBRID_ROLE_ID_RETRIEVING_ERROR("ORGPERMMGT_00023",
                "Error while retrieving the hybrid role id for role : %s"),
        ERROR_CODE_INVALID_ROLE_ERROR("ORGPERMMGT_00024", "Invalid role name: %s"),
        ERROR_CODE_USERS_PER_ORG_ROLE_RETRIEVING_ERROR("ORGPERMMGT_00025",
                "Error while retrieving users for role: %s , organization : $s"),
        ERROR_CODE_ROLES_PER_ORG_USER_RETRIEVING_ERROR("ORGPERMMGT_00026",
                "Error while retrieving roles for user: %s , organization : $s");

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
