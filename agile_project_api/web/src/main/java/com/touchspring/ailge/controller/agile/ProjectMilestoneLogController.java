package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectMilestoneLogDao;
import com.touchspring.ailge.entity.agile.ProjectMilestoneLog;
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
@RequestMapping("//project-milestone-log")
public class ProjectMilestoneLogController {

    private final ProjectMilestoneLogDao projectMilestoneLogDao;

    public ProjectMilestoneLogController(ProjectMilestoneLogDao projectMilestoneLogDao) {
        this.projectMilestoneLogDao = projectMilestoneLogDao;
    }

    /**
     * 查看里程碑所有Log
     */
    @GetMapping("list")
    public ResultData findAllLogProjectMilestoneId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                                   @RequestParam(defaultValue = "20", required = false) Integer size,
                                                   String projectMilestoneId,
                                                   @RequestParam(required = false) String searchName){
        Page<ProjectMilestoneLog> projectMilestoneLogPage = new Page<>(page, size);
        projectMilestoneLogPage.setRecords(projectMilestoneLogDao.findByProjectMilestoneId(projectMilestoneLogPage, projectMilestoneId, searchName));
        return ResultData.ok().putDataValue("projectMilestoneLogs", projectMilestoneLogPage.getRecords()).putDataValue("totalAmount", projectMilestoneLogPage.getTotal());
    }
}
