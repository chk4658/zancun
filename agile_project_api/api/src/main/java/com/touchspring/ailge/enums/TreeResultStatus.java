package com.touchspring.ailge.enums;

public enum TreeResultStatus {

    /**
     * 是否启用
     */
    NOT_ENABLE("0", "NOT_ENABLE"),
    ENABLE("1", "ENABLE"),

    /**
     * 是否禁用
     */
    NOT_FORBIDDEN("0", "NOT_FORBIDDEN"),
    FORBIDDEN("1", "FORBIDDEN"),


    /**
     * 是否展开
     */
    NOT_COLLAPSED("0", "NOT_COLLAPSED"),
    COLLAPSED("1", "COLLAPSED"),

    ;

    private String code;

    private String message;

    TreeResultStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
