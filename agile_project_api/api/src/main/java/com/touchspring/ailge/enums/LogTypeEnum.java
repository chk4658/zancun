package com.touchspring.ailge.enums;

/**
 * 日志类型
 */
public enum LogTypeEnum {
    DELETE("DELETE", "删除"),
    INSERT("INSERT", "添加"),
    UPDATE("UPDATE", "更新"),
    ;

    private String code;
    private String msg;

    LogTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
