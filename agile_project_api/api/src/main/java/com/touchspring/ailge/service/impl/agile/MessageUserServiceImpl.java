package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.enums.MessageModuleEnum;
import com.touchspring.ailge.service.agile.IMailService;
import com.touchspring.ailge.service.agile.MessageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.PlaceholderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息用户关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class MessageUserServiceImpl extends ServiceImpl<MessageUserDao, MessageUser> implements MessageUserService {


    @Autowired
    private MessageUserDao messageUserDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private SysUserDao sysUserDao;
    @Value("${path.prefix}")
    private String pathPrefix;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;

    /**
     * 当前用户的消息
     * @param sysUserId 当前用户ID
     * @param flag true -> 全部信息 false -> 未读信息
     * @return .
     */
    @Override
    public Page<Message> list(String sysUserId, boolean flag, Integer page, Integer size) {

        LambdaQueryChainWrapper<MessageUser> lambdaQueryChainWrapper = new LambdaQueryChainWrapper<MessageUser>(messageUserDao).eq(MessageUser::getSysUserId, sysUserId);
        if (!flag)
            lambdaQueryChainWrapper.eq(MessageUser::getIsRead, BaseEnums.NO.getCode());
        Page<MessageUser> messageUserPage = lambdaQueryChainWrapper.orderByDesc(BaseEntity::getCreateAt).select(MessageUser::getMessageId).page(new Page<>(page, size));

        Page<Message> messagePage = new Page<>();
        messagePage.setTotal(messageUserPage.getTotal());
        List<Message> messageList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messageUserPage.getRecords())){
            List<String> messageIds = messageUserPage.getRecords().stream().map(MessageUser::getMessageId).collect(Collectors.toList());
            messageList = new LambdaQueryChainWrapper<Message>(messageDao).in(BaseEntity::getId, messageIds).orderByDesc(BaseEntity::getCreateAt).list();
        }
        return messagePage.setRecords(messageList);
    }

    /**
     * 保存消息并通知对应的负责人
     * @param type 消息类型
     * @param relationId 关联ID
     * @param content 消息内容
     * @param receiveUserIds 负责人ID
     */
    @Override
    public void saveMessageToUser(String type, String relationId, String content, String[] receiveUserIds, String projectId,
                                  String circleName, String circleRoleName, String circleRoleUserName) {
        //保存消息
        Message message = new Message();
        message.setType(type);
        message.setRelationId(relationId);
        message.setContent(content);
        if ("TASK".equals(type)) {
            message.setProjectId(projectId);
        }
        messageDao.insert(message);

        //通知对应的负责人
        if (ArrayUtils.isEmpty(receiveUserIds)) return;
        List<MessageUser> messageUsers = new ArrayList<>();
        Set<String> filterUserIds = Arrays.stream(receiveUserIds).filter(userId -> StringUtils.isNotBlank(userId)).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(filterUserIds)) return;
        filterUserIds.forEach(userId -> {
            MessageUser messageUser = new MessageUser();
            messageUser.setMessageId(message.getId());
            messageUser.setIsRead(BaseEnums.NO.getCode());
            messageUser.setSysUserId(userId);
            messageUsers.add(messageUser);
        });
        this.saveBatch(messageUsers);

        //发送邮件
        List<SysUser> sysUserList = new LambdaQueryChainWrapper<SysUser>(sysUserDao).in(BaseEntity::getId, filterUserIds).isNotNull(SysUser::getEmail).list();
        if (CollectionUtils.isEmpty(sysUserList)) return;

        //邮件内容
        String url = pathPrefix +
                (MessageModuleEnum.TASK_CHECK.getType().equals(type) ? MessageModuleEnum.PROJECT_WILL_DELAY.getType().toLowerCase() + "?id=" + projectId : type.toLowerCase() + "?id=" + relationId);

        if (MessageModuleEnum.TASK_WILL_DELAY.getType().equals(type)) {
            Task task = taskDao.selectById(relationId);
            if (task == null) return;
            Project project = projectDao.selectById(projectId);
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(task.getMilestoneId());
            SysUser reviewUser = StringUtils.isNotBlank(task.getReviewProjectRoleId()) ? sysUserDao.findByProjectRoleId(task.getReviewProjectRoleId()) : null;
            SysUser confirmUser = StringUtils.isNotBlank(task.getConfirmProjectRoleId()) ? sysUserDao.findByProjectRoleId(task.getConfirmProjectRoleId()) : null;

            sysUserList.forEach(sysUser -> {
                StringBuilder sb = new StringBuilder();
                sb.append("亲爱的").append(sysUser.getRealName()).append("，").append("<br>").append(content);
                mailService.sendMessageMail(sysUser.getEmail(), type, sb.toString(), url, project == null ? "" : project.getName(), projectMilestone == null ? "" : projectMilestone.getName(),
                        task.getName(), reviewUser == null ? "" : reviewUser.getRealName(), confirmUser == null ? "" : confirmUser.getRealName(),
                        task.getEstEndTime() == null ? "" : task.getEstEndTime().toLocalDate().toString(), type, null, null, null);
            });

        } else if (MessageModuleEnum.PROJECT_WILL_DELAY.getType().equals(type)) {
            Project project = projectDao.selectById(relationId);
            if (project == null) return;
            sysUserList.forEach(sysUser -> {
                StringBuilder sb = new StringBuilder();
                sb.append("亲爱的").append(sysUser.getRealName()).append("，").append("<br>").append(content);
                mailService.sendMessageMail(sysUser.getEmail(), type, sb.toString(), url, project.getName(), null,
                        null, null, null,
                        project.getEstEndTime() == null ? "" : project.getEstEndTime().toLocalDate().toString(), type, null, null, null);
            });
        } else {
            sysUserList.forEach(sysUser -> {
                StringBuilder sb = new StringBuilder();
                sb.append("亲爱的").append(sysUser.getRealName()).append("，").append("<br>").append(content);
                mailService.sendMessageMail(sysUser.getEmail(), type, sb.toString(), url, null, null,
                        null, null, null, null, type, circleName, circleRoleName, circleRoleUserName);
            });
        }
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        System.out.println(now.toLocalDate());
        System.out.println(now);
    }

    /**
     * 任务催办
     */
    @Override
    public void taskUrgedToUser(String type, String relationId, String[] receiveUserIds, String projectId, SysUser createUser) {
        Project project = projectDao.selectById(projectId);
        Task task = taskDao.selectById(relationId);
        Map<String,String> messageExt = new HashMap<>();
        messageExt.put("projectName", project == null? null : project.getName());
        messageExt.put("taskName", task == null? null: task.getName());
        messageExt.put("userName", createUser.getRealName());
        this.saveMessageToUser(type, relationId, PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_URGED.getMsg(), messageExt), receiveUserIds, projectId, null, null, null);
    }

}
