package com.touchspring.ailge.dto;

import lombok.Data;

import java.util.List;

@Data
public class TreeResultDTO {

    //模板相关
    public final static String PROJECT_TEMPLATE = "PROJECT_TEMPLATE";

    public final static String PROJECT_TEMPLATE_MILESTONE = "PROJECT_TEMPLATE_MILESTONE";

    public final static String PROJECT_TEMPLATE_TASK = "PROJECT_TEMPLATE_TASK";

    public final static String PROJECT_TEMPLATE_TASK_CHILD = "PROJECT_TEMPLATE_TASK_CHILD";

    //项目相关
    public final static String PROJECT = "PROJECT";

    public final static String PROJECT_MILESTONE = "PROJECT_MILESTONE";

    public final static String PROJECT_TASK = "PROJECT_TASK";

    public final static String PROJECT_TASK_CHILD = "PROJECT_TASK_CHILD";

    private String id;

    private String name;

    private String type;

    private String status;

    private List<TreeResultDTO> children;

    private String forbiddenStatus;
}
