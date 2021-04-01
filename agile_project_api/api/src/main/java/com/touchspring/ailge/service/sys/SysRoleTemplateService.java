package com.touchspring.ailge.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
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
public interface SysRoleTemplateService extends IService<SysRoleTemplate> {

    Page<SysRoleTemplate> findAll(Integer page, Integer size, Long enabled);

    boolean batchEnabled(String[] roleIds, boolean flag);

    List<SysRoleTemplate> searchByPage(Page page, String roleName, Long enabled);

    boolean save(SysRoleTemplate sysRoleTemplate);

    List<SysRoleTemplate> findListByRoleNames(List<String> roleNames);

}
