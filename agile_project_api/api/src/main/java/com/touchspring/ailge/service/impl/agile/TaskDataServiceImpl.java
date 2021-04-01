package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectAttrDao;
import com.touchspring.ailge.dao.agile.TaskDataDao;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskData;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.service.agile.TaskDataService;
import com.touchspring.ailge.service.agile.TaskLogService;
import com.touchspring.ailge.service.agile.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务数据表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Service
public class TaskDataServiceImpl extends ServiceImpl<TaskDataDao, TaskData> implements TaskDataService {

    @Autowired
    private TaskDataDao taskDataDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private ProjectAttrDao projectAttrDao;

    @Override
    public boolean save(TaskData taskData, String userId) {
        TaskData lastTaskData = new LambdaQueryChainWrapper<TaskData>(taskDataDao).eq(TaskData::getTaskId, taskData.getTaskId())
                .eq(TaskData::getProjectAttrId, taskData.getProjectAttrId()).one();
        if (lastTaskData == null) {
            lastTaskData = new TaskData();
            lastTaskData.setTaskId(taskData.getTaskId());
            lastTaskData.setProjectAttrId(taskData.getProjectAttrId());
            lastTaskData.setValue(taskData.getValue());
            taskDataDao.insert(lastTaskData);
        } else if (!StringUtils.equals(lastTaskData.getValue(), taskData.getValue())) {
            //Log 自定义属性值变更
            ProjectAttr projectAttr = projectAttrDao.selectById(taskData.getProjectAttrId());
            if (projectAttr != null)
                taskLogService.saveAttrChange(userId, projectAttr.getName(), taskData.getTaskId(), projectAttr.getProjectId(), lastTaskData.getValue(), taskData.getValue());

            lastTaskData.setValue(taskData.getValue());
            taskDataDao.updateById(lastTaskData);
        }
        // 更新Redis中的任务  ---> 只更新启用
        taskService.updateTaskInRedisByTaskId(taskData.getTaskId());
        return true;
    }

    @Override
    public boolean delete(String id) {
        TaskData taskData = taskDataDao.selectById(id);
        taskDataDao.deleteById(id);
        // 更新Redis中的任务
        taskService.updateTaskInRedisByTaskId(taskData.getTaskId());
        return true;
    }
}
