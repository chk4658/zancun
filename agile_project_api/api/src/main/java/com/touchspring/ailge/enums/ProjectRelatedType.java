package com.touchspring.ailge.enums;

/**
 * 项目相关类型
 */
public class ProjectRelatedType{

    //创建项目的可见范围：项目参与人
    public static final Integer PROJECT_PARTICIPANT = 1;
    //创建项目的可见范围：所有人
    public static final Integer ALL_THE_PEOPLE = 0;
    //创建项目的可见范围：选定部门
    public static final Integer DEPARTMENTAL_VISIBILITY_RANGE = 2;
    //项目负责人角色
    public static final String PROJECT_LEADER = "项目经理";

    public enum ProjectEnums {
        NO(0, "不是"),
        YES(1, "是");

        private Integer code;

        private String message;

        ProjectEnums(Integer code, String message) {
            this.code = code;
            this.message = message;
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
}
