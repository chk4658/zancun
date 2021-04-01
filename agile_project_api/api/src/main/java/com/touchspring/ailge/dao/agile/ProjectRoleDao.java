package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目角色关系表
 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectRoleDao extends BaseMapper<ProjectRole> {



    /**
     * 分页查找
     * @param page
     * @param projectRole
     * @return
     */
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "projectRoleUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectRoleUserDao.findListByProjectRoleId"))
    })
    @Select("select * from PROJECT_ROLE where is_delete = 0 AND PROJECT_ID = #{projectRole.projectId}")
    List<ProjectRole> findListPage(Page<ProjectRole> page, @Param("projectRole") ProjectRole projectRole);


    List<ProjectRole> findListPageFilter(Page<ProjectRole> page, @Param("projectRole") ProjectRole projectRole,@Param("roleNames") String[] roleNames,@Param("userNames") String[] userNames);


    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "projectRoleUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectRoleUserDao.findListByProjectRoleId"))
    })
    @Select("select * from PROJECT_ROLE where is_delete = 0 AND PROJECT_ID = #{projectRole.projectId}")
    List<ProjectRole> findList(@Param("projectRole") ProjectRole projectRole);


    /**
     * 根绝projectId查询
     */
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "projectRoleUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectRoleUserDao.findListByProjectRoleId"))
    })
    @Select("SELECT * FROM PROJECT_ROLE WHERE is_delete = 0 AND PROJECT_ID = #{projectId}")
    List<ProjectRole> findByProjectId(@Param("projectId") String projectId);

    @Delete("DELETE FROM PROJECT_ROLE where CREATE_USER_ID = #{userId} AND CREATE_AT >= TO_DATE(#{beginTime},'yyyy-mm-dd hh24:mi:ss') AND CREATE_AT <= TO_DATE(#{endTime},'yyyy-mm-dd hh24:mi:ss')")
    void deleteByBatch(@Param("userId") String userId,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

}
