package com.touchspring.ailge.dto;

import lombok.Data;

import java.util.List;

/**
 * 启用或禁用
 */
@Data
public class EnableOrForbiddenDTO {
    public final static String PROJECT_MILESTONE = "PROJECT_MILESTONE";

    public final static String TASK = "TASK";

    private Integer status;

    private List<EnableOrForbiddens> enableOrForbiddens;

    @Data
    public static class  EnableOrForbiddens {

        private String id;

        private String type;
    }




}
