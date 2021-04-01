package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectLogDao;
import com.touchspring.ailge.entity.agile.ProjectLog;
import com.touchspring.ailge.service.agile.ProjectLogService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 项目日志表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-log")
public class ProjectLogController {

    private final ProjectLogDao projectLogDao;
    private final ProjectLogService projectLogService;

    public ProjectLogController(ProjectLogDao projectLogDao, ProjectLogService projectLogService) {
        this.projectLogDao = projectLogDao;
        this.projectLogService = projectLogService;
    }

    /**
     * 查看项目所有Log
     */
    @GetMapping("list")
    public ResultData findAllLogByProjectId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                            @RequestParam(defaultValue = "20", required = false) Integer size,
                                            String projectId,
                                            @RequestParam(required = false) String searchName){
        Page<ProjectLog> projectLogPage = new Page<>(page, size);
        projectLogPage.setRecords(projectLogDao.findByProjectId(projectLogPage, projectId, searchName));
        return ResultData.ok().putDataValue("projectLogs", projectLogPage.getRecords()).putDataValue("totalAmount", projectLogPage.getTotal());
    }

}
