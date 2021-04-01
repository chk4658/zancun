package com.touchspring.ailge.enums;

/**
 * 消息模块
 */
public enum MessageModuleEnum {
    //任务相关
    TASK_WILL_DELAY("TASK", "您管理的项目：${projectName}中的${taskName}任务。还有${day}天到期,请及时修改项目状态！"),
    TASK_ALLOCATION("TASK", "${userName}给${toUserName}分配了任务。"),
    TASK_TIME_CHANGE("TASK", "${userName}更新了任务截止时间，请及时查看！"),
    TASK_CHECK("TASK", "${userName}将任务状态修改成：待审核，请及时审核！"),
    //    TASK_REFUSED("TASK", "${userName}将任务状态修改成：已拒绝，请及时检查！"),
    TASK_REFUSED("TASK", "${userName}提交审核的任务被拒绝，原因：${refuseReason}，请及时检查！"),
    TASK_RESPONSIBLE_PERSON_CHANGE("TASK", "${userName}将您修改为任务${roleType}，请及时处理！"),
    //项目相关
    PROJECT_WILL_DELAY("PROJECT", "您管理的项目即将超期，请及时检查项目状态！"),
    //圈子相关
    CIRCLE_ROLE_CHANGE("CIRCLE", "${userName}变更了圈子：${circleName}的${roleName}角色，请及时查看！"),
    //任务催办
    TASK_URGED("TASK", "您的\"${projectName}\"项目-\"${taskName}\"任务还未完成，${userName}催促您请及时完成！"),
    ;

    MessageModuleEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    private String type;
    private String msg;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
