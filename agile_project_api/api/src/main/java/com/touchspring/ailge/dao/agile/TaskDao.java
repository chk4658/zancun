package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 项目任务信息表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-19
 */
public interface TaskDao extends BaseMapper<Task> {

    @Select("select * from TASK where MILESTONE_ID = #{milestoneId} and IS_DELETE = 0")
    List<Task> findByMilestoneId(@Param("milestoneId") String milestoneId);

    String getMaxDateStatusByMilestoneIdAndEnable(@Param("milestoneId") String milestoneId, @Param("enabled") Integer enabled);

    LocalDateTime getMaxEstEndTimeByMilestoneIdAndEnable(@Param("milestoneId") String milestoneId, @Param("enabled") Integer enabled);

    LocalDateTime getMaxEstEndTimeByParentTaskIdAndEnable(@Param("parentId") String parentId, @Param("enabled") Integer enabled);

    List<Task> findParentListByProjectMilestoneId(@Param("projectMilestoneId") String projectMilestoneId);

    List<Task> findChildListByParentId(@Param("parentId") String parentId);

    List<Task> findContainsChildListById(@Param("id") String id);

    Task findAllById(@Param("id") String id);

    List<Task> findEnableParentListByProjectMilestoneId(@Param("projectMilestoneId") String projectMilestoneId);

    List<Task> findEnableChildListByParentId(@Param("parentId") String parentId);

    int updateByRoleId(@Param("taskId") String taskId, @Param("reviewProjectRoleId") String reviewProjectRoleId, @Param("confirmProjectRoleId") String confirmProjectRoleId);

    @Select("SELECT * FROM task WHERE MILESTONE_ID = #{milestoneId} AND sort > #{sort} ORDER BY sort ASC")
    List<Task> findByMilestoneIdAndLtSort(@Param("milestoneId") String milestoneId, @Param("sort") String sort);


    @Select("SELECT * FROM task WHERE PARENT_ID = #{parentId} AND sort > #{sort} ORDER BY sort ASC")
    List<Task> findByParentIdAndLtSort(@Param("parentId") String parentId, @Param("sort") String sort);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDao.findTreeEnableChildListByParentId")),
    })
    @Select("select * from TASK where is_delete = 0 and MILESTONE_ID = #{projectMilestoneId} and PARENT_ID IS NULL and ENABLED = 1 and ST_MARK = 0 order by sort")
    List<Task> findTreeParentEnableListByProjectMilestoneId(@Param("projectMilestoneId") String projectMilestoneId);

//    @Results({
//            @Result(column = "id", property = "id"),
//            @Result(column = "id", property = "children",
//                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDao.findTreeEnableChildListByParentId")),
//    })
    @Select("select * from TASK where is_delete = 0 and PARENT_ID = #{parentId} and ENABLED = 1 and ST_MARK = 0 order by sort")
    List<Task> findTreeEnableChildListByParentId(@Param("parentId") String parentId);

    int insertByBatch(List<Task> list);

    @Delete("DELETE FROM TASK WHERE CREATE_USER_ID = #{userId} AND CREATE_AT >= TO_DATE(#{beginTime},'yyyy-mm-dd hh24:mi:ss') AND CREATE_AT <= TO_DATE(#{endTime},'yyyy-mm-dd hh24:mi:ss')")
    void deleteByBatch(@Param("userId") String userId,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    @Select("SELECT * FROM TASK WHERE CREATE_USER_ID = #{userId} AND CREATE_AT >= TO_DATE(#{beginTime},'yyyy-mm-dd hh24:mi:ss') AND CREATE_AT <= TO_DATE(#{endTime},'yyyy-mm-dd hh24:mi:ss')")
    List<Task> findByUserIdAndTime(@Param("userId") String userId,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    List<Task> findAll();

    List<Task> findContainsRoleBetweenDate(@Param("allVisibleProjectIds") List<String> allVisibleProjectIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Task> listByReviewSysUserId(@Param("status") String status, @Param("month") String month, @Param("sysUserId") String sysUserId);

    List<Task> listByConfirmSysUserId(@Param("status") String status, @Param("month") String month, @Param("sysUserId") String sysUserId);

//    List<Task> findListBy

    List<Task> findCurUserTemporaryTask(@Param("userId") String userId, @Param("isTemporary") Integer isTemporary);

    @Select("SELECT t.* FROM TASK t " +
            "left JOIN PROJECT_ROLE_USER pju1 on t.REVIEW_PROJECT_ROLE_ID = pju1.project_Role_id " +
            "left JOIN PROJECT_ROLE_USER pju2  on t.CONFIRM_PROJECT_ROLE_ID = pju2.project_Role_id " +
            "WHERE (pju1.sys_user_id  = #{userId} or   pju2.sys_user_id= #{userId}) " +
            "AND t.IS_DELETE = 0 AND t.FORBIDDEN = 0 AND t.ENABLED = 1 AND t.ST_MARK = 0" +
            "and TO_CHAR(t.EST_END_TIME, 'yyyy-mm-dd') >= TO_CHAR(#{beginTime}, 'yyyy-mm-dd') and TO_CHAR(t.EST_END_TIME, 'yyyy-mm-dd') <= TO_CHAR(#{endTime}, 'yyyy-mm-dd')")
    List<Task> findListUserIdAndStartTimeAtAndEndTimeAt(@Param("userId") String userId,@Param("beginTime") Date beginTime,@Param("endTime") Date endTime);

    List<Task> findListByProjectIdAndTaskName(@Param("projectId") String projectId,@Param("taskName") String taskName);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDao.findTreeAllStatusChildListByParentId")),
    })
    @Select("select * from TASK where is_delete = 0 and MILESTONE_ID = #{projectMilestoneId} and PARENT_ID IS NULL and ST_MARK = 0 order by sort")
    List<Task> findTreeParentAllStatusListByProjectMilestoneId(@Param("projectMilestoneId") String projectMilestoneId);

    @Select("select * from TASK where is_delete = 0 and PARENT_ID = #{parentId} and ST_MARK = 0 order by sort")
    List<Task> findTreeAllStatusChildListByParentId(@Param("parentId") String parentId);

    List<Task> findExistsUserInTaskIds(@Param("taskIds") List<String> taskIds);

    List<Task> findReviewOrConfirmNotCompleteTaskByUserId(Page<Task> page, @Param("userId") String userId);


    List<Task> findListByProjectId(String projectId);

}
