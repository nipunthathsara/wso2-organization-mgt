package org.wso2.carbon.identity.organization.mgt.endpoint.dto;

import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserstoreConfigDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class OrganizationAddDTO  {
  
  
  @NotNull
  private String name = null;
  
  
  private String parentId = null;
  
  
  private List<UserstoreConfigDTO> userstoreConfigs = new ArrayList<UserstoreConfigDTO>();

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("parentId")
  public String getParentId() {
    return parentId;
  }
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("userstoreConfigs")
  public List<UserstoreConfigDTO> getUserstoreConfigs() {
    return userstoreConfigs;
  }
  public void setUserstoreConfigs(List<UserstoreConfigDTO> userstoreConfigs) {
    this.userstoreConfigs = userstoreConfigs;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationAddDTO {\n");
    
    sb.append("  name: ").append(name).append("\n");
    sb.append("  parentId: ").append(parentId).append("\n");
    sb.append("  userstoreConfigs: ").append(userstoreConfigs).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
