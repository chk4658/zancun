package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.entity.agile.CircleRoleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 圈子角色与人员关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
public interface CircleRoleUserDao extends BaseMapper<CircleRoleUser> {

    @Select("select * from CIRCLE_ROLE_USER where is_delete = 0 AND CIRCLE_ROLE_ID = #{circleRoleId}")
    @Results({
            @Result(column = "SYS_USER_ID", property = "sysUserId"),
            @Result(column = "SYS_USER_ID", property = "sysUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById"))
    })
    List<CircleRoleUser> findListByCircleRoleId(String circleRoleId);

    @Delete("DELETE FROM CIRCLE_ROLE_USER WHERE CIRCLE_ROLE_ID = #{circleRoleId}")
    int deleteByCircleRoleId(@Param("circleRoleId") String circleRoleId);

    @Select("SELECT cru.* FROM CIRCLE_ROLE_USER  cru " +
            "LEFT JOIN CIRCLE_ROLE cr on cru.CIRCLE_ROLE_ID = cr.id WHERE cru.SYS_USER_ID = #{sysUserId} and cr.CIRCLE_ID = #{circleId}")
    List<CircleRoleUser>  findListByCircleIdAndUserId(@Param("circleId") String circleId,@Param("sysUserId") String sysUserId);

}
