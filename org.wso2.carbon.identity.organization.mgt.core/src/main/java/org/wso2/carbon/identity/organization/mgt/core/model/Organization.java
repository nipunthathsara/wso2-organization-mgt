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
    private int tenantId;
    private String name;
    private String displayName;
    private String description;
    private Parent parent = new Parent();
    private OrgStatus status;
    private Metadata metadata = new Metadata();
    private boolean hasAttributes;
    private Map<String, Attribute> attributes = new HashMap<>();
    private Map<String, UserStoreConfig> userStoreConfigs = new HashMap<>();

    /**
     * Allowed organization status.
     */
    public enum OrgStatus {
        ACTIVE,
        DISABLED
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

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

    public OrgStatus getStatus() {
        return status;
    }

    public void setStatus(OrgStatus status) {
        this.status = status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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
