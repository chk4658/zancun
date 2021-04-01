package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.entity.agile.CircleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 圈子角色与人员关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
public interface CircleUserDao extends BaseMapper<CircleUser> {

    @Select("select * from CIRCLE_USER where is_delete = 0 and CIRCLE_ID = #{circleId} and TYPE = 'OPERATE'")
    List<CircleUser> findOperateListByCircleId(@Param("circleId") String circleId);

    @Select("select * from CIRCLE_USER where is_delete = 0 and CIRCLE_ID = #{circleId} and TYPE = 'ADD_CIRCLE'")
    List<CircleUser> findAddAddCircleListByCircleId(@Param("circleId") String circleId);

    @Select("select * from CIRCLE_USER where is_delete = 0 and CIRCLE_ID = #{circleId} and TYPE = 'ADD_PROJECT'")
    List<CircleUser> findAddProjectListByCircleId(@Param("circleId") String circleId);

    @Select("select * from CIRCLE_USER where is_delete = 0 and CIRCLE_ID = #{circleId} and SYS_USER_ID = #{sysUserId}")
    CircleUser findByCircleIdAndUserId(@Param("circleId") String circleId,@Param("sysUserId") String sysUserId);

}
