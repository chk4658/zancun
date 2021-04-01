package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.ChatDTO;
import com.touchspring.ailge.entity.agile.Chat;
import com.touchspring.ailge.entity.agile.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ChatService extends IService<Chat> {

    List<Chat> findAll(Chat chat);

    boolean save(ChatDTO chatDTO, String userId);

    List<String> findHasTaskChatByUser(String projectId, String sysUserId);

    void updateChatRedisByTaskId(String taskId, String sysUserId);
}
