package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.dao.agile.ProjectRoleEquivalentDao;
import com.touchspring.ailge.entity.agile.ProjectRoleEquivalent;
import com.touchspring.ailge.service.agile.ProjectRoleEquivalentService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目角色当量（项目当量） 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-role-equivalent")
public class ProjectRoleEquivalentController {

    private final ProjectRoleEquivalentDao projectRoleEquivalentDao;
    private final ProjectRoleEquivalentService projectRoleEquivalentService;

    public ProjectRoleEquivalentController(ProjectRoleEquivalentDao projectRoleEquivalentDao, ProjectRoleEquivalentService projectRoleEquivalentService) {
        this.projectRoleEquivalentDao = projectRoleEquivalentDao;
        this.projectRoleEquivalentService = projectRoleEquivalentService;
    }

    /**
     * 保存或更新项目当量
     */
    @PostMapping("")
    public ResultData saveOrUpdate(@RequestBody List<ProjectRoleEquivalent> projectRoleEquivalentList){
        projectRoleEquivalentService.saveOrUpdateBatch(projectRoleEquivalentList);
        return ResultData.ok();
    }

}
