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
 * This class defines constants values for Organization Management eventing purposes.
 */
public class OrganizationMgtEventConstants {

    public static final String PRE_CREATE_ORGANIZATION = "PRE_CREATE_ORGANIZATION";
    public static final String POST_CREATE_ORGANIZATION = "POST_CREATE_ORGANIZATION";
    public static final String PRE_IMPORT_ORGANIZATION = "PRE_IMPORT_ORGANIZATION";
    public static final String POST_IMPORT_ORGANIZATION = "POST_IMPORT_ORGANIZATION";
    public static final String PRE_GET_ORGANIZATION = "PRE_GET_ORGANIZATION";
    public static final String POST_GET_ORGANIZATION = "POST_GET_ORGANIZATION";
    public static final String PRE_LIST_ORGANIZATIONS = "PRE_LIST_ORGANIZATIONS";
    public static final String POST_LIST_ORGANIZATIONS = "POST_LIST_ORGANIZATIONS";
    public static final String PRE_PATCH_ORGANIZATION = "PRE_PATCH_ORGANIZATION";
    public static final String POST_PATCH_ORGANIZATION = "POST_PATCH_ORGANIZATION";
    public static final String PRE_DELETE_ORGANIZATION = "PRE_DELETE_ORGANIZATION";
    public static final String POST_DELETE_ORGANIZATION = "POST_DELETE_ORGANIZATION";
    public static final String PRE_GET_USER_STORE_CONFIGS = "PRE_GET_USER_STORE_CONFIGS";
    public static final String POST_GET_USER_STORE_CONFIGS = "POST_GET_USER_STORE_CONFIGS";
    public static final String PRE_PATCH_USER_STORE_CONFIGS = "PRE_PATCH_USER_STORE_CONFIGS";
    public static final String POST_PATCH_USER_STORE_CONFIGS = "POST_PATCH_USER_STORE_CONFIGS";
    public static final String PRE_GET_CHILD_ORGANIZATIONS = "PRE_GET_CHILD_ORGANIZATIONS";
    public static final String POST_GET_CHILD_ORGANIZATIONS = "POST_GET_CHILD_ORGANIZATIONS";

    public static final String USER_NAME = "username";
    public static final String TENANT_DOMAIN = "tenantDomain";
    public static final String USER_ID = "userId";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String DATA = "data";
    public static final String STATUS = "status";

    /**
     * Status of the user's Organization Management action.
     */
    public enum Status {

        SUCCESS("Success"),
        FAILURE("Failure");

        private String status;

        Status (String status) {

            this.status = status;
        }

        public String getStatus() {

            return status;
        }
    }
}
