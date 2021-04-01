package com.touchspring.ailge.controller.agile;


import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dao.agile.TaskDataDao;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskData;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.RedisTableName;
import com.touchspring.ailge.service.agile.TaskDataService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 任务数据表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@RestController
@RequestMapping("//task-data")
public class TaskDataController {

    private final TaskDataService taskDataService;
    private final TaskDataDao taskDataDao;

    public TaskDataController(TaskDataService taskDataService, TaskDataDao taskDataDao) {
        this.taskDataService = taskDataService;
        this.taskDataDao = taskDataDao;
    }

    /**
     * 保存/更新
     * @param taskData 任务数据
     * @return .
     */
    @PostMapping("")
    public ResultData saveOrUpdate(TaskData taskData, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        taskDataService.save(taskData, user.getId());
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id) {
        taskDataService.delete(id);
        return ResultData.ok();
    }

}
