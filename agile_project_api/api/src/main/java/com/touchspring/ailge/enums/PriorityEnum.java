package com.touchspring.ailge.enums;

/**
 * 任务进展状态枚举类
 */
public enum PriorityEnum {

    LOW("4", "低"),
    MIDDLE("3", "中"),
    HIGH("2", "高"),
    EMERGENCY("1", "紧急"),
    ;

    private String code;

    private String message;

    PriorityEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     *
     * @param code
     * @return
     */
    public static String getMessage(String code) {
        for (PriorityEnum taskStatusEnum : PriorityEnum.values()) {
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
