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

package org.wso2.carbon.identity.organization.mgt.core.model;

import java.util.List;

/**
 * Organization-User-Role mapping for role assign and revoke events.
 */
public class OrganizationUserRoleMappingForEvent {

    private String organizationId;
    private String roleId;
    private String userId;
    private List<UserRoleInheritance> usersRoleInheritance;
    private boolean includeSubOrgs;

    public OrganizationUserRoleMappingForEvent() {

    }

    public OrganizationUserRoleMappingForEvent(String organizationId, String roleId, String userId,
                                               boolean includeSubOrgs) {

        this.organizationId = organizationId;
        this.userId = userId;
        this.roleId = roleId;
        this.includeSubOrgs = includeSubOrgs;
    }

    public OrganizationUserRoleMappingForEvent(String organizationId, String roleId,
                                               List<UserRoleInheritance> usersRoleInheritance) {

        this.organizationId = organizationId;
        this.roleId = roleId;
        this.usersRoleInheritance = usersRoleInheritance;
    }

    public void setOrganizationId(String organizationId) {

        this.organizationId = organizationId;
    }

    public void setRoleId(String roleId) {

        this.roleId = roleId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public void setUsersRoleInheritance(
            List<UserRoleInheritance> usersRoleInheritance) {

        this.usersRoleInheritance = usersRoleInheritance;
    }

    public void setIncludeSubOrgs(boolean includeSubOrgs) {

        this.includeSubOrgs = includeSubOrgs;
    }

    public String getOrganizationId() {

        return organizationId;
    }

    public String getUserId() {

        return userId;
    }

    public String getRoleId() {

        return roleId;
    }

    public List<UserRoleInheritance> getUsersRoleInheritance() {

        return usersRoleInheritance;
    }

    public boolean isIncludeSubOrgs() {

        return includeSubOrgs;
    }
}
