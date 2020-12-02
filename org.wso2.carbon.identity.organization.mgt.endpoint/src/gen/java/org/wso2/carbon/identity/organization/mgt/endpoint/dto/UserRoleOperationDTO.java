/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.organization.mgt.endpoint.dto;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel(description = "")
public class UserRoleOperationDTO {

    @Valid 
    @NotNull(message = "Property op cannot be null.") 
    private String op = null;

    @Valid 
    @NotNull(message = "Property path cannot be null.") 
    private String path = null;

    @Valid 
    @NotNull(message = "Property value cannot be null.") 
    private Boolean value = null;

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("op")
    public String getOp() {
        return op;
    }
    public void setOp(String op) {
        this.op = op;
    }

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("path")
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("value")
    public Boolean getValue() {
        return value;
    }
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class UserRoleOperationDTO {\n");
        
        sb.append("    op: ").append(op).append("\n");
        sb.append("    path: ").append(path).append("\n");
        sb.append("    value: ").append(value).append("\n");
        
        sb.append("}\n");
        return sb.toString();
    }
}
