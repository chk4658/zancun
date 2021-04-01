package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dto.ProjectChartDTO;
import com.touchspring.ailge.entity.agile.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.core.orm.mybatis.PageHelper;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 项目信息表
 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectDao extends BaseMapper<Project> {

    LinkedHashSet<Project> findCurUserProject(@Param("userId") String userId, @Param("isDraftBox") Integer isDraftBox, @Param("searchName") String searchName, @Param("stMark") Integer stMark);

    Set<Project> findDepartmentRangeProject(@Param("userId") String userId, @Param("isDraftBox") Integer isDraftBox, @Param("searchName") String searchName, @Param("stMark") Integer stMark);

    List<Project> findByProject(@Param("project") Project project);

    List<Project> findPageByProject(Page<Project> page, @Param("project") Project project);

    List<Project> findByProjectIdsAndName(@Param("projectIds") List<String> projectIds, @Param("searchName") String searchName);

    List<Project> findPageByProjectAndUserId(Page<Project> page, @Param("project") Project project, @Param("userId") String userId);


    @Results({
            @Result(column = "PROJECT_USER_ID", property = "projectUserId"),
            @Result(column = "PROJECT_USER_ID", property = "projectUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById")),
    })
    @Select("select * from PROJECT where is_delete = 0 and id = #{id}")
    Project findAllById(@Param("id") String id);

    int updateEstEndTimeAndStatus(@Param("id") String id, @Param("estEndTime") LocalDateTime estEndTime, @Param("status") String status);


    List<Project> findCurUserNotOnlineProject(Page<Project> page, @Param("userId") String userId,
                                              @Param("isDraftBox") Integer isDraftBox,
                                              @Param("searchName") String searchName,
                                              @Param("roleNames") List<String> roleNames);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "projectMilestones",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectMilestoneDao.findAllStatusByProjectId")),
    })
    @Select("select * from PROJECT where is_delete = 0 and id = #{projectId}")
    Project findTreeByProjectId(@Param("projectId") String projectId);

    List<ProjectChartDTO> getNewProjectNumPerMonth();

    List<ProjectChartDTO> getProjectNumByStatus();

    List<ProjectChartDTO> getTopTenTaskNumInProject();

    List<ProjectChartDTO> getTopTenUserNumInProject();

    List<ProjectChartDTO> getNewTaskNumPerDayInProjectIds(@Param("projectIds") List<String> projectIds, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
