package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectRoleDao;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.ailge.entity.agile.ProjectRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectRoleService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目角色关系表
 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-role")
public class ProjectRoleController {

    private final ProjectRoleService projectRoleService;
    private final ProjectRoleDao projectRoleDao;

    public ProjectRoleController(ProjectRoleService projectRoleService, ProjectRoleDao projectRoleDao) {
        this.projectRoleService = projectRoleService;
        this.projectRoleDao = projectRoleDao;
    }

    /**
     * 查看
     */
    @GetMapping("")
    public ResultData findAllByProjectId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                         @RequestParam(defaultValue = "20", required = false) Integer size,
                                         ProjectRole projectRole,
                                         @RequestParam(defaultValue = "", required = false) String[] roleNames,
                                         @RequestParam(defaultValue = "", required = false) String[] userNames){
        LambdaQueryWrapper<ProjectRole> queryWrapper = new LambdaQueryWrapper<ProjectRole>().eq(ProjectRole::getProjectId, projectRole.getProjectId());
//        Page<ProjectRole> projectRolePage = projectRoleDao.selectPage(new Page<>(page, size), queryWrapper);
        Page<ProjectRole> projectRolePage = new Page<>(page, size);
        projectRolePage.setRecords(projectRoleDao.findListPageFilter(projectRolePage,projectRole,roleNames,userNames));

        List<ProjectRole> list = projectRoleDao.findList(projectRole);

        return ResultData.ok().putDataValue("projectRoleList", projectRolePage.getRecords())
                .putDataValue("totalAmount", projectRolePage.getTotal())
                .putDataValue("projectRoleListAll", list)
                .putDataValue("totalAmountAll", list.size());


    }

    /**
     * 添加/修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectRole projectRole, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        ResultData resultData = projectRoleService.insert(projectRole, user.getId());
        return new ResultData(resultData.getCode(), resultData.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectRole", projectRoleService.getById(id));
    }

    /**
     * 删除 并删除关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean flag = projectRoleService.batchDelete(new String[]{id}, user.getId());
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 批量删除 并删除关联表
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] ids, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = projectRoleService.batchDelete(ids, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

}
