package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dto.ChatDTO;
import com.touchspring.ailge.entity.agile.Chat;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.service.agile.ChatService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("//chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 查询所有
     */
    @GetMapping("all")
    public ResultData findAll(Chat chat){
        List<Chat> chats = chatService.findAll(chat);
        return ResultData.ok().putDataValue("chatList", chats);
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ChatDTO chatDTO, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        chatService.save(chatDTO, user.getId());
        return ResultData.ok();
    }

    /**
     * 根据TASK_ID 更新Redis里  用户-任务交流
     */

    @GetMapping("updateChatRedisByTaskId")
    public ResultData updateChatRedisByTaskId(String taskId, @RequestHeader(SystemConfig.TOKEN) String token){
        if (StringUtils.isBlank(taskId)) return ResultData.ok();
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        chatService.updateChatRedisByTaskId(taskId, user.getId());
        return ResultData.ok();
    }

    /**
     * 获取Redis里  用户-任务交流
     */
    @GetMapping("findHasTaskChatByUser")
    public ResultData findHasTaskChatByUser(String projectId, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<String> hasChatTaskIds = chatService.findHasTaskChatByUser(projectId, user.getId());
        return ResultData.ok().putDataValue("hasChatTaskIds", hasChatTaskIds);
    }


}
