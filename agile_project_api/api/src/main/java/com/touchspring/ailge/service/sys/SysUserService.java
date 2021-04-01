package com.touchspring.ailge.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
public interface SysUserService extends IService<SysUser> {

    List<SysUser> findByNameOrAccount(Integer page, Integer size, String name, String companyId, String departmentId);

    Page<SysUser> findByCompanyAndDepartment(Integer page, Integer size, String companyId, String departmentId, String name, Long enabled);

    boolean save(SysUser sysUser);

    boolean batchEnabled(String[] userIds, boolean flag);

    boolean pullData();
}
