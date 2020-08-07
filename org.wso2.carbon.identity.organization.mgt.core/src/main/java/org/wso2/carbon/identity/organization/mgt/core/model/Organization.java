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

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the model of an Organization.
 */
public class Organization {

    private String id;
    private String name;
    private String description;
    private int tenantId;
    private String parentId;
    private boolean active;
    private String lastModified;
    private String created;
    private boolean hasAttributes;
    private Map<String, Attribute> attributes = new HashMap<>();
    private Map<String, UserStoreConfig> userStoreConfigs = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean hasAttributes() {
        return hasAttributes;
    }

    public void setHasAttributes(boolean hasAttributes) {
        this.hasAttributes = hasAttributes;
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, UserStoreConfig> getUserStoreConfigs() {
        return userStoreConfigs;
    }

    public void setUserStoreConfigs(Map<String, UserStoreConfig> userStoreConfigs) {
        this.userStoreConfigs = userStoreConfigs;
    }
}
