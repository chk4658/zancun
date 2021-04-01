package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dto.TaskDTO;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectAttrService;
import com.touchspring.ailge.service.agile.TaskService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 项目任务信息表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@RestController
@RequestMapping("//task")
public class TaskController {

    private final TaskDao taskDao;
    private final TaskService taskService;
    private final ProjectAttrService projectAttrService;

    public TaskController(TaskDao taskDao, TaskService taskService, ProjectAttrService projectAttrService) {
        this.taskDao = taskDao;
        this.taskService = taskService;
        this.projectAttrService = projectAttrService;
    }

    /**
     * 保存/修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(TaskDTO taskDTO, @RequestHeader(SystemConfig.TOKEN) String token, String reuploadListIds){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return taskService.save(taskDTO, user.getId(), reuploadListIds);
    }

    /**
     * 删除任务、关联任务及关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = taskService.delete(id, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("task", taskDao.selectById(id));
    }


    /**
     * 上移
     */
    @GetMapping("up")
    public ResultData upTask(String id){
        return taskService.upTask(id);
    }

    /**
     * 下移
     */
    @GetMapping("down")
    public ResultData downTask(String id){
        return taskService.downTask(id);
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("taskList", taskDao.selectList(null));
    }

    /**
     * 根据项目ID 获取TASK
     * @param projectId 项目ID
     * @return .
     */
    @GetMapping("getByProjectId")
    public ResultData getTaskByProjectId(String projectId){
        List<Task> taskList = taskService.getByProjectId(projectId);
        List<ProjectAttr> projectAttrs = projectAttrService.findByProjectId(projectId);
        return ResultData.ok().putDataValue("taskList", taskList).putDataValue("projectAttrs", projectAttrs);
    }

    /**
     * 批量删除 并删除关联表
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] ids, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = taskService.batchDelete(ids, true, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 批量修改时间
     * @param isUpdateMilestoneFlag 是否需要更新里程碑时间
     */
    @PostMapping("updateTaskTime")
    public ResultData batchUpdateTaskTime(@RequestBody List<Task> taskList, @RequestHeader(SystemConfig.TOKEN) String token, Boolean isUpdateMilestoneFlag) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        taskService.batchUpdateTaskTime(taskList, user, isUpdateMilestoneFlag);
        return ResultData.ok();
    }

    /**
     * 复制任务关联信息
     * @param taskId 任务ID
     * @return .
     */
    @GetMapping("copy")
    public ResultData copyTask(String taskId){
        taskService.copyTask(taskId);
        return ResultData.ok();
    }

    /**
     * 全部 ----> 全部项目当前用户所有可见项目 排除草稿箱
     */
    @GetMapping("token-all")
    public ResultData findAllVisibleProject(@RequestHeader(SystemConfig.TOKEN) String token,Date startDate,Date endDate){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return ResultData.ok().putDataValue("taskList",taskService.getByUserId(user.getId(),startDate,endDate));
    }


    @GetMapping("get-list/token")
    public ResultData findListByToken(@RequestHeader(SystemConfig.TOKEN) String token,Date startDate,Date endDate){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return ResultData.ok().putDataValue("taskList",taskDao.findListUserIdAndStartTimeAtAndEndTimeAt(user.getId(),startDate,endDate));
    }

    /**
     * 查找归档任务
     */
    @GetMapping("findStMarkTaskByProjectId")
    public ResultData findStMarkTaskByProjectId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                                @RequestParam(defaultValue = "20", required = false) Integer size,
                                                String projectId){
        Page<Task> taskPage = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getProjectId, projectId).eq(Task::getStMark, BaseEnums.YES.getCode()).page(new Page<>(page, size));
        return ResultData.ok().putDataValue("stMarkTaskList", taskPage.getRecords()).putDataValue("totalAmount", taskPage.getTotal());
    }

    /**
     * 将任务设置为归档任务
     * @param taskId 最顶级任务ID
     * @return .
     */
    @GetMapping("letTaskToStMark")
    public ResultData letTaskToStMark(String taskId){
        taskService.letTaskToStMark(taskId);
        return ResultData.ok();
    }



    /**
     * 根据任务名称和项目id 找到任务
     * @return .
     */
    @GetMapping("getTaskByProjectIdAndTaskName")
    public ResultData getTaskByProjectIdAndTaskName(String projectId,String taskName){
        Task task = null;
        List<Task> tasks = taskDao.findListByProjectIdAndTaskName(projectId,taskName);
        if (!CollectionUtils.isEmpty(tasks)) task = tasks.get(0);
        return ResultData.ok().putDataValue("task",task);
    }

    /**
     * 我的任务
     */
    @GetMapping("getMyTasks")
    public ResultData getMyTasks(@RequestParam(defaultValue = "1", required = false) Integer page,
                                 @RequestParam(defaultValue = "20", required = false) Integer size,
                                 @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        Page<Task> myTaskPage = taskService.getMyTasks(user.getId(), page, size);
        return ResultData.ok().putDataValue("myTasks", myTaskPage.getRecords()).putDataValue("total", myTaskPage.getTotal());
    }


    /**
     * 根据项目ID 获取TASK
     * @param projectId 项目ID
     * @return .
     */
    @GetMapping("getTreeListByProjectId")
    public ResultData getTreeListByProjectId(String projectId){
        List<Task> taskList = taskDao.findListByProjectId(projectId);
        List<ProjectAttr> projectAttrs = projectAttrService.findByProjectId(projectId);
        return ResultData.ok().putDataValue("taskList", taskList).putDataValue("projectAttrs", projectAttrs);
    }

}
