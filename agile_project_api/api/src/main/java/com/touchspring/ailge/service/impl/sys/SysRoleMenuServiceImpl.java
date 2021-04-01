package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysMenuDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRoleMenu;
import com.touchspring.ailge.dao.sys.SysRoleMenuDao;
import com.touchspring.ailge.service.sys.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.mvc.ui.ResultData;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色菜单关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-10
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenu> implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;
    @Autowired
    private SysMenuDao sysMenuDao;

    @Override
    public List<SysMenu> findByRoleId(String roleId) {
        List<SysRoleMenu> sysRoleMenuList = new LambdaQueryChainWrapper<SysRoleMenu>(sysRoleMenuDao).eq(SysRoleMenu::getSysRoleId, roleId)
                .orderByAsc(BaseEntity::getCreateAt).list();
        List<SysMenu> sysMenus = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysRoleMenuList)) {
            sysRoleMenuList.forEach(sysRoleMenu -> {
                SysMenu sysMenu = sysMenuDao.selectById(sysRoleMenu.getSysMenuId());
                sysMenus.add(sysMenu);
            });
        }
        return sysMenus;
    }

    @Override
    public ResultData bindMenu(String roleId, String[] menuIds) {
        //删除当前角色的菜单
        sysRoleMenuDao.deleteByRoleId(roleId);

        //绑定新菜单
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(menuIds)) {
            for (String menuId: menuIds){
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setSysRoleId(roleId);
                roleMenu.setSysMenuId(menuId);
                sysRoleMenus.add(roleMenu);
            }
            this.saveBatch(sysRoleMenus);
        }
        return ResultData.ok();
    }
}
