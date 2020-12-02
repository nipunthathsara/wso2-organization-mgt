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
 * Role inheritance and assigned level details.
 */
public class RoleAssignment {

    private boolean includeSubOrgs;
    private String assignedAt;

    public RoleAssignment() {

    }

    public RoleAssignment(boolean includeSubOrgs, String assignedAt) {

        this.includeSubOrgs = includeSubOrgs;
        this.assignedAt = assignedAt;
    }

    public void setIncludeSubOrgs(boolean includeSubOrgs) {

        this.includeSubOrgs = includeSubOrgs;
    }

    public boolean isIncludeSubOrgs() {

        return includeSubOrgs;
    }

    public void setAssignedAt(String assignedAt) {

        this.assignedAt = assignedAt;
    }

    public String getAssignedAt() {

        return assignedAt;
    }
}
