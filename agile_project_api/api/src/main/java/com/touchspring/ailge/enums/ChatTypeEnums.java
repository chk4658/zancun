package com.touchspring.ailge.enums;

public enum ChatTypeEnums {

    PROJECT("PROJECT", "PROJECT"),
    CIRCLE("CIRCLE", "CIRCLE"),
    TASK("TASK","TASK")
    ;

    private String code;

    private String message;

    ChatTypeEnums(String code, String message) {
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
