package com.touchspring.ailge.controller.agile;


import com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao;
import com.touchspring.ailge.dto.ProjectTemplateTaskDTO;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateTaskService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 项目模板任务表
 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template-task")
public class ProjectTemplateTaskController {

    private final ProjectTemplateTaskDao projectTemplateTaskDao;
    private final ProjectTemplateTaskService projectTemplateTaskService;

    public ProjectTemplateTaskController(ProjectTemplateTaskDao projectTemplateTaskDao, ProjectTemplateTaskService projectTemplateTaskService) {
        this.projectTemplateTaskDao = projectTemplateTaskDao;
        this.projectTemplateTaskService = projectTemplateTaskService;
    }

    /**
     * 保存/修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplateTaskDTO projectTemplateTaskDTO){
        projectTemplateTaskService.save(projectTemplateTaskDTO);
        return ResultData.ok().putDataValue("id",projectTemplateTaskDTO.getId());
    }

    /**
     * 删除任务、关联任务及关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        boolean delete = projectTemplateTaskService.delete(id);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 批量删除 并删除关联表
     */
    @DeleteMapping("batchDelete")
    public ResultData batchDelete(String[] ids){
        boolean delete = projectTemplateTaskService.batchDelete(ids);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("task", projectTemplateTaskDao.selectById(id));
    }

    /**
     * 上移
     */
    @GetMapping("up")
    public ResultData upTask(String id){
        return projectTemplateTaskService.upTask(id);
    }

    /**
     * 下移
     */
    @GetMapping("down")
    public ResultData downTask(String id){
        return projectTemplateTaskService.downTask(id);
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("taskList", projectTemplateTaskDao.selectList(null));
    }

}
