package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysRoleMenuDao;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.dao.sys.SysRoleDao;
import com.touchspring.ailge.entity.sys.SysRoleMenu;
import com.touchspring.ailge.entity.sys.SysUserRole;
import com.touchspring.ailge.service.sys.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 系统角色信息表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    /**
     * 保存或更新
     */
    @Override
    public boolean save(SysRole sysRole) {
        SysRole target;
        if (StringUtils.isNotBlank(sysRole.getId())){
            target = sysRoleDao.selectById(sysRole.getId());
            if (!StringUtils.equals(target.getName(), sysRole.getName())) {
                if (this.findByName(sysRole.getName())) return false;
            }
            sysRoleDao.updateById(sysRole);
        }else {
            if (this.findByName(sysRole.getName())) return false;
            sysRoleDao.insert(sysRole);
        }
        return true;
    }

    /**
     * 根据角色id删除角色
     * @param id 角色ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
        //角色用户
        LambdaQueryWrapper<SysUserRole> sysUserRoleLambdaQueryWrapper = new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getSysRoleId, id);
        sysUserRoleDao.delete(sysUserRoleLambdaQueryWrapper);
        //角色菜单
        LambdaQueryWrapper<SysRoleMenu> sysRoleMenuLambdaQueryWrapper = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getSysRoleId, id);
        sysRoleMenuDao.delete(sysRoleMenuLambdaQueryWrapper);
        sysRoleDao.deleteById(id);
        return true;
    }

    @Override
    public List<SysRole> search(String roleName) {
        return sysRoleDao.searchByRoleName(roleName);
    }

    /**
     * 根据名称查找
     * @param name 名称
     * @return .
     */
    private boolean findByName(String name) {
        List<SysRole> sysRoleList = new LambdaQueryChainWrapper<SysRole>(sysRoleDao).eq(SysRole::getName, name).list();
        if (CollectionUtils.isEmpty(sysRoleList)) return false;
        return true;
    }
}
