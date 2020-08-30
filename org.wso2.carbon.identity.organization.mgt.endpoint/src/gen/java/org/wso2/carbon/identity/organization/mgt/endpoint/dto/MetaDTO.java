package org.wso2.carbon.identity.organization.mgt.endpoint.dto;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.MetaUserDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class MetaDTO  {
  
  
  @NotNull
  private String created = null;
  
  @NotNull
  private String lastModified = null;
  
  @NotNull
  private MetaUserDTO createdBy = null;
  
  @NotNull
  private MetaUserDTO lastModifiedBy = null;

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("created")
  public String getCreated() {
    return created;
  }
  public void setCreated(String created) {
    this.created = created;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("lastModified")
  public String getLastModified() {
    return lastModified;
  }
  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("createdBy")
  public MetaUserDTO getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(MetaUserDTO createdBy) {
    this.createdBy = createdBy;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("lastModifiedBy")
  public MetaUserDTO getLastModifiedBy() {
    return lastModifiedBy;
  }
  public void setLastModifiedBy(MetaUserDTO lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  

  @Override
  public String toString()  {
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
