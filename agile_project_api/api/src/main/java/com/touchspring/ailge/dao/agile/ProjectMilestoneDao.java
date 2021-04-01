package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 里程碑信息表
 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectMilestoneDao extends BaseMapper<ProjectMilestone> {

    List<ProjectMilestone> findAllByProjectId(@Param("projectId") String projectId, @Param("searchName") String searchName);

    ProjectMilestone findById(@Param("id") String id);

    String getMaxStatusByProjectIdAndEnable(@Param("projectId") String projectId, @Param("enabled") Integer enabled);

    LocalDateTime getMaxEstEndTimeByProjectIdAndEnable(@Param("projectId") String projectId, @Param("enabled") Integer enabled);

    //包含Task
    ProjectMilestone findContainsTaskById(@Param("id") String id);

    int updateByRoleId(@Param("id") String id, @Param("reviewProjectRoleId") String reviewProjectRoleId, @Param("confirmProjectRoleId") String confirmProjectRoleId);

    int updateEstEndTimeAndStatus(@Param("id") String id, @Param("estEndTime") LocalDateTime estEndTime, @Param("status") String status);

//    Integer getMaxSort();

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDao.findTreeParentEnableListByProjectMilestoneId")),
    })
    @Select("select * from PROJECT_MILESTONE where is_delete = 0 and PROJECT_ID = #{projectId} and ENABLED = 1 order by sort")
    List<ProjectMilestone> findEnableByProjectId(@Param("projectId") String projectId);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDao.findTreeParentAllStatusListByProjectMilestoneId")),
    })
    @Select("select * from PROJECT_MILESTONE where is_delete = 0 and PROJECT_ID = #{projectId} order by sort")
    List<ProjectMilestone> findAllStatusByProjectId(@Param("projectId") String projectId);

    @Delete("DELETE FROM PROJECT_MILESTONE WHERE CREATE_USER_ID = #{userId} AND CREATE_AT >= TO_DATE(#{beginTime},'yyyy-mm-dd hh24:mi:ss') AND CREATE_AT <= TO_DATE(#{endTime},'yyyy-mm-dd hh24:mi:ss')")
    void deleteByBatch(@Param("userId") String userId,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    @Select("SELECT * FROM PROJECT_MILESTONE WHERE CREATE_USER_ID = #{userId} AND CREATE_AT >= TO_DATE(#{beginTime},'yyyy-mm-dd hh24:mi:ss') AND CREATE_AT <= TO_DATE(#{endTime},'yyyy-mm-dd hh24:mi:ss')")
    List<ProjectMilestone> findByUserIdAndTime(@Param("userId") String userId,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    List<ProjectMilestone> findAll();

}
