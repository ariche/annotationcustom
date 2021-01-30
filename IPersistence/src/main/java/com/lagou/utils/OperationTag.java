package com.lagou.utils;

public enum OperationTag {
    UPDATE("update"),INSERT("insert"),DELETE("delete"),SELECT("select");

    private String code;
    private OperationTag(String oper){
        this.code=oper;
    }

    public String getCode() {
        return code;
    }
}
