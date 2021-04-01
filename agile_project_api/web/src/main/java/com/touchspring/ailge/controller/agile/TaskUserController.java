package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dao.agile.TaskUserDao;
import com.touchspring.ailge.entity.agile.TaskUser;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.service.agile.TaskUserService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 临时任务-人 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@RestController
@RequestMapping("//task-user")
public class TaskUserController {

    private final TaskUserDao taskUserDao;
    private final TaskUserService taskUserService;


    public TaskUserController(TaskUserDao taskUserDao, TaskUserService taskUserService) {
        this.taskUserDao = taskUserDao;
        this.taskUserService = taskUserService;
    }

    /**
     * 查找当前用户的临时任务  当前用户为审核人、负责人 或者临时任务创建人
     */
    @GetMapping("list")
    public ResultData findCurUserTemporaryTask(@RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return ResultData.ok().putDataValue("taskUserList", taskUserService.findCurUserTemporaryTask(user.getId()));
    }

    /**
     * 保存
     */
    @PostMapping("")
    public ResultData saveOrUpdate(TaskUser taskUser, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        taskUserService.save(taskUser, user.getId());
        return ResultData.ok();
    }

}
