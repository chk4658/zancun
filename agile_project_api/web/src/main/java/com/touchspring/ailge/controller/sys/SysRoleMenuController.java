package com.touchspring.ailge.controller.sys;


import com.touchspring.ailge.dao.sys.SysRoleMenuDao;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysRoleMenuService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色菜单关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-10
 */
@RestController
@RequestMapping("//sys-role-menu")
public class SysRoleMenuController {

    private final SysRoleMenuDao sysRoleMenuDao;
    private final SysRoleMenuService sysRoleMenuService;

    public SysRoleMenuController(SysRoleMenuDao sysRoleMenuDao, SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuDao = sysRoleMenuDao;
        this.sysRoleMenuService = sysRoleMenuService;
    }

    /**
     * 根据roleId查询相应的菜单信息
     * @param roleId 角色ID
     * @return 菜单信息
     */
    @GetMapping("roleMenu/{roleId}")
    public ResultData findByRoleId(@PathVariable("roleId") String roleId) {
        return ResultData.ok().putDataValue("sysMenus", sysRoleMenuService.findByRoleId(roleId));
    }

    /**
     * 绑定菜单角色 关联表信息
     * @param roleId 角色ID
     * @param menuIds 菜单ID
     * @return .
     */
    @PostMapping("roleMenu/{roleId}")
    public ResultData bindMenu(@PathVariable("roleId") String roleId, String[] menuIds) {
        return sysRoleMenuService.bindMenu(roleId, menuIds);
    }

    /**
     * 根据id删除关联表
     * @param id 角色菜单ID
     * @return .
     */
    @DeleteMapping("roleMenu/{id}")
    public ResultData deleteUser(@PathVariable("id") String id) {
        int delete = sysRoleMenuDao.deleteById(id);
        if (delete > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }



}
