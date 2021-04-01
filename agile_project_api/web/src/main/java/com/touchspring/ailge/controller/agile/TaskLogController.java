package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.TaskLogDao;
import com.touchspring.ailge.entity.agile.TaskLog;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 任务记录表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@RestController
@RequestMapping("//task-log")
public class TaskLogController {

    private final TaskLogDao taskLogDao;

    public TaskLogController(TaskLogDao taskLogDao) {
        this.taskLogDao = taskLogDao;
    }

    /**
     * 查看任务所有Log
     */
    @GetMapping("list")
    public ResultData findAllLogByTaskId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                         @RequestParam(defaultValue = "20", required = false) Integer size,
                                         String taskId,
                                         @RequestParam(required = false) String searchName){
        Page<TaskLog> taskLogPage = new Page<>(page, size);
        taskLogPage.setRecords(taskLogDao.findByTaskId(taskLogPage, taskId, searchName));
        return ResultData.ok().putDataValue("taskLogs", taskLogPage.getRecords()).putDataValue("totalAmount", taskLogPage.getTotal());
    }
}
