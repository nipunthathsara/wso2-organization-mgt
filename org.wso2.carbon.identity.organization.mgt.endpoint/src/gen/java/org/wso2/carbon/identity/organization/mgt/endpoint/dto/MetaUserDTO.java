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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(description = "")
public class MetaUserDTO {

    @NotNull
    private String id = null;

    @NotNull
    private String ref = null;

    private String username = null;

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true,
                      value = "")
    @JsonProperty("ref")
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("username")
    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MetaUserDTO {\n");

        sb.append("  id: ").append(id).append("\n");
        sb.append("  ref: ").append(ref).append("\n");
        sb.append("  username: ").append(username).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
