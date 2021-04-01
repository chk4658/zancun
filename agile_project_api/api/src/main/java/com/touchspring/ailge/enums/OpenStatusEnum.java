package com.touchspring.ailge.enums;

/**
 * 开阀状态
 */
public enum OpenStatusEnum {
    normal(1, "正常"),
    abnormal(2, "异常"),
    ;

    private Integer code;

    private String message;

    OpenStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     * @param code
     * @return
     */
    public static String getMessage(Integer code) {
        for (OpenStatusEnum openStatusEnum : OpenStatusEnum.values()) {
            if (openStatusEnum.getCode().equals(code)) return openStatusEnum.getMessage();
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
