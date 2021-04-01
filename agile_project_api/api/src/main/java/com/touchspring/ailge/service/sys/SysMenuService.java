package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findAll();

    boolean delete(String id);

    List<SysMenu> search(String value);

    ResultData insert(SysMenu sysMenu);

    ResultData upMenu(String id);

    ResultData downMenu(String id);

    List<SysMenu> findTreeMenuByRole(List<SysRole> sysRoles);

}
