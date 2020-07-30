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

    public enum ErrorMessages {

        ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID("ORGMGT_00001", "Invalid organization creation request : %s."),
        ERROR_CODE_ORGANIZATION_ALREADY_EXISTS_ERROR("ORGMGT_00002", "Organization already exists : %s."),
        ERROR_CODE_CHECK_ORGANIZATION_EXIST_ERROR("ORGMGT_00003", "Error while checking if the organization exist : %s."),
        ERROR_CODE_INSERT_ORGANIZATION_ERROR("ORGMGT_00004", "Error creating the organization : %s."),

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
