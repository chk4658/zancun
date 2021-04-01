package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统角色信息表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysRoleService extends IService<SysRole> {

    boolean save(SysRole sysRole);

    boolean delete(String id);

    List<SysRole> search(String roleName);

}
