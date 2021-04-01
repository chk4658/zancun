package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.TaskTemplateDataDao;
import com.touchspring.ailge.entity.agile.TaskTemplateData;
import com.touchspring.ailge.service.agile.TaskTemplateDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 任务数据表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Service
public class TaskTemplateDataServiceImpl extends ServiceImpl<TaskTemplateDataDao, TaskTemplateData> implements TaskTemplateDataService {

    @Autowired
    private TaskTemplateDataDao taskTemplateDataDao;

    @Override
    public boolean save(TaskTemplateData taskTemplateData) {
        TaskTemplateData lastTaskData = new LambdaQueryChainWrapper<TaskTemplateData>(taskTemplateDataDao)
                .eq(TaskTemplateData::getProjectTemplateTaskId, taskTemplateData.getProjectTemplateTaskId())
                .eq(TaskTemplateData::getProjectTemplateAttrId, taskTemplateData.getProjectTemplateAttrId()).one();
        if (lastTaskData == null) {
            lastTaskData = new TaskTemplateData();
            lastTaskData.setProjectTemplateTaskId(taskTemplateData.getProjectTemplateTaskId());
            lastTaskData.setProjectTemplateAttrId(taskTemplateData.getProjectTemplateAttrId());
            lastTaskData.setValue(taskTemplateData.getValue());
            taskTemplateDataDao.insert(lastTaskData);
        } else if (!StringUtils.equals(lastTaskData.getValue(), taskTemplateData.getValue())) {
            lastTaskData.setValue(taskTemplateData.getValue());
            taskTemplateDataDao.updateById(lastTaskData);
        }
        taskTemplateData.setId(lastTaskData.getId());
        return true;
    }

    /**
     * 保存List
     * @param templateDataList
     * @return
     */
    @Override
    public boolean saveList(List<TaskTemplateData> templateDataList) {
        if (CollectionUtils.isEmpty(templateDataList)) return true;
        templateDataList.forEach(taskTemplateData -> this.save(taskTemplateData));
        return true;
    }
}
