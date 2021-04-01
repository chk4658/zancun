package com.touchspring.ailge.enums;

/**
 * 任务类型
 */
public enum TaskTypeEnum {
    COMMON_TASKS(1, "普通任务"),
    APQP_TASK(2, "APQP任务"),
    OPEN_TASK(3, "质量阀任务"),
    ;

    private Integer code;

    private String message;

    TaskTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     * @param code
     * @return
     */
    public static String getMessage(Integer code) {
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()) {
            if (taskTypeEnum.getCode().equals(code)) return taskTypeEnum.getMessage();
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
