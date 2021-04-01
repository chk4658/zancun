package com.touchspring.ailge.controller.agile;


import com.touchspring.ailge.dao.agile.ProjectDepartmentDao;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 项目与部门关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@RestController
@RequestMapping("//project-department")
public class ProjectDepartmentController {

    private final ProjectDepartmentDao projectDepartmentDao;

    public ProjectDepartmentController(ProjectDepartmentDao projectDepartmentDao) {
        this.projectDepartmentDao = projectDepartmentDao;
    }

    /**
     * 根据项目ID查找部门
     * @param projectId 项目ID
     * @return .
     */
    @GetMapping("listByProjectId")
    public ResultData findDepartmentsByProjectId(String projectId){
        return ResultData.ok().putDataValue("projectDepartments", projectDepartmentDao.findByProjectId(projectId));
    }

}
