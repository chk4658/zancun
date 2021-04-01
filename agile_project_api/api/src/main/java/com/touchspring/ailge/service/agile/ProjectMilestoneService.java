package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dto.EnableOrForbiddenDTO;
import com.touchspring.ailge.dto.ProjectMilestoneDTO;
import com.touchspring.ailge.dto.ProjectRedisDTO;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.agile.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 里程碑信息表
 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectMilestoneService extends IService<ProjectMilestone> {

    List<ProjectMilestone> findByProjectId(String projectId, String searchName);

    List<ProjectMilestone> findByProjectIdAndStatusName(String projectId, String statusName);

    Task getParentTask(String parentId, List<Task> tasks);

    ProjectRedisDTO findAllInRedisByProjectId(String projectId, String searchName);

    ResultData save(ProjectMilestoneDTO projectMilestoneDTO, String userId, boolean flag, boolean batchRorC);

    void comparePastAndPresentValues(String lastRoleId, String curUserName, String createUserId, String projectId, String taskId, List<TaskLog> taskLogs, boolean flag, Integer hasOnLine, String curRoleId);

    Map<String, String> getChangeLog(String type, String lastValue, String curValue);

    boolean delete(String id, boolean flag, String userId);

    ResultData upMilestone(String id);

    ResultData downMilestone(String id);

    boolean deleteByProjectId(String projectId, String userId);

    void importByTemplate(TreeResultDTO treeResult, Project project, String userId);

    void deleteRoleId(List<String> projectRoleIds);

    boolean updateToNotForbidden(List<EnableOrForbiddenDTO.EnableOrForbiddens> enableOrForbiddenDTOList, Integer isForbidden);

    boolean updateEnabled(List<EnableOrForbiddenDTO.EnableOrForbiddens> enableOrForbiddenDTOList, Integer isEnabled);

    void updateToForbidden(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens);

    void updateToNotEnabled(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens);

    ResultData updateReferCollapsed(String id, String single, Integer collapsed);

    void copyMilestone(String milestoneId);

    void copyTask(List<Task> children, String[] excludeArr, String milestoneId, String parentTaskId, List<Task> saveToRedisList);

    String[] getCopyExcludeArr();

    String importByExistProject(TreeResultDTO treeResult, String userId, String projectId);

    void updateMilestoneAndTaskCircleIdByProjectId(String circleId, String projectId);
}

