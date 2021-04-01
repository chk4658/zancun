package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ChatUser;

import java.util.Set;

public interface ChatUserService extends IService<ChatUser> {

    void saveChatToUser(String chatId, Set<String> userIds);

}
