package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectRoleUserDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.agile.ProjectRoleUser;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectRoleUserService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目角色与人员关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@RestController
@RequestMapping("//project-role-user")
public class ProjectRoleUserController {

    @Autowired
    private SysUserDao sysUserDao;

    private final ProjectRoleUserDao projectRoleUserDao;
    private final ProjectRoleUserService projectRoleUserService;

    public ProjectRoleUserController(ProjectRoleUserDao projectRoleUserDao, ProjectRoleUserService projectRoleUserService) {
        this.projectRoleUserDao = projectRoleUserDao;
        this.projectRoleUserService = projectRoleUserService;
    }


    /**
     * 查看
     */
    @GetMapping("")
    public ResultData list(@RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size,
                           String projectRoleId){
        Page<SysUser> sysUserPage = projectRoleUserService.findByProjectRoleId(page, size, projectRoleId);
//        if (sysUserPage == null) return new ResultData(ResultStatus.NOT_EXIST.getCode(), ResultStatus.NOT_EXIST.getMessage());
        return ResultData.ok().putDataValue("sysUserList", sysUserPage.getRecords()).putDataValue("totalAmount", sysUserPage.getTotal());
    }

    /**
     * 添加/修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(String projectRoleId, String userId, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        projectRoleUserService.save(projectRoleId, userId, user.getId());
        return ResultData.ok();
    }


    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String projectRoleId, String userId){
        boolean flag = projectRoleUserService.delete(projectRoleId, userId);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectRoleUser", projectRoleUserDao.selectById(id));
    }

    /**
     * 查找当前项目下的人员数量
     */
    @GetMapping("getUserNum")
    public ResultData findCurProjectUserNumByProjectId(String projectId) {
        Integer sysUserNum = projectRoleUserService.findCurProjectUserNumByProjectId(projectId);
        return ResultData.ok().putDataValue("sysUserNum", sysUserNum);
    }

}
