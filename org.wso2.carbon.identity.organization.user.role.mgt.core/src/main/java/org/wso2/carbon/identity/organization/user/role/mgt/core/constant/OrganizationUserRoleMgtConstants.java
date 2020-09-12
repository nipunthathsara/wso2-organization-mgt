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

public class OrganizationUserRoleMgtConstants {

    public enum ErrorMessages {

        // Client errors (ORGPERMMGT_00001-ORGPERMMGT_00019)
        ERROR_CODE_INVALID_ORGANIZATION_ADD_REQUEST("ORGPERMMGT_00001", "Invalid organization add request : %s"),

        // Server errors (ORGPERMMGT_00020-ORGPERMMGT_00040)
        ERROR_CODE_ORGANIZATION_AND_USER_ROLE_MAPPING_ADD_ERROR("ORGPERMMGT_00020", "Error while creating the role mapping for organization : %s, user: %s, role: %s");


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
