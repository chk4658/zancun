package com.touchspring.ailge.enums;

import lombok.Data;

import java.util.List;

/**
 * 圈子操作类型
 */
public class CircleOperationType{

    //默认
    public static final Integer DEFAULT = 0;
    //所有人
    public static final Integer ALL = 1;
    //部分授权
    public static final Integer PART = 2;
    //圈长角色名称
    public static final String CIRCLE_OWNER = "圈长";


    @Data
    public static class UserOperation {
        /**
         * 用户id
         */
        private String userId;

        /**
         * 权限
         */
        private List<Operation> operations;

        @Data
        public static class Operation {
            /**
             * 圈子id
             */
            private String circleId;
            /**
             * 允许操作圈子
             */
            private Boolean hasOperate;
            /**
             * 允许添加子圈
             */
            private Boolean hasAddCircle;
            /**
             * 允许添加项目
             */
            private Boolean hasAddProject;
            /**
             * 圈子名
             */
            private String circleName;
        }
    }



    public enum CircleOperationTypeEnums {
        OPERATE("OPERATE", "允许操作圈子"),
        ADD_CIRCLE("ADD_CIRCLE", "允许添加子圈"),
        ADD_PROJECT("ADD_PROJECT", "允许添加项目");

        private String code;

        private String message;

        CircleOperationTypeEnums(String code, String message) {
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
}

