package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.TaskDTO;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.core.mvc.ui.ResultData;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 项目任务信息表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
public interface TaskService extends IService<Task> {

    ResultData save(TaskDTO taskDTO, String userId, String reuploadListIds);

    void roleUserChaneOfTask(boolean flag, String userId, String toUserName, String reviewProjectRoleId, String confirmProjectRoleId, String taskId, String projectId);

    void updateTaskInRedisByTaskId(String taskId);

    void batchUpdateTaskInRedis(List<Task> taskList);

    boolean delete(String id, String userId);

    boolean deleteByProjectMilestoneId(String projectMilestoneId, String userId);

    ResultData upTask(String id);

    ResultData downTask(String id);

    List<Task> getByProjectId(String projectId);

    void setMilestoneStatusByTask(String milestoneId, boolean batchFlag);

    void setProjectStatusByMilestone(String projectId);

    String getDateStatus(String taskStatus, LocalDateTime estEndTime);

    boolean batchDelete(String[] ids, boolean flag, String userId);

    void deleteRoleId(List<String> projectRoleIds);

    void updateTaskInRedisByProjectRoleId(String projectRoleId);

    void batchUpdateTaskTime(List<Task> taskList, SysUser user, boolean isUpdateMilestoneFlag);

    void copyTask(String taskId);

    void updateProjectEstEndTimeAndStatus(List<Project> projectList);

    List<Task> getByUserId(String userId, Date startDate, Date endDate);

    void taskMessage(String reviewProjectRoleId, String confirmProjectRoleId, String taskId, Boolean flag, String projectId, String type, String msg);

    boolean letTaskToStMark(String taskId);

    Page<Task> getMyTasks(String userId, Integer page, Integer size);

}
