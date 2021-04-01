package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ChatDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dto.ChatDTO;
import com.touchspring.ailge.entity.agile.Chat;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.enums.ChatTypeEnums;
import com.touchspring.ailge.enums.RedisTableName;
import com.touchspring.ailge.service.agile.ChatService;
import com.touchspring.ailge.utils.WebSocketServer;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ChatServiceImpl extends ServiceImpl<ChatDao, Chat> implements ChatService {

    @Autowired
    private ChatDao chatDao;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TaskDao taskDao;

    /**
     * 获取交流
     */
    @Override
    public List<Chat> findAll(Chat chat) {
        return chatDao.findList(chat);
    }

    /**
     * 保存
     */
    @Override
    public boolean save(ChatDTO chatDTO, String userId) {
        //保存
        if(StringUtils.isBlank(chatDTO.getId())) {
           chatDTO.setCreateUserId(userId);
           chatDTO.setUpdateUserId(userId);
           chatDao.insert(chatDTO);
        } else {
           chatDTO.setUpdateUserId(userId);
           chatDao.updateById(chatDTO);
        }

        //任务类型相关通知显示
        if(StringUtils.isNotBlank(chatDTO.getForeignId()) && chatDTO.getType().equals(ChatTypeEnums.TASK.getCode()))
            //存储交流与人关系
            this.saveTaskChatUserToRedis(chatDTO);

       return true;
    }

    /**
     * 获取Redis里  用户-任务交流
     * @param projectId 项目ID
     * @param sysUserId 用户ID
     * @return 任务IDs
     */
    @Override
    public List<String> findHasTaskChatByUser(String projectId, String sysUserId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String taskIds = hashOperations.get(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + projectId, sysUserId);
        if (StringUtils.isBlank(taskIds)) return new ArrayList<>();
        return JSON.parseArray(taskIds, String.class);
    }

    /**
     * 根据TASK_ID 更新Redis里  用户-任务交流 todo 推送到web-socket
     * @param taskId 任务ID
     * @param sysUserId 当前用户ID
     */
    @Override
    public void updateChatRedisByTaskId(String taskId, String sysUserId) {
        Task task = taskDao.selectById(taskId);
        if (task == null || StringUtils.isBlank(task.getProjectId())) return;
        //删除Redis数据
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String taskIds = hashOperations.get(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + task.getProjectId(), sysUserId);
        if (StringUtils.isBlank(taskIds)) return;
        List<String> taskIdList = JSON.parseArray(taskIds, String.class);
        taskIdList.removeIf(id -> id.contains(taskId));
        if (CollectionUtils.isEmpty(taskIdList))
            hashOperations.delete(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + task.getProjectId(), sysUserId);
        else
            hashOperations.put(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + task.getProjectId(), sysUserId, JSON.toJSONString(taskIdList));
    }

    /**
     * 存储交流与人关系
     */
    private void saveTaskChatUserToRedis(ChatDTO chatDTO){
        //存入Redis
        Task task = taskDao.selectById(chatDTO.getForeignId());
        if (task == null || StringUtils.isBlank(task.getProjectId())) return;

        // 提醒对应人员 存储相关联人员及关联表TASK_ID
        Set<String> userIds = new HashSet<>();
        //回复人
        if (StringUtils.isNotBlank(chatDTO.getReplyId())){
            Chat replyChat = chatDao.selectById(chatDTO.getReplyId());
            if (replyChat != null && StringUtils.isNotBlank(replyChat.getCreateUserId()))
                userIds.add(replyChat.getCreateUserId());
        }
        //创建人
        if (StringUtils.isNotBlank(chatDTO.getParentId())) {
            Chat parentChat = chatDao.selectById(chatDTO.getParentId());
            if (parentChat != null && StringUtils.isNotBlank(parentChat.getCreateUserId()))
                userIds.add(parentChat.getCreateUserId());
        }
        //@ 的用户ID
        if (!CollectionUtils.isEmpty(chatDTO.getAtUserIds()))
            userIds.addAll(chatDTO.getAtUserIds());
        if (CollectionUtils.isEmpty(userIds)) return;

        List<String> userList = new ArrayList<>(userIds);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<String> taskIdsList = hashOperations.multiGet(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + task.getProjectId(), userList);

        for (int i = 0; i < taskIdsList.size(); i++) {
            String taskIds = taskIdsList.get(i);
            //空，直接put；不空，去重put
            HashSet<String> taskIdSet = new HashSet<>();
            if (StringUtils.isBlank(taskIds)) {
                taskIdSet.add(chatDTO.getForeignId());
            } else {
                List<String> taskIdList = JSON.parseArray(taskIds, String.class);
                taskIdList.add(chatDTO.getForeignId());
                taskIdSet= new HashSet<>(taskIdList);
            }
            hashOperations.put(RedisTableName.PROJECT_TASK_CHAT_USER.getCode() + task.getProjectId(), userList.get(i), JSON.toJSONString(taskIdSet));
        }

        // 推送到web-socket
        userList.forEach(userId -> {
            try {
                webSocketServer.sendInfo(chatDTO.getForeignId(), userId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
