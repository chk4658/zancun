package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysUserRoleService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户角色关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/sys-user-role")
public class SysUserRoleController {

    private final SysUserRoleDao sysUserRoleDao;
    private final SysUserRoleService sysUserRoleService;

    public SysUserRoleController(SysUserRoleDao sysUserRoleDao, SysUserRoleService sysUserRoleService) {
        this.sysUserRoleDao = sysUserRoleDao;
        this.sysUserRoleService = sysUserRoleService;
    }

    /**
     * 根据userId查询所属角色 及 所有可操作的菜单列表(去重)
     *
     * @param userId 用户ID
     * @return .
     */
    @GetMapping("userRole/{userId}")
    public ResultData findByUserId(@PathVariable("userId") String userId) {
        List<SysRole> roles = sysUserRoleService.findRoleByUserId(userId);
        Set<SysMenu> menus = sysUserRoleService.findMenuUnionByRole(roles);
        return ResultData.ok().putDataValue("roles", roles).putDataValue("menus", menus);
    }

    /**
     * 根据roleId查询成员信息 分页
     *
     * @param page   当前页
     * @param size   页面数
     * @param roleId 角色ID
     * @return "users", "totalAmount"
     */
    @GetMapping("roleId")
    public ResultData findUserByRoleId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                       @RequestParam(defaultValue = "20", required = false) Integer size,
                                       String roleId,
                                       @RequestParam(required = false) String searchName) {
        Page<SysUser> sysUserPage = sysUserRoleService.findUserByRoleId(page, size, roleId, searchName);
        return ResultData.ok().putDataValue("sysUserList", sysUserPage.getRecords()).putDataValue("totalAmount", sysUserPage.getTotal());
    }

    /**
     * 角色下 增加用户信息
     */
    @PostMapping("userRole/{roleId}")
    public ResultData insertUserList(@PathVariable("roleId") String roleId, String[] userIds) {
        boolean save = sysUserRoleService.insertUserList(roleId, userIds);
        if (save) return ResultData.ok();
        return ResultData.errorRequest();
    }

    /**
     * 根据id删除关联表
     *
     * @param id 角色用户ID
     * @return .
     */
    @DeleteMapping("userRole/{id}")
    public ResultData deleteById(@PathVariable("id") String id) {
        int delete = sysUserRoleDao.deleteById(id);
        if (delete > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }


    /**
     * 根据userIds,roleId删除关联表
     *
     * @param userIds
     * @param roleId
     * @return .
     */
    @DeleteMapping("userRole/unbind")
    public ResultData deleteByRoleIdAndUserId(String[] userIds, String roleId) {
        int delete = 0;
        for (String userId: userIds){
            delete = sysUserRoleDao.deleteByRoleIdAndUserId(roleId, userId);
            if (delete > 0) {
                continue;
            }else {
                break;
            }
        }

        if (delete > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }


}
