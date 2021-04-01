package com.touchspring.ailge.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by liu shicen on 2018/10/9.
 * 是否禁用
 */
public enum IsEnabled {
    DISABLE(1L, "禁用"),
    NOT_DISABLED(0L, "未禁用"),
    ;


    private Long code;
    private String message;

    IsEnabled(Long code, String message) {
        this.code = code;
        this.message = message;
    }


    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();
        for (IsEnabled e : IsEnabled.values()) {
            JSONObject object = new JSONObject();
            object.put("value", e.getCode());
            object.put("label", e.getMessage());
            jsonArray.add(object);
        }
        return jsonArray;
    }
}
