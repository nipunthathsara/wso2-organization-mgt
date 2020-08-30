package org.wso2.carbon.identity.organization.mgt.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class UserStoreConfigDTO  {
  
  
  public enum KeyEnum {
     USER_STORE_DOMAIN,  RDN,  DN, 
  };
  @NotNull
  private KeyEnum key = null;
  
  @NotNull
  private String value = null;

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("key")
  public KeyEnum getKey() {
    return key;
  }
  public void setKey(KeyEnum key) {
    this.key = key;
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
    sb.append("class UserStoreConfigDTO {\n");
    
    sb.append("  key: ").append(key).append("\n");
    sb.append("  value: ").append(value).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
