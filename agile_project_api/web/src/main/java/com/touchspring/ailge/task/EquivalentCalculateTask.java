package com.touchspring.ailge.task;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.EquivalentDao;
import com.touchspring.ailge.dao.agile.ProjectRoleDao;
import com.touchspring.ailge.dao.agile.ProjectRoleEquivalentDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.entity.agile.Equivalent;
import com.touchspring.ailge.entity.agile.ProjectRole;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.enums.TaskStatusEnum;
import com.touchspring.ailge.service.agile.EquivalentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 当量计算
 */
@Service
public class EquivalentCalculateTask {

    @Autowired
    private EquivalentDao equivalentDao;
    @Autowired
    private ProjectRoleEquivalentDao projectRoleEquivalentDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private EquivalentService equivalentService;

    /**
     * 每个月初计算当量
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void equivalentCal(){

        //取出上一个月需要计算当量的人及相关信息
        YearMonth y2 = YearMonth.now().minusMonths(1);
        String curMonth = y2.toString();
        this.calculate(curMonth);
    }

    private double getTaskEquivalentValue(List<Task> taskList, Double percent){
        double taskEquivalentValue = 0.0;
        Map<String, List<Task>> reviewMap = taskList.stream().collect(Collectors.groupingBy(Task::getReviewProjectRoleId));
        for (Map.Entry<String, List<Task>> map: reviewMap.entrySet()){
            ProjectRole projectRole = projectRoleDao.selectById(map.getKey());
            if (projectRole != null && projectRole.getTaskEquivalent() != null)
                taskEquivalentValue += map.getValue().size() * Double.valueOf(projectRole.getTaskEquivalent()) * percent;
        }
        return taskEquivalentValue;
    }

    /**
     * 计算当前月份当量管理
     * @param curMonth 当前月份
     */
    public void calculate(String curMonth){
        List<Equivalent> equivalents = new LambdaQueryChainWrapper<Equivalent>(equivalentDao).eq(Equivalent::getMonths, curMonth).list();
        if (CollectionUtils.isEmpty(equivalents)) return;

        equivalents.forEach(equivalent -> {
            //项目当量
            Double projectEquivalentValue = projectRoleEquivalentDao.getProjectEquivalentByUserIdAndMonth(equivalent.getSysUserId(), curMonth);
            if (projectEquivalentValue == null)
                projectEquivalentValue = 0.0;
            projectEquivalentValue = projectEquivalentValue > 2 ? 2 : projectEquivalentValue;

            //任务当量 ----> 当前月份，用户完成的任务
            double taskEquivalentValue = 0.0;
            List<Task> reviewTasks = taskDao.listByReviewSysUserId(TaskStatusEnum.COMPLETED.getCode(), curMonth, equivalent.getSysUserId());
            if (!CollectionUtils.isEmpty(reviewTasks))
                taskEquivalentValue += this.getTaskEquivalentValue(reviewTasks, 0.9);
            //获取当前用户的审核任务
            List<Task> confirmTasks = taskDao.listByConfirmSysUserId(TaskStatusEnum.COMPLETED.getCode(), curMonth, equivalent.getSysUserId());
            if (!CollectionUtils.isEmpty(confirmTasks))
                taskEquivalentValue += this.getTaskEquivalentValue(confirmTasks, 0.1);
            taskEquivalentValue = taskEquivalentValue > 2 ? 2 : taskEquivalentValue;

            //工作当量 ---->（项目当量 + 任务当量 + 其他当量）* 交付质量 + 知识
            double equivalentSum = projectEquivalentValue + taskEquivalentValue + (equivalent.getOther() == null? 0: equivalent.getOther());
            if (equivalentSum > 2)
                equivalentSum = 2;
            double result = equivalentSum * (equivalent.getDeliveryQuality() == null? 0: equivalent.getDeliveryQuality()) + (equivalent.getKnowledge() == null? 0: equivalent.getKnowledge());

            equivalent.setProject(projectEquivalentValue);
            equivalent.setTask(taskEquivalentValue);
            equivalent.setWork(result > 2 ? 2: result);
        });
        equivalentService.updateBatchById(equivalents);
    }

}
