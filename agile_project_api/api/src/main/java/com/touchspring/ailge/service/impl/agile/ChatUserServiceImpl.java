package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ChatUserDao;
import com.touchspring.ailge.entity.agile.ChatUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.service.agile.ChatUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ChatUserServiceImpl extends ServiceImpl<ChatUserDao, ChatUser> implements ChatUserService {

    /**
     * 交流通知对应的人员
     * @param chatId 交流ID
     * @param userIds 人员ID
     */
    @Override
    public void saveChatToUser(String chatId, Set<String> userIds) {
        List<ChatUser> chatUserList = new ArrayList<>();
        userIds.forEach(userId -> {
            ChatUser chatUser = new ChatUser();
            chatUser.setChatId(chatId);
            chatUser.setIsRead(BaseEnums.NO.getCode());
            chatUser.setSysUserId(userId);
            chatUserList.add(chatUser);
        });
        this.saveBatch(chatUserList);
    }
}
