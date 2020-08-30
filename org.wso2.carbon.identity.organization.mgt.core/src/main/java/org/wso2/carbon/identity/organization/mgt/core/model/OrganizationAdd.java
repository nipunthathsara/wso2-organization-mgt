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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the organization data retrieved in the 'organization create' request.
 */
public class OrganizationAdd {

    private String name;
    private String displayName;
    private String description;
    // Using 'Parent' instead of 'String parentId' here for performance improvement down the lane
    private Parent parent = new Parent();
    private List<Attribute> attributes = new ArrayList<>();
    private List<UserStoreConfig> userStoreConfigs = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<UserStoreConfig> getUserStoreConfigs() {
        return userStoreConfigs;
    }

    public void setUserStoreConfigs(List<UserStoreConfig> userStoreConfigs) {
        this.userStoreConfigs = userStoreConfigs;
    }
}
