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

package org.wso2.carbon.identity.organization.mgt.core.model;

public class OrganizationMgtRole {

    private String roleName;
    private String groupId;
    private int hybridRoleId;

    public OrganizationMgtRole(String roleName, String groupId, int hybridRoleId) {

        this.roleName = roleName;
        this.groupId = groupId;
        this.hybridRoleId = hybridRoleId;
    }

    public String getRoleName() {

        return roleName;
    }

    public void setRoleName(String roleName) {

        this.roleName = roleName;
    }

    public String getGroupId() {

        return groupId;
    }

    public void setGroupId(String groupId) {

        this.groupId = groupId;
    }

    public int getHybridRoleId() {

        return hybridRoleId;
    }

    public void setHybridRoleId(int hybridRoleId) {

        this.hybridRoleId = hybridRoleId;
    }
}
