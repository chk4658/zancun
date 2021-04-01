package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 用户角色关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Component
public interface SysUserRoleDao extends BaseMapper<SysUserRole> {

    @Delete("DELETE FROM SYS_USER_ROLE WHERE SYS_ROLE_ID = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);



    @Delete("DELETE FROM SYS_USER_ROLE WHERE SYS_ROLE_ID = #{roleId} AND SYS_USER_ID = #{userId}")
    int deleteByRoleIdAndUserId(@Param("roleId") String roleId, @Param("userId") String userId);

    @Select("SELECT * FROM SYS_USER_ROLE sur LEFT JOIN SYS_ROLE sr ON sur.SYS_ROLE_ID = sr.ID " +
            "WHERE sur.IS_DELETE = 0 AND sr.IS_DELETE = 0 AND sr.NAME = #{roleName} AND sur.SYS_USER_ID = #{sysUserId} AND rownum<=1")
    SysUserRole findByUserIdAndRoleNameLimit1(@Param("sysUserId") String sysUserId, @Param("roleName") String roleName);

}
