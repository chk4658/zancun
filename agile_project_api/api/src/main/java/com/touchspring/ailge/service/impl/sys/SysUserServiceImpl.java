package com.touchspring.ailge.service.impl.sys;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.ldap.UserDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysCompany;
import com.touchspring.ailge.entity.sys.SysDepartment;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.IsEnabled;
import com.touchspring.ailge.service.ldap.UserService;
import com.touchspring.ailge.service.sys.SysCompanyService;
import com.touchspring.ailge.service.sys.SysDepartmentService;
import com.touchspring.ailge.service.sys.SysUserService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SysCompanyService sysCompanyService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private UserService userService;

    /**
     * test
     * 根据名称模糊查询
     */
    @Override
    public List<SysUser> findByNameOrAccount(Integer page, Integer size, String name, String companyId, String departmentId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name))
            queryWrapper.and(i -> i.nested(
                    j -> j.like(SysUser::getAccount, name)).or(j -> j.like(SysUser::getRealName, name)));
        if (StringUtils.isNotBlank(companyId) && StringUtils.isNotBlank(departmentId))
            queryWrapper.eq(SysUser::getCompanyId, companyId).eq(SysUser::getDepartmentId, departmentId).eq(SysUser::getIsDelete, 0);
        Page<SysUser> sysUserPage = sysUserDao.selectPage(new Page<>(page, size), queryWrapper);
        return sysUserPage.getRecords();
    }

    @Override
    public Page<SysUser> findByCompanyAndDepartment(Integer page, Integer size, String companyId, String departmentId, String name, Long enabled) {
        Page<SysUser> sysUserPage = new Page<>(page, size);
        if (enabled == null)
            enabled = IsEnabled.NOT_DISABLED.getCode();
        return sysUserPage.setRecords(sysUserDao.findByCompanyAndDepartment(sysUserPage, companyId, departmentId, name, enabled));
    }

    @Override
    public boolean save(SysUser sysUser) {
        User user = userService.findOneBySAMAccountName(sysUser.getAccount());
        if (user == null) return false;
        // 公司
        return this.setSysUser(user);
    }

    /**
     * 禁用或解除禁用
     *
     * @param userIds 用户ID
     * @param flag    true->禁用 false->解除禁用
     * @return .
     */
    @Override
    public boolean batchEnabled(String[] userIds, boolean flag) {
        Arrays.asList(userIds).forEach(userId -> {
            SysUser sysUser = sysUserDao.selectById(userId);
            if (sysUser != null) {
                if (flag) sysUser.setEnabled(IsEnabled.DISABLE.getCode());
                else sysUser.setEnabled(IsEnabled.NOT_DISABLED.getCode());
                sysUserDao.updateById(sysUser);
            }
        });
        return true;
    }

    @Override
    public boolean pullData() {
        userDao.findAll().forEach(user -> {
            log.info(JSON.toJSONString(user));
             this.setSysUser(user);
        });
        return true;
    }

    /**
     * 插入信息
     * @param user
     * @return
     */
    private boolean setSysUser(User user) {
        if (StringUtils.isBlank(user.getCompany())) return false;
        SysCompany sysCompany = null;
        QueryWrapper<SysCompany> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.lambda().eq(SysCompany::getFullName, user.getCompany());
        sysCompany = sysCompanyService.getOne(companyQueryWrapper);
        if (sysCompany == null){
            sysCompany = new SysCompany();
            sysCompany.setFullName(user.getCompany());
            sysCompanyService.save(sysCompany);
        }

        // 部门
        if(StringUtils.isBlank(user.getDepartment()))  return false;
        SysDepartment sysDepartment = null;
        QueryWrapper<SysDepartment> sysDepartmentQueryWrapper = new QueryWrapper<>();
        sysDepartmentQueryWrapper.lambda().eq(SysDepartment::getFullName, user.getDepartment());
        sysDepartment = sysDepartmentService.getOne(sysDepartmentQueryWrapper);
        if (sysDepartment == null) {
            sysDepartment = new SysDepartment();
            sysDepartment.setFullName(user.getDepartment());
            sysDepartment.setCompanyId(sysCompany.getId());
            sysDepartmentService.save(sysDepartment);
        }

        String realName = user.getName().substring(user.getName().indexOf("(") + 1, user.getName().lastIndexOf(")"));
        String spell = user.getName().substring(0, user.getName().indexOf("(")).trim();

        SysUser sysUser = new SysUser(user);
        sysUser.setRealName(realName);
        sysUser.setSpell(spell);
        sysUser.setCompanyId(sysCompany.getId());
        sysUser.setDepartmentId(sysDepartment.getId());
        QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(SysUser::getAccount,sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(userQueryWrapper);
        if (sysUser1 == null) {
            sysUser.setId(IdWorker.nextId());
            sysUserDao.insert(sysUser);
        } else {
            sysUser.setId(sysUser1.getId());
            sysUserDao.updateById(sysUser);
        }
        return true;
    }

}
