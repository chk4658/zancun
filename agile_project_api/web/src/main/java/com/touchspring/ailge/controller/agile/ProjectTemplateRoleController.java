package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectTemplateRoleDao;
import com.touchspring.ailge.entity.agile.ProjectTemplateRole;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateRoleService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 项目模板角色关系表
 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template-role")
public class ProjectTemplateRoleController {

    private final ProjectTemplateRoleService projectTemplateRoleService;
    private final ProjectTemplateRoleDao projectTemplateRoleDao;

    public ProjectTemplateRoleController(ProjectTemplateRoleService projectTemplateRoleService, ProjectTemplateRoleDao projectTemplateRoleDao) {
        this.projectTemplateRoleService = projectTemplateRoleService;
        this.projectTemplateRoleDao = projectTemplateRoleDao;
    }

    /**
     * 查看
     */
    @GetMapping("")
    public ResultData findAllByProjectId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                         @RequestParam(defaultValue = "20", required = false) Integer size,
                                         String projectTemplateId){
        LambdaQueryWrapper<ProjectTemplateRole> queryWrapper = new LambdaQueryWrapper<ProjectTemplateRole>().eq(ProjectTemplateRole::getProjectTemplateId, projectTemplateId);
        Page<ProjectTemplateRole> projectTemplateRolePage = projectTemplateRoleDao.selectPage(new Page<>(page, size), queryWrapper);
        return ResultData.ok().putDataValue("projectTemplateRoleList", projectTemplateRolePage.getRecords()).putDataValue("totalAmount", projectTemplateRolePage.getTotal());
    }

    /**
     * 添加/修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplateRole projectTemplateRole){
        boolean save = projectTemplateRoleService.save(projectTemplateRole);
        if (save) return ResultData.ok();
        return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectRole", projectTemplateRoleDao.selectById(id));
    }

    /**
     * 删除 / 批量删除 并删除关联表
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] ids){
        int deleteBatchIds = projectTemplateRoleDao.deleteBatchIds(Arrays.asList(ids));
        if (deleteBatchIds > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

}
