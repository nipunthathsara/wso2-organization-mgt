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

/**
 * This class represents the metadata of an organization.
 */
public class Metadata {

    private String lastModified;
    private String created;
    private MetaUser createdBy = new MetaUser();
    private MetaUser lastModifiedBy = new MetaUser();

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

    public MetaUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(MetaUser createdBy) {
        this.createdBy = createdBy;
    }

    public MetaUser getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(MetaUser lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
