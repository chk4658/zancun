package com.touchspring.ailge.enums;

public enum ResultStatus {
//    SUCCESS(1200, "成功"),
//    ERROR_REQUEST(1400, "错误的请求"),
//    UNAUTHORIZED(1401, "没有授权"),
//    FORBIDDEN(1403, "没有权限访问"),
//    NOT_FOND(1404, "页面不存在"),
//    SERVER_ERROR(1500, "服务器错误"),
//    ERROR_USERNAME(16001, "用户名错误"),
//    ERROR_PASSWORD(16002, "密码错误"),
//    TOKEN_EXPIRED(16003, "TOKEN过期"),
//    USER_NOT_FOUND(16004, "用户不存在"),
//    USER_TELEPHONE_HAS_BINDING(16005, "用户手机已经被绑定"),
//    USER_WECHAT_HAS_BINDING(16005, "用户微信已经被绑定"),
//    VERIFY_CODE_EXPIRED(16006, "验证码过期"),
//    TELEPHONE_NOT_FOUND(16007, "手机号码不存在"),
//    USER_EXISTS(16008, "用户已存在"),
    DELETE_ERROR(2000, "删除失败"),
    ENABLED_ERROR(4000, "禁用失败"),
    AVAILABLE_ERROR(4200, "解除禁用失败"),
    IS_EXIST(1300, "该关系已存在，请重新输入！"),
    TEMPLATE_IS_EXIST(4211, "该项目模板已存在，请重新输入！"),
    TEMPLATE_TYPE_IS_EXIST(4212, "该项目模板类型已存在，请重新输入！"),
    CODE_IS_EXIST(4213, "编码已存在，请重新输入！"),
    DICT_IS_FAIL(4214, "输入为空，请重新输入！"),
    ROLE_IS_EXIST(4215, "该角色已存在，请重新输入！"),
    NOT_EXIST(1600, "不存在相关词条"),
    IS_REPEAT(1400, "前端编码重复，请重新输入！"),
    PROJECT_IS_EXIST(4218, "该项目名称已存在，创建失败！"),
    PROJECT_NAME_IS_EXIST(4219, "该项目名称已存在，更新失败！"),
    PROJECT_NAME_IS_NOT_EMPTY(4220, "该项目名称不能为空！"),
    ROLE_NAME_IS_NOT_EMPTY(4221, "该角色名称不能为空！"),
    PARENT_EST_END_TIME(4222, "父任务截止时间不能小于子任务截止时间！"),
    EST_END_TIME_IS_NULL(4223, "存在截止时间未填写！"),
    ROLE_IS_NULL(4224, "责任角色或确认角色为空！"),
    ROLE_USER_IS_NULL(4225, "责任人或确认人为空！"),
    CHILD_EST_END_TIME(4226, "子任务截止时间不能大于父任务截止时间！"),
    EXIT_CHILD_TASK_NOT_END(4227, "子任务未完成（未开始、待审核、拒绝等），主任务不能通过！"),
    MILESTONE_EST_END_TIME(4228, "里程碑截止时间不能早于任务截止时间！"),
    EXIT_CHILD_TASK_IN_PASS(4229, "子任务带条件通过时，主任务不能完全通过，可以带条件通过或拒绝！"),
    BLANK_PAGES(4230, "空白页不可以上线！"),
    PROJECT_CAN_NOT_MARK(4231, "存在未完成的任务，项目无法归档！"),

    ;

    private Integer code;
    private String message;

    ResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
