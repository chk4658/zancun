package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Message;
import com.touchspring.ailge.entity.agile.MessageUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.sys.SysUser;

import java.util.List;

/**
 * <p>
 * 消息用户关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface MessageUserService extends IService<MessageUser> {

    Page<Message> list(String sysUserId, boolean flag, Integer page, Integer size);

    void saveMessageToUser(String type, String relationId, String content, String[] receiveUserIds, String projectId, String circleName, String circleRoleName, String circleRoleUserName);

    void taskUrgedToUser(String type, String relationId, String[] receiveUserIds, String projectId, SysUser createUser);
}
