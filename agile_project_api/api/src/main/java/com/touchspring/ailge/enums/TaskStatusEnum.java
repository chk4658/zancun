package com.touchspring.ailge.enums;

/**
 * 任务进展状态枚举类
 */
public enum TaskStatusEnum {

    NOT_STARTED("1", "未开始"),
    IN_PROGRESS("2", "进行中"),
    CHECK_PENDING("3", "待审核"),
    REFUSED("4", "已拒绝"),
    CONDITIONAL_PASS("5", "带条件通过"),
    COMPLETED("6", "已完成"),
    ;

    private String code;

    private String message;

    TaskStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     * @param code
     * @return
     */
    public static String getMessage(String code) {
        for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
            if (taskStatusEnum.getCode().equals(code)) return taskStatusEnum.getMessage();
        }
        return "";
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
