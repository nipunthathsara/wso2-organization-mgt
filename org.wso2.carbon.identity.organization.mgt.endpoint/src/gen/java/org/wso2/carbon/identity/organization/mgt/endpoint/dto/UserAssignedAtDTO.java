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


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;

@ApiModel(description = "")
public class UserAssignedAtDTO {

    @Valid 
    private String orgName = null;

    @Valid 
    private String orgId = null;

    /**
    **/
    @ApiModelProperty(value = "")
    @JsonProperty("orgName")
    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
    **/
    @ApiModelProperty(value = "")
    @JsonProperty("orgId")
    public String getOrgId() {
        return orgId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class UserAssignedAtDTO {\n");
        
        sb.append("    orgName: ").append(orgName).append("\n");
        sb.append("    orgId: ").append(orgId).append("\n");
        
        sb.append("}\n");
        return sb.toString();
    }
}
