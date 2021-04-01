package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.TaskLogDao;
import com.touchspring.ailge.entity.agile.TaskLog;
import com.touchspring.ailge.service.agile.TaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 任务记录表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogDao, TaskLog> implements TaskLogService {

    @Autowired
    private TaskLogDao taskLogDao;

    @Override
    public void saveAttrChange(String userId, String attrName, String taskId, String projectId, String lastValue, String curValue) {
        TaskLog taskLog = new TaskLog();
        taskLog.setCreateUserId(userId);
        taskLog.setTaskId(taskId);
        taskLog.setContent("Changed 自定义属性: " + attrName + "对应的值, From \"" + lastValue + "\", To \"" + curValue + "\"");
        taskLog.setProjectId(projectId);
        taskLogDao.insert(taskLog);
    }

    @Override
    public void saveChildTaskChange(String userId, List<String> childNames, String parentId, String type, String projectId) {
        TaskLog taskLog = new TaskLog();
        taskLog.setCreateUserId(userId);
        taskLog.setTaskId(parentId);
        taskLog.setContent(type + "子任务: " + StringUtils.join(childNames.toArray(), ","));
        taskLog.setProjectId(projectId);
        taskLogDao.insert(taskLog);
    }
}
