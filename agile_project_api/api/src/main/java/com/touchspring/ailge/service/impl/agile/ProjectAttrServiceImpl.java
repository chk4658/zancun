package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.TaskDataDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.dao.agile.ProjectAttrDao;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskData;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.service.agile.ProjectAttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.service.agile.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目任务属性字段表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectAttrServiceImpl extends ServiceImpl<ProjectAttrDao, ProjectAttr> implements ProjectAttrService {

    @Autowired
    private ProjectAttrDao projectAttrDao;
    @Autowired
    private TaskDataDao taskDataDao;
    @Autowired
    private TaskService taskService;

    /**
     * 删除
     * @param id 属性字段ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
//        taskDataDao.delete(new LambdaQueryWrapper<TaskData>().eq(TaskData::getProjectAttrId, id));
        List<TaskData> taskDataList = new LambdaQueryChainWrapper<TaskData>(taskDataDao).eq(TaskData::getProjectAttrId, id).list();
        if (!CollectionUtils.isEmpty(taskDataList)){
            List<String> taskDataIds = taskDataList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            taskDataDao.deleteBatchIds(taskDataIds);
            Set<String> taskIds = taskDataList.stream().collect(Collectors.groupingBy(TaskData::getTaskId)).keySet();
            // 更新Redis中的任务  ---> 只更新启用
            List<Task> enableTasks = taskService.listByIds(taskIds).stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))).collect(Collectors.toList());
            taskService.batchUpdateTaskInRedis(enableTasks);
//            taskIds.forEach(taskId -> taskService.updateTaskInRedisByTaskId(taskId));
        }

        projectAttrDao.deleteById(id);
        return true;
    }

    @Override
    public List<ProjectAttr> findByProjectId(String projectId) {
        return new LambdaQueryChainWrapper<>(projectAttrDao).eq(ProjectAttr::getProjectId, projectId).orderByAsc(BaseEntity::getCreateAt).list();
    }
}
