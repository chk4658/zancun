package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 角色菜单关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-10
 */
@Component
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenu> {

    @Delete("DELETE FROM SYS_ROLE_MENU WHERE SYS_ROLE_ID = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);

}
