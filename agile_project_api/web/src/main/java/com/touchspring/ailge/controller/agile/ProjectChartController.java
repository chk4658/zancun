package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.dto.ProjectChartDTO;
import com.touchspring.ailge.entity.agile.Circle;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.core.mvc.ui.ResultData;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目图表
 */
@RestController
@RequestMapping("//project-chart")
public class ProjectChartController {

    private final ProjectDao projectDao;

    public ProjectChartController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    /**
     * 项目统计  每月新增项目数
     */
    @GetMapping("newProjectNumPerMonth")
    public ResultData getNewProjectNumPerMonth() {
        List<ProjectChartDTO> month = projectDao.getNewProjectNumPerMonth();
        return ResultData.ok().putDataValue("month", month);
    }

    /**
     * 按状态统计项目
     */
    @GetMapping("projectNumByStatus")
    public ResultData getProjectNumByStatus() {
        List<ProjectChartDTO> projectNumByStatus = projectDao.getProjectNumByStatus();
        return ResultData.ok().putDataValue("projectNumByStatus", projectNumByStatus);
    }

    /**
     * 项目任务数Top10
     */
    @GetMapping("topTenTaskNumInProject")
    public ResultData getTopTenTaskNumInProject() {
        List<ProjectChartDTO> topTenTaskNumInProject = projectDao.getTopTenTaskNumInProject();

        List<Project> projects = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topTenTaskNumInProject)) {
            topTenTaskNumInProject.forEach(taskNumInProject -> {
                Project project = projectDao.selectById(taskNumInProject.getProjectId());
                if (project != null) {
                    project.setTaskNum(taskNumInProject.getNum());
                    projects.add(project);
                }
            });
        }
        return ResultData.ok().putDataValue("topTenTaskNumInProject", projects);
    }

    /**
     * 项目参与人数Top10
     */
    @GetMapping("topTenUserNumInProject")
    public ResultData getTopTenUserNumInProject() {
        List<ProjectChartDTO> topTenUserNumInProject = projectDao.getTopTenUserNumInProject();

        List<Project> projects = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topTenUserNumInProject)) {
            topTenUserNumInProject.forEach(userNumInProject -> {
                Project project = projectDao.selectById(userNumInProject.getProjectId());
                if (project != null) {
                    project.setUserNum(userNumInProject.getNum().longValue());
                    projects.add(project);
                }
            });
        }
        return ResultData.ok().putDataValue("topTenUserNumInProject", projects);
    }

    /**
     * 多选项目比较任务新增情况
     * （质量阀任务可参考类似方式生成表格）
     */
    @PostMapping("newTaskNumPerDayInProjectIds")
    public ResultData getNewTaskNumPerDayInProjectIds(String[] projectIds, String startDate, String endDate) {
        if (ArrayUtils.isEmpty(projectIds) || startDate == null || endDate == null) return ResultData.notFound();
        List<ProjectChartDTO> perDayInProjectIds = projectDao.getNewTaskNumPerDayInProjectIds(Arrays.asList(projectIds), startDate, endDate);
//        Map<String, List<ProjectChartDTO>> listMap = perDayInProjectIds.stream().collect(Collectors.groupingBy(ProjectChartDTO::getDateTime));
//        listMap.forEach((k, v) -> {
//            v.forEach(projectChartDTO -> {
//                Project project = projectDao.selectById(projectChartDTO.getProjectId());
//                projectChartDTO.setProject(project);
//            });
//        });

        perDayInProjectIds.forEach(projectChartDTO -> {
            Project project = projectDao.selectById(projectChartDTO.getProjectId());
            projectChartDTO.setProject(project);
        });
        return ResultData.ok().putDataValue("listMap", perDayInProjectIds);
    }
}
