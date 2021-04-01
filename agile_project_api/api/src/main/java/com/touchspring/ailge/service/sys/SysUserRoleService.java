package com.touchspring.ailge.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.entity.sys.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<SysRole> findRoleByUserId(String userId);

    Set<SysMenu> findMenuUnionByRole(List<SysRole> sysRoles);

    Page<SysUser> findUserByRoleId(Integer page, Integer size, String roleId, String searchName);

    boolean insertUserList(String roleId, String[] userIds);

}
