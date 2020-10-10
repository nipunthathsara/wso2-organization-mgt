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
public class RoleDTO {

    @Valid 
    @NotNull(message = "Property roleId cannot be null.") 
    private String roleId = null;

    @Valid 
    @NotNull(message = "Property roleName cannot be null.") 
    private String roleName = null;

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("roleId")
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("roleName")
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class RoleDTO {\n");
        
        sb.append("    roleId: ").append(roleId).append("\n");
        sb.append("    roleName: ").append(roleName).append("\n");
        
        sb.append("}\n");
        return sb.toString();
    }
}
