package org.wso2.carbon.identity.organization.user.role.mgt.core.model;

import java.util.List;

/**
 * User Role Mapping.
 */
public class UserRoleMapping {

    private List<UserRoleMappingUser> users;
    private String roleId;
    private int hybridRoleId;

    public UserRoleMapping(String roleId, List<UserRoleMappingUser> users) {
        this.roleId = roleId;
        this.users = users;
    }

    public void setRoleId(String roleId) {

        this.roleId = roleId;
    }

    public void setUsers(List<UserRoleMappingUser> users) {

        this.users = users;
    }

    public void setHybridRoleId(int hybridRoleId) {

        this.hybridRoleId = hybridRoleId;
    }

    public String getRoleId() {

        return roleId;
    }

    public List<UserRoleMappingUser> getUsers() {

        return users;
    }

    public int getHybridRoleId() {

        return hybridRoleId;
    }
}
