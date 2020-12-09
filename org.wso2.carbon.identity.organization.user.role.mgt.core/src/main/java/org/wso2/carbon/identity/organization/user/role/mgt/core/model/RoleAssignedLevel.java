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

package org.wso2.carbon.identity.organization.user.role.mgt.core.model;

/**
 * Role assigned level details;
 */
public class RoleAssignedLevel {

    private String orgId;
    private String orgName;

    public RoleAssignedLevel() {

    }

    public RoleAssignedLevel(String orgId, String orgName) {

        this.orgId = orgId;
        this.orgName = orgName;
    }

    public void setOrgId(String orgId) {

        this.orgId = orgId;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }

    public String getOrgId() {

        return orgId;
    }

    public String getOrgName() {

        return orgName;
    }
}
