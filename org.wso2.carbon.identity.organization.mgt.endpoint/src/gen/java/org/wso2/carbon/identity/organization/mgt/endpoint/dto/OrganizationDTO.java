package org.wso2.carbon.identity.organization.mgt.endpoint.dto;

import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.AttributeDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class OrganizationDTO  {
  
  
  @NotNull
  private String id = null;
  
  @NotNull
  private String name = null;
  
  @NotNull
  private String parentId = null;
  
  @NotNull
  private Boolean status = null;
  
  @NotNull
  private String rdn = null;
  
  @NotNull
  private String dn = null;
  
  @NotNull
  private String lastModified = null;
  
  @NotNull
  private String created = null;
  
  @NotNull
  private List<AttributeDTO> attributes = new ArrayList<AttributeDTO>();
  
  
  private List<String> children = new ArrayList<String>();

  
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
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("parentId")
  public String getParentId() {
    return parentId;
  }
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("status")
  public Boolean getStatus() {
    return status;
  }
  public void setStatus(Boolean status) {
    this.status = status;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("rdn")
  public String getRdn() {
    return rdn;
  }
  public void setRdn(String rdn) {
    this.rdn = rdn;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("dn")
  public String getDn() {
    return dn;
  }
  public void setDn(String dn) {
    this.dn = dn;
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
  @JsonProperty("attributes")
  public List<AttributeDTO> getAttributes() {
    return attributes;
  }
  public void setAttributes(List<AttributeDTO> attributes) {
    this.attributes = attributes;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("children")
  public List<String> getChildren() {
    return children;
  }
  public void setChildren(List<String> children) {
    this.children = children;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationDTO {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  parentId: ").append(parentId).append("\n");
    sb.append("  status: ").append(status).append("\n");
    sb.append("  rdn: ").append(rdn).append("\n");
    sb.append("  dn: ").append(dn).append("\n");
    sb.append("  lastModified: ").append(lastModified).append("\n");
    sb.append("  created: ").append(created).append("\n");
    sb.append("  attributes: ").append(attributes).append("\n");
    sb.append("  children: ").append(children).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
