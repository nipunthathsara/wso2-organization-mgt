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

import java.util.Map;

/**
 * User representation.
 */
public class RoleMember {

    private Map<String, Object> userAttributes;
    private boolean includeSubOrg;
    private String assignedOrgLevelId;

    public RoleMember(Map<String, Object> attributes) {

        this.userAttributes = attributes;
    }

    public RoleMember(Map<String, Object> attributes, boolean includeSubOrg, String assignedOrgLevelId) {

        this.userAttributes = attributes;
        this.includeSubOrg = includeSubOrg;
        this.assignedOrgLevelId = assignedOrgLevelId;
    }

    public Map<String, Object> getUserAttributes() {

        return userAttributes;
    }

    public boolean isIncludeSubOrg() {

        return includeSubOrg;
    }

    public String getAssignedOrgLevelId() {

        return assignedOrgLevelId;
    }

    public void setUserAttributes(Map<String, Object> userAttributes) {

        this.userAttributes = userAttributes;
    }

    public void setAssignedOrgLevelId(String assignedOrgLevelId) {

        this.assignedOrgLevelId = assignedOrgLevelId;
    }

    public void setIncludeSubOrg(boolean includeSubOrg) {

        this.includeSubOrg = includeSubOrg;
    }
}
