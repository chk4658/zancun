package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;

/**
 * <p>
 * 角色菜单关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-10
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    List<SysMenu> findByRoleId(String roleId);

    ResultData bindMenu(String roleId, String[] menuIds);
}
