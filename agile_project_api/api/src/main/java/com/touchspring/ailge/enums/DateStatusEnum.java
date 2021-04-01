package com.touchspring.ailge.enums;

/**
 * 任务时间状态枚举类
 */
public enum DateStatusEnum {

    FINISH_ON_TIME("1", "已完成，提前或按时完成"),
    NOT_TO_THE_NODE("2", "未完成，未到节点"),
    DELAY_COMPLETION("3", "已完成，延期完成"),
    ADJACENT_NODE("4", "未完成，临近节点（距节点7天）"),
    CONDITIONAL_PASS("5", "带条件通过"),
    DELAY("6", "未完成，延期"),

    ;

    private String code;

    private String message;

    DateStatusEnum(String code, String message) {
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
