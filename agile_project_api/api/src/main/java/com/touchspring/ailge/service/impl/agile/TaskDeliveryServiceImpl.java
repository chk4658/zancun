package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.Delivery;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskDelivery;
import com.touchspring.ailge.entity.agile.TaskLog;
import com.touchspring.ailge.enums.RedisTableName;
import com.touchspring.ailge.service.agile.TaskDeliveryService;
import com.touchspring.ailge.service.agile.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务交付物关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Service
public class TaskDeliveryServiceImpl extends ServiceImpl<TaskDeliveryDao, TaskDelivery> implements TaskDeliveryService {

    @Autowired
    private TaskDeliveryDao taskDeliveryDao;
    @Autowired
    private DeliveryDao deliveryDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogDao taskLogDao;


    /**
     * 保存任务与交付物关系
     * @param taskId 任务ID
     * @param taskDelivery 交付物
     * @return 保存成功
     */
    @Override
    public boolean save(String taskId, TaskDelivery taskDelivery, String sysUserId, String path, String reUploadId) {
        TaskDelivery target;
        if (StringUtils.isBlank(taskDelivery.getId())){
            target = taskDelivery;
            if (StringUtils.isBlank(target.getDeliveryId())){
                Delivery delivery = new Delivery();
                delivery.setName(target.getDeliveryName());
                if (StringUtils.isNotBlank(path)) delivery.setPath(path);
                deliveryDao.insert(delivery);
                target.setDeliveryId(delivery.getId());
            }
            target.setTaskId(taskId);
            target.setSubmitUserId(sysUserId);
            target.setProjectId(taskDao.selectById(taskId).getProjectId());
            taskDeliveryDao.insert(target);
            if (StringUtils.isNotBlank(reUploadId)){
                String targetId = target.getId();
                TaskDelivery reUploadDelivery = taskDeliveryDao.selectById(reUploadId);
                reUploadDelivery.setReuploadList(StringUtils.isBlank(reUploadDelivery.getReuploadList()) ? targetId : reUploadDelivery.getReuploadList() + "||" + targetId);
//                reUploadDelivery.setIsReupload(1);
                taskDeliveryDao.updateById(reUploadDelivery);
            }

            //上传交付物日志
            TaskLog taskLog = new TaskLog("上传交付物：" + target.getDeliveryName(), target.getProjectId(), taskId);
            taskLog.setCreateUserId(sysUserId);
            taskLogDao.insert(taskLog);
        } else {
            taskDeliveryDao.updateById(taskDelivery);
        }

        // 更新Redis中的任务
        taskService.updateTaskInRedisByTaskId(taskId);

        return true;
    }

    @Override
    public boolean delete(String id, String sysUserId) {
        TaskDelivery taskDelivery = taskDeliveryDao.selectById(id);
        taskDeliveryDao.deleteById(id);
        // 更新Redis中的任务
        taskService.updateTaskInRedisByTaskId(taskDelivery.getTaskId());

        //删除交付物日志
        TaskLog taskLog = new TaskLog("删除交付物：" + taskDelivery.getDeliveryName(), taskDelivery.getProjectId(), taskDelivery.getTaskId());
        taskLog.setCreateUserId(sysUserId);
        taskLogDao.insert(taskLog);
        return true;
    }

    /**
     * 删除任务绑定的交付物
     * @return .
     */
    @Override
    public boolean deleteByIdAndReferId(String id, String referId, String afterReferList, String sysUserId) {
//        LambdaQueryWrapper<TaskDelivery> queryWrapper = new LambdaQueryWrapper<TaskDelivery>().eq(TaskDelivery::getTaskId, taskId).eq(TaskDelivery::getDeliveryId, deliveryId);
//        taskDeliveryDao.delete(queryWrapper);

        TaskDelivery taskDeliveryCurrent = taskDeliveryDao.selectById(id);
        taskDeliveryDao.deleteById(id);

//        重新上传会向reuploadList拼接新上传的id，删除的话也要将这个剔除

        if (StringUtils.isNotBlank(referId)) {
            TaskDelivery taskDelivery = taskDeliveryDao.selectById(referId);
            taskDelivery.setReuploadList(afterReferList);
            if (StringUtils.isBlank(afterReferList)) {
                taskDelivery.setIsReupload(2);
            }
            taskDeliveryDao.updateById(taskDelivery);

        }
        // 更新Redis中的任务
        taskService.updateTaskInRedisByTaskId(taskDeliveryCurrent.getTaskId());

        //删除交付物日志
        TaskLog taskLog = new TaskLog("删除交付物：" + taskDeliveryCurrent.getDeliveryName(), taskDeliveryCurrent.getProjectId(), taskDeliveryCurrent.getTaskId());
        taskLog.setCreateUserId(sysUserId);
        taskLogDao.insert(taskLog);

        return true;
    }

    /**
     * 获取任务的交付物
     * @param taskId 任务ID
     * @return .
     */
    @Override
    public List<TaskDelivery> getByTaskId(String taskId) {
//        new LambdaQueryChainWrapper<TaskDelivery>(taskDeliveryDao).eq(TaskDelivery::getTaskId, taskId).list()
        return taskDeliveryDao.getDeliveryByTaskId(taskId);
    }


    /**
     * 项目下的交付物   以交付物名称筛选
     * @param projectId 项目ID
     * @param deliveryNames 交付物名称
     * @param searchName 任务名称
     * @return .
     */
    @Override
    public Page<TaskDelivery> findByProjectIdFilter(Integer page, Integer size, String projectId, String[] deliveryNames, String searchName) {
        Page<TaskDelivery> taskDeliveryPage = new Page<>(page, size);
        return taskDeliveryPage.setRecords(taskDeliveryDao.findPageByProjectIdOrDeliveryNameFilter(taskDeliveryPage, projectId, deliveryNames, searchName));
    }
}
