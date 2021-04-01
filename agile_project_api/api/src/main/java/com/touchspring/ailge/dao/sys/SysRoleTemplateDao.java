package com.touchspring.ailge.dao.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统角色信息表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysRoleTemplateDao extends BaseMapper<SysRoleTemplate> {

    List<SysRoleTemplate> findByRoleNameAndIsEnabled(Page page, @Param("roleName") String roleName, @Param("enabled") Long enabled);

}
