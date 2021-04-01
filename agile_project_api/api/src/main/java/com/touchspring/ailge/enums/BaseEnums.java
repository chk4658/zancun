package com.touchspring.ailge.enums;

/**
 * @author： mengyue
 * @version: 1.0
 * @date： 2019/11/26$ 10:27$
 * @description：
 */
public enum BaseEnums {
    NO(0, "不是"),
    YES(1, "是");

    private Integer code;

    private String message;

    BaseEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     * @param code
     * @return
     */
    public static String getMessage(Integer code) {
        for (BaseEnums baseEnums : BaseEnums.values()) {
            if (baseEnums.getCode().equals(code)) return baseEnums.getMessage();
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
