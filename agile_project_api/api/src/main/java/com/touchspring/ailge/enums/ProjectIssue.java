package com.touchspring.ailge.enums;

import lombok.Data;

public class ProjectIssue {


    public enum Repeat {

        YES(0,"重复发生"),
        NO(1,"非重复发生");

        private int code;

        private String message;

        Repeat(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public enum Close {

        YES(0,"关闭"),
        NO(1,"不关闭");

        private int code;

        private String message;

        Close(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public enum Status {

        // 0/4表示无措施
        NO_MEASURE(0,"0/4"),
        //1/4表示有短期措施
        SHORT_MEASURE(1,"1/4"),
        //2/4表示有长期措施
        LONG_MEASURE(2,"2/4"),
        //3/4表示已实施待跟踪
        TRACKING(3,"3/4"),
        //4/4表示已完成
        COMPLETE(4,"4/4");

        private int code;

        private String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public enum Ll {

        YES(0,"输入LL"),
        NO(1,"不输入LL");

        private int code;

        private String message;

        Ll(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public enum Priority {

        H("H","高"),
        M("M","一般");

        private String code;

        private String message;

        Priority(String code, String message) {
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

    // 当前项目阶段
    public enum Stage{
        // 创建
        CREATE(0,"创建表单"),
        //项目经理
        MANAGER(1,"选择分析人"),
        //分析
        ANALYSIS(2,"分析问题原因"),
        //短期措施
        SHORT_MEASURE(3,"短期措施"),
        //长期措施
        LONG_MEASURE(4,"长期措施"),
        // 否关闭状态
        CLOSE(5,"关闭表单"),
        ;

        private int code;

        private String message;

        Stage(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public static class StageC{

        public StageC() {
        }

        public StageC(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;

        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class StatusC{

        public StatusC() {
        }

        public StatusC(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;

        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
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
