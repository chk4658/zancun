package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dto.PlatformOpenDTO;
import com.touchspring.ailge.dto.ProjectDTO;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.ailge.entity.agile.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.orm.mybatis.PageHelper;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 项目信息表
 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectService extends IService<Project> {

    ResultData save(ProjectDTO projectDTO, boolean flag, SysUser user);

    void saveProjectRoleAndUser(String projectId, String circleId, String projectUserId, String createUserId);

    boolean deleteById(String projectId, String userId);

    List<Project> findCurUserProject(String userId, String searchName, Integer stMark);

    List<Project> findAllVisibleProjectContainsAdmin(String userId, String searchName, Integer stMark);

    Page<Project> findPageAllVisibleProjectContainsAdmin(String userId, String searchName, Integer stMark, Integer page, Integer size);

    List<Project> findAllVisibleProject(String userId, String searchName, Integer stMark);

    List<Project> findByCircleIdAndName(String circleId, String searchName, String userId);

//    List<Project> platformOpenList(Integer pageIndex, Integer pageSize, String userId, String searchName);

    PlatformOpenDTO findAllPlatformOpenByProjectId(String[] projectIds);


    List<Project> findCurUserNotOnlineProject(Page<Project> page,String userId, String searchName);

    TreeResultDTO findTreeResultByProjectId(String projectId);

    ResultData onlineProject(String projectId, Integer hasOnLine);

    boolean letProjectToStMark(String id, String userId);

}
