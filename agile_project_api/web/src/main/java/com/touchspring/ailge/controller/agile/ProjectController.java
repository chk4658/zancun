package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.dto.ProjectDTO;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.sys.SysDict;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.entity.sys.SysUserRole;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.CircleService;
import com.touchspring.ailge.service.agile.ProjectMilestoneService;
import com.touchspring.ailge.service.agile.ProjectService;
import com.touchspring.ailge.service.agile.ProjectTemplateMilestoneService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 项目信息表
 * 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project")
public class ProjectController {

    private final ProjectDao projectDao;
    private final ProjectService projectService;
    private final ProjectMilestoneService projectMilestoneService;
    private final ProjectTemplateMilestoneService projectTemplateMilestoneService;
    private final ProjectRoleDao projectRoleDao;
    private final TaskDeliveryDao taskDeliveryDao;
    private final CircleDao circleDao;
    private final CircleService circleService;
    private final ProjectRoleUserDao projectRoleUserDao;
    private final SysUserRoleDao sysUserRoleDao;

    public ProjectController(ProjectDao projectDao, ProjectService projectService, ProjectMilestoneService projectMilestoneService, ProjectTemplateMilestoneService projectTemplateMilestoneService, ProjectRoleDao projectRoleDao, TaskDeliveryDao taskDeliveryDao, CircleDao circleDao, CircleService circleService, ProjectRoleUserDao projectRoleUserDao, SysUserRoleDao sysUserRoleDao) {
        this.projectDao = projectDao;
        this.projectService = projectService;
        this.projectMilestoneService = projectMilestoneService;
        this.projectTemplateMilestoneService = projectTemplateMilestoneService;
        this.projectRoleDao = projectRoleDao;
        this.taskDeliveryDao = taskDeliveryDao;
        this.circleDao = circleDao;
        this.circleService = circleService;
        this.projectRoleUserDao = projectRoleUserDao;
        this.sysUserRoleDao = sysUserRoleDao;
    }

    /**
     * 保存/更新 保存为正常项目
     */
    @PostMapping("")
    public ResultData saveOrUpdate(@RequestHeader(SystemConfig.TOKEN) String token, ProjectDTO projectDTO) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        ResultData resultData = projectService.save(projectDTO, true, user);
        return new ResultData(resultData.getCode(), resultData.getMessage()).putDataValue("project", projectDTO);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = projectService.deleteById(id, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 我的项目 ----> 当前用户参与的项目 排除草稿箱、归档项目
     */
    @GetMapping("user-project")
    public ResultData findCurUserProject(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Project> curUserProject = projectService.findCurUserProject(user.getId(), searchName, BaseEnums.NO.getCode());
        return ResultData.ok().putDataValue("curUserProject", curUserProject);
    }

    /**
     * 全部 ----> 全部项目当前用户所有可见项目 排除草稿箱、归档项目  其中，管理员可见所有项目
     */
    @GetMapping("all-project")
    public ResultData findAllVisibleProject(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Project> allVisibleProject = projectService.findAllVisibleProjectContainsAdmin(user.getId(), searchName, BaseEnums.NO.getCode());
        return ResultData.ok().putDataValue("allVisibleProject", allVisibleProject);
    }

    /**
     * 未上线 ----> NOT_ONLINE
     */
    @GetMapping("not-online")
    public ResultData findNotOnline(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<String> roleNames = new ArrayList<>();
        roleNames.add(CircleOperationType.CIRCLE_OWNER);
        roleNames.add(ProjectRelatedType.PROJECT_LEADER);
        List<Project> notOnlineProjectList = projectDao.findCurUserNotOnlineProject(null, user.getId(), BaseEnums.NO.getCode(), searchName, roleNames);
        return ResultData.ok().putDataValue("notOnlineProjectList", notOnlineProjectList);
    }


    /**
     * 获取项目 & 圈子
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id) {
//        Integer count = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).eq(ProjectRole::getProjectId, id).count();
        Project project = projectDao.findAllById(id);
        String circleId = project.getCircleId();
        Circle circle = circleDao.selectById(circleId);
        List<Circle> parentsCircles = circleService.findParentsById(circleId);
        // 反转排序 最上一层父圈下标0
        Collections.reverse(parentsCircles);
        if (!CollectionUtils.isEmpty(parentsCircles)) {
            parentsCircles.add(circle);
        }
        return ResultData.ok().putDataValue("project", project)
                .putDataValue("circle", circle)
                .putDataValue("parentsCircles", parentsCircles);
    }

    /**
     * 获取交付物个数
     */
    @GetMapping("getDeliveryCount/{id}")
    public ResultData getDeliveryCount(@PathVariable("id") String id) {
        Integer deliveryCount = new LambdaQueryChainWrapper<TaskDelivery>(taskDeliveryDao).eq(TaskDelivery::getProjectId, id).count();
        return ResultData.ok().putDataValue("deliveryCount", deliveryCount);
    }

    /**
     * 获取角色
     */
    @GetMapping("getProjectRole/{id}")
    public ResultData getProjectRole(@PathVariable("id") String id) {
        List<ProjectRole> roleDaoByProjectId = projectRoleDao.findByProjectId(id);
        return ResultData.ok().putDataValue("roleCount", roleDaoByProjectId.size()).putDataValue("rolePlus", roleDaoByProjectId);
    }


//    /**
//     * 所有项目
//     */
//    @GetMapping("all")
//    public ResultData list(){
//        return ResultData.ok().putDataValue("projectList", projectService.list());
//    }

    /**
     * 模板导入成项目
     *
     * @param token token
     * @return .
     */
    @PostMapping("importByTemplate/{id}")
    public ResultData projectImportByTemplate(@RequestHeader(SystemConfig.TOKEN) String token,
                                              @RequestBody List<TreeResultDTO> treeResults, @PathVariable("id") String projectId) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        if (!CollectionUtils.isEmpty(treeResults))
            projectMilestoneService.importByTemplate(treeResults.get(0), projectDao.selectById(projectId), user.getId());
        return ResultData.ok();
    }

    /**
     * 项目导入 需要传入新建的项目ID
     */
    @PostMapping("importByExistProject/{id}")
    public ResultData projectImportByExistProject(@RequestHeader(SystemConfig.TOKEN) String token,
                                                  @RequestBody List<TreeResultDTO> treeResults, @PathVariable("id") String projectId) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        if (!CollectionUtils.isEmpty(treeResults))
            projectId = projectMilestoneService.importByExistProject(treeResults.get(0), user.getId(), projectId);
        return ResultData.ok().putDataValue("projectId", projectId);

    }

    /**
     * 项目未上线 ----> 当前用户 为创建人、项目经理、圈长 皆可看
     */
    @GetMapping("user-notOnline-project")
    public ResultData findCurUserNotOnlineProject(@RequestHeader(SystemConfig.TOKEN) String token,
                                                  @RequestParam(defaultValue = "1", required = false) Integer page,
                                                  @RequestParam(defaultValue = "20", required = false) Integer size,
                                                  @RequestParam(required = false) String searchName) {
        Page<Project> curUserNotOnlineProject = new Page<>(page, size);
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        curUserNotOnlineProject.setRecords(projectService.findCurUserNotOnlineProject(curUserNotOnlineProject, user.getId(), searchName));
        return ResultData.ok().putDataValue("curUserNotOnlineProject", curUserNotOnlineProject.getRecords()).putDataValue("totalAmount", curUserNotOnlineProject.getTotal());
    }

    /**
     * 上线状态
     */
    @PutMapping("setOnLine")
    public ResultData findCurUserNotOnlineProject(String id, Integer hasOnLine) {
        return projectService.onlineProject(id, hasOnLine);
    }

    /**
     * 导入已有项目：获取里程碑、任务树状结构
     */
    @GetMapping("tree")
    public ResultData findTreeResultByProjectId(String projectId) {
        TreeResultDTO treeResultDTO = projectService.findTreeResultByProjectId(projectId);
        return ResultData.ok().putDataValue("treeResult", treeResultDTO);
    }

    /**
     * 项目人员权限
     * @param id 项目ID
     * @param token
     * @return
     */
    @GetMapping("has/{id}")
    public ResultData hasByIdAndToken(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        SysUserRole sysUserRole = sysUserRoleDao.findByUserIdAndRoleNameLimit1(user.getId(),FixedField.ADMIN_ROLE);
        if (sysUserRole != null) return  ResultData.ok().putDataValue("hasCircle",true);
        List<ProjectRoleUser> projectRoleUsers = projectRoleUserDao.findListByCircleIdAndUserId(id,user.getId());
        return ResultData.ok().putDataValue("hasCircle",!CollectionUtils.isEmpty(projectRoleUsers));
    }

    /**
     * 项目归档 任务需为完成状态
     *
     * @param id 项目id
     * @return .
     */
    @GetMapping("letProjectToStMark/{id}")
    public ResultData letProjectToStMark(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean stMark = projectService.letProjectToStMark(id, user.getId());
        if (!stMark)
            return new ResultData(ResultStatus.PROJECT_CAN_NOT_MARK.getCode(), ResultStatus.PROJECT_CAN_NOT_MARK.getMessage());
        return ResultData.ok();
    }

    /**
     * 查找归档项目
     */
//    @GetMapping("findStMarkProject")
//    public ResultData findStMarkProject(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName) {
//        SysUser user = JWTUtils.unSign(token, SysUser.class);
//        List<Project> allStMarkVisibleProject = projectService.findAllVisibleProjectContainsAdmin(user.getId(), searchName, BaseEnums.YES.getCode());
//        return ResultData.ok().putDataValue("allStMarkVisibleProject", allStMarkVisibleProject);
//    }

    @GetMapping("findStMarkProject")
    public ResultData findStMarkProject(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName,
                                        @RequestParam(defaultValue = "1", required = false) Integer page,
                                        @RequestParam(defaultValue = "20", required = false) Integer size) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        Page<Project> projectPage = projectService.findPageAllVisibleProjectContainsAdmin(user.getId(), searchName, BaseEnums.YES.getCode(), page, size);
        return ResultData.ok().putDataValue("allStMarkVisibleProject", projectPage.getRecords()).putDataValue("total", projectPage.getTotal());
    }

//    /**
//     * 项目根据名称模糊搜索
//     */
//    @GetMapping("getProjectsByName")
//    public ResultData getProjectsByName(String name) {
//        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
//        return ResultData.ok().putDataValue("projectList",
//                projectDao.selectList(queryWrapper.like(Project::getName,name).apply("rownum <= 20")));
//    }

}
