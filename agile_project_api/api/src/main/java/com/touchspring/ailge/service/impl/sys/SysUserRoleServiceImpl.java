package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.sys.SysDepartmentDao;
import com.touchspring.ailge.dao.sys.SysRoleDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.entity.sys.SysUserRole;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.service.sys.SysRoleMenuService;
import com.touchspring.ailge.service.sys.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.GetWhereInStatementUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysDepartmentDao sysDepartmentDao;

    /**
     * 根据userId查询所属角色
     * @param userId 用户ID
     * @return .
     */
    @Override
    public List<SysRole> findRoleByUserId(String userId) {
        List<SysUserRole> sysUserRoleList = new LambdaQueryChainWrapper<SysUserRole>(sysUserRoleDao).eq(SysUserRole::getSysUserId, userId)
                .orderByAsc(BaseEntity::getCreateAt).list();
        List<SysRole> sysRoles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysUserRoleList)) {
            sysUserRoleList.forEach(sysUserRole -> {
                SysRole sysRole = sysRoleDao.selectById(sysUserRole.getSysRoleId());
                //对应的菜单
                sysRole.setSysMenuList(sysRoleMenuService.findByRoleId(sysUserRole.getSysRoleId()));
                sysRoles.add(sysRole);
            });
        }
        return sysRoles;
    }

    /**
     * 根据userId查询所有可操作的菜单列表
     * @param sysRoles 角色
     * @return .
     */
    @Override
    public Set<SysMenu> findMenuUnionByRole(List<SysRole> sysRoles) {
        List<SysMenu> sysMenuList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysRoles)) {
            sysRoles.forEach(sysRole -> sysMenuList.addAll(sysRole.getSysMenuList()));
        }
        return new HashSet<>(sysMenuList);
    }

    /**
     * 根据角色ID查询所属用户
     * @param roleId 角色ID
     * @return .
     */
    @Override
    public Page<SysUser> findUserByRoleId(Integer page, Integer size, String roleId, String searchName) {
//        Page<SysUserRole> userRolePage = new LambdaQueryChainWrapper<>(sysUserRoleDao).eq(SysUserRole::getSysRoleId, roleId).select(SysUserRole::getSysUserId).page(new Page<>(page, size));

        Page<SysUser> sysUserPage = new Page<>(page, size);
//        sysUserPage.setTotal(userRolePage.getTotal());
//        List<SysUser> sysUsers = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(userRolePage.getRecords())){
//            List<String> userIds = userRolePage.getRecords().stream().map(SysUserRole::getSysUserId).collect(Collectors.toList());
//            //userIds 数量超过1000问题
////            String sql = GetWhereInStatementUtil.getString("u.id", userIds);
////            sysUsers = sysUserDao.findInSqlByUserIds(sysUserPage, sql);
//            sysUsers = sysUserDao.findInUserIdsContainsDepartmentName(userIds);
//        }
        return sysUserPage.setRecords(sysUserDao.findPageUserByRoleId(sysUserPage, roleId, searchName));
    }

    /**
     * 角色下 增加用户信息
     * @param roleId 角色ID
     * @param userIds 用户ID
     * @return .
     */
    @Override
    public boolean insertUserList(String roleId, String[] userIds) {
//        sysUserRoleDao.deleteByRoleId(roleId);
        List<SysUserRole> sysUserRoles = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(userIds)){
            for (String userId: userIds){
                //查询是否已存在
                List<SysUserRole> list = new LambdaQueryChainWrapper<SysUserRole>(sysUserRoleDao).eq(SysUserRole::getSysRoleId, roleId).eq(SysUserRole::getSysUserId, userId).list();
                if (!CollectionUtils.isEmpty(list)) continue;
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setSysRoleId(roleId);
                sysUserRole.setSysUserId(userId);
                sysUserRoles.add(sysUserRole);
            }
            this.saveBatch(sysUserRoles);
        }
        return true;
    }
}
