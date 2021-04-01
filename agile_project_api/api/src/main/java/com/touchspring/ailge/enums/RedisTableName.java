package com.touchspring.ailge.enums;

/**
 * Redis存储的表的名称
 */
public enum RedisTableName {
    PROJECT("PROJECT:", "项目表"),
    PROJECT_MILESTONE("PROJECT_MILESTONE:", "里程碑表"),
    PARENT_TASK("PARENT_TASK:", "父任务表"),
    CHILD_TASK("CHILD_TASK:", "子任务表"),

    PROJECT_TASK_CHAT_USER("PROJECT_TASK_CHAT_USER:", "项目任务中的聊天记录"),
    ;

    private String code;
    private String message;

    RedisTableName(String code, String message) {
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
