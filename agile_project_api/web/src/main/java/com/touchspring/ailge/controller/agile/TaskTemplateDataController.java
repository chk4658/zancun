package com.touchspring.ailge.controller.agile;

import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.dao.agile.TaskTemplateDataDao;
import com.touchspring.ailge.entity.agile.TaskTemplateData;
import com.touchspring.ailge.service.agile.TaskTemplateDataService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 模板任务数据表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@RestController
@RequestMapping("//task-template-data")
public class TaskTemplateDataController {

    private final TaskTemplateDataDao taskTemplateDataDao;
    private final TaskTemplateDataService templateDataService;


    public TaskTemplateDataController(TaskTemplateDataDao taskTemplateDataDao, TaskTemplateDataService templateDataService) {
        this.taskTemplateDataDao = taskTemplateDataDao;
        this.templateDataService = templateDataService;
    }

    /**
     * 保存/更新
     * @param taskTemplateData 任务数据
     * @return .
     */
    @PostMapping("")
    public ResultData saveOrUpdate(TaskTemplateData taskTemplateData) {
        templateDataService.save(taskTemplateData);
        return ResultData.ok().putDataValue("taskTemplateData",taskTemplateData);
    }

    /**
     * 保存/更新
     * @param taskTemplateDataList 任务数据
     * @return .
     */
    @PostMapping("saveOrUpdateList")
    public ResultData saveOrUpdateList(@RequestParam("taskTemplateDatas") String taskTemplateDatas) {
        List<TaskTemplateData> taskTemplateDataList = JSON.parseArray(taskTemplateDatas,TaskTemplateData.class);
        templateDataService.saveList(taskTemplateDataList);
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id) {
        taskTemplateDataDao.deleteById(id);
        return ResultData.ok();
    }

}
