package org.wso2.carbon.identity.organization.mgt.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class OperationDTO  {
  
  
  @NotNull
  private String op = null;
  
  @NotNull
  private String path = null;
  
  @NotNull
  private String value = null;

  
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
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OperationDTO {\n");
    
    sb.append("  op: ").append(op).append("\n");
    sb.append("  path: ").append(path).append("\n");
    sb.append("  value: ").append(value).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
