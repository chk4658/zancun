package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysRole;
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
public interface SysRoleDao extends BaseMapper<SysRole> {

    List<SysRole> searchByRoleName(@Param("roleName") String roleName);

}
