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

package org.wso2.carbon.identity.organization.mgt.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(description = "")
public class MetaDTO {

    @NotNull
    private String created = null;

    @NotNull
    private String lastModified = null;

    @NotNull
    private MetaUserDTO createdBy = null;

    @NotNull
    private MetaUserDTO lastModifiedBy = null;

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("lastModified")
    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("createdBy")
    public MetaUserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(MetaUserDTO createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("lastModifiedBy")
    public MetaUserDTO getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(MetaUserDTO lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MetaDTO {\n");

        sb.append("  created: ").append(created).append("\n");
        sb.append("  lastModified: ").append(lastModified).append("\n");
        sb.append("  createdBy: ").append(createdBy).append("\n");
        sb.append("  lastModifiedBy: ").append(lastModifiedBy).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
