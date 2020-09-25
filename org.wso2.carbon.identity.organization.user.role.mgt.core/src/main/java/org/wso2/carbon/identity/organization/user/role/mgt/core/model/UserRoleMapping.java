package org.wso2.carbon.identity.organization.user.role.mgt.core.model;

import java.util.List;

public class UserRoleMapping {

    private List<String> userIds;
    private String roleId;
    private Integer hybridRoleId;

    public UserRoleMapping(String roleId, List<String> userIds) {
        this.roleId = roleId;
        this.userIds = userIds;
    }

    public void setRoleId(String roleId) {

        this.roleId = roleId;
    }

    public void setUserIds(List<String> userIds) {

        this.userIds = userIds;
    }

    public void setHybridRoleId(Integer hybridRoleId) {

        this.hybridRoleId = hybridRoleId;
    }

    public String getRoleId() {

        return roleId;
    }

    public List<String> getUserIds() {

        return userIds;
    }

    public Integer getHybridRoleId() {

        return hybridRoleId;
    }
}
