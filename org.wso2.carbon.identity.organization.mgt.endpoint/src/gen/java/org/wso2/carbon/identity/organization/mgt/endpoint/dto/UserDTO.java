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

import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserEmailsDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserNameDTO;
import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel(description = "")
public class UserDTO {

    @Valid 
    @NotNull(message = "Property username cannot be null.") 
    private String username = null;

    @Valid 
    @NotNull(message = "Property id cannot be null.") 
    private String id = null;

    @Valid 
    private UserNameDTO name = null;

    @Valid 
    private List<UserEmailsDTO> emails = new ArrayList<UserEmailsDTO>();

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /**
    **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
    **/
    @ApiModelProperty(value = "")
    @JsonProperty("name")
    public UserNameDTO getName() {
        return name;
    }
    public void setName(UserNameDTO name) {
        this.name = name;
    }

    /**
    **/
    @ApiModelProperty(value = "")
    @JsonProperty("emails")
    public List<UserEmailsDTO> getEmails() {
        return emails;
    }
    public void setEmails(List<UserEmailsDTO> emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class UserDTO {\n");
        
        sb.append("    username: ").append(username).append("\n");
        sb.append("    id: ").append(id).append("\n");
        sb.append("    name: ").append(name).append("\n");
        sb.append("    emails: ").append(emails).append("\n");
        
        sb.append("}\n");
        return sb.toString();
    }
}
