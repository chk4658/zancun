package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.MessageUserDao;
import com.touchspring.ailge.entity.agile.Message;
import com.touchspring.ailge.entity.agile.MessageUser;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.service.agile.MessageUserService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息用户关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/message-user")
public class MessageUserController {

    private final MessageUserService messageUserService;
    private final MessageUserDao messageUserDao;

    public MessageUserController(MessageUserService messageUserService, MessageUserDao messageUserDao) {
        this.messageUserService = messageUserService;
        this.messageUserDao = messageUserDao;
    }

    /**
     * 当前用户的所有消息
     * @param flag true -> 全部信息 false -> 未读信息
     * @return .
     */
    @GetMapping("list")
    public ResultData list(@RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size,
                           @RequestHeader(SystemConfig.TOKEN) String token,
                           boolean flag) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        Page<Message> messagePage = messageUserService.list(user.getId(), flag, page, size);

//        Page<Message> unreadMessagePage = messageUserService.list(user.getId(), false, page, size);
        Integer unreadMessageCount = new LambdaQueryChainWrapper<MessageUser>(messageUserDao).eq(MessageUser::getSysUserId, user.getId()).eq(MessageUser::getIsRead, BaseEnums.NO.getCode()).count();

        return ResultData.ok().putDataValue("messageList", messagePage.getRecords()).putDataValue("totalAmount", messagePage.getTotal())
                .putDataValue("unreadTotalAmount", unreadMessageCount);
    }

    /**
     * 单个消息标记为已读
     */
    @GetMapping("mark-as-read")
    public ResultData markAsRead(String messageId, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        MessageUser messageUser = new LambdaQueryChainWrapper<MessageUser>(messageUserDao).eq(MessageUser::getMessageId, messageId).eq(MessageUser::getSysUserId, user.getId()).one();
        messageUser.setIsRead(BaseEnums.YES.getCode());
        messageUserDao.updateById(messageUser);
        return ResultData.ok();
    }

    /**
     * 全部标记为已读
     */
    @GetMapping("markAllAsRead")
    public ResultData markAllAsRead(@RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<MessageUser> messageUsers = new LambdaQueryChainWrapper<MessageUser>(messageUserDao).eq(MessageUser::getSysUserId, user.getId()).eq(MessageUser::getIsRead, BaseEnums.NO.getCode()).list();
        messageUsers = messageUsers.stream().peek(messageUser -> messageUser.setIsRead(BaseEnums.YES.getCode())).collect(Collectors.toList());
        messageUserService.updateBatchById(messageUsers);
        return ResultData.ok();
    }

    /**
     * 任务催办
     * @param type 消息类型
     * @param relationId 关联ID
     * @param content 消息内容
     * @param receiveUserIds 相关人员ID
     * @param projectId 项目ID（type为TASK，必传）
     * @return .
     */
    @GetMapping("taskManager")
    public ResultData taskManager(String type, String relationId, String content, String[] receiveUserIds, String projectId, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        messageUserService.taskUrgedToUser(type, relationId, receiveUserIds, projectId, user);
        return ResultData.ok();
    }
}
