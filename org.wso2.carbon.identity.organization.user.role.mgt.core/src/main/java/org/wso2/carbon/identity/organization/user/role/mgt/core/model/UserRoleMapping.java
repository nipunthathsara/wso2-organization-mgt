package org.wso2.carbon.identity.organization.user.role.mgt.core.model;

import java.util.List;

public class UserRoleMapping {

    private List<String> userIds;
    private Integer roleId;

    public UserRoleMapping(Integer roleId, List<String> userIds) {
        this.roleId = roleId;
        this.userIds = userIds;
    }

    public void setRoleId(Integer roleId) {

        this.roleId = roleId;
    }

    public void setUserIds(List<String> userIds) {

        this.userIds = userIds;
    }

    public Integer getRoleId() {

        return roleId;
    }

    public List<String> getUserIds() {

        return userIds;
    }
}
