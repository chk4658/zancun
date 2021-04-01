package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Circle;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 圈子角色关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleRoleDao extends BaseMapper<CircleRole> {

    @Select("select ROLE_NAME from CIRCLE_ROLE where CIRCLE_ID = #{circleId} and IS_DELETE = 0")
    List<String> findRoleNameByCircleId(String circleId);

    /**
     * 分页查找
     * @param page
     * @param circleRole
     * @return
     */
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "circleRoleUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleRoleUserDao.findListByCircleRoleId"))
    })
    @Select("select * from CIRCLE_ROLE where is_delete = 0 AND CIRCLE_ID = #{circleRole.circleId} ORDER BY CREATE_AT, ID")
    List<CircleRole> findListPage(Page<CircleRole> page,@Param("circleRole") CircleRole circleRole);

}
