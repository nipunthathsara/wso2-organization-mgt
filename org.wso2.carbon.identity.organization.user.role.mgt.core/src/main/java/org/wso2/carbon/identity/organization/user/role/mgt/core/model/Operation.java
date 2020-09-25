package org.wso2.carbon.identity.organization.user.role.mgt.core.model;

public class Operation {

    private String path;
    private String value;
    private String op;

    public Operation(String op, String path, String value) {

        this.op = op;
        this.path = path;
        this.value = value;
    }

    public Operation(String op, String path) {

        this.op = op;
        this.path = path;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
