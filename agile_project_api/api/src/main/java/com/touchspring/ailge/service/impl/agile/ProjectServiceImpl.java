package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.dto.*;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.entity.sys.SysUserRole;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.orm.mybatis.PageHelper;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 项目信息表
 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectDao, Project> implements ProjectService {

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectDepartmentService projectDepartmentService;
    @Autowired
    private CircleDao circleDao;
    @Autowired
    private ProjectRoleUserService projectRoleUserService;
    @Autowired
    private ProjectDepartmentDao projectDepartmentDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private ProjectRoleService projectRoleService;
    @Autowired
    private DraftBoxDao draftBoxDao;
    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private ProjectLogDao projectLogDao;
    @Autowired
    private ProjectCollectionDao projectCollectionDao;
    @Autowired
    private ProjectMilestoneService projectMilestoneService;
    @Autowired
    private ProjectAttrDao projectAttrDao;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private BeanChangeUtilService<Project> beanChangeUtilService;
    @Autowired
    private ProjectTemplateTaskDao projectTemplateTaskDao;
    @Autowired
    private ExcelDataService excelDataService;
    @Value("${path.prefix}")
    private String pathPrefix;
    @Autowired
    private IMailService iMailService;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    /**
     * 保存/修改 项目及关联表
     * @param projectDTO 项目信息
     * @param flag true -> 保存为项目 false -> 保存为草稿箱
     * @return .
     */
    @Override
    public ResultData save(ProjectDTO projectDTO, boolean flag, SysUser user) {
        Project target;
        if (StringUtils.isBlank(projectDTO.getId())){

            //项目名称不能重复
            if (StringUtils.isNotBlank(projectDTO.getName())){
                List<Project> projects = new LambdaQueryChainWrapper<Project>(projectDao).eq(Project::getName, projectDTO.getName()).list();
                if (!CollectionUtils.isEmpty(projects)) return new ResultData(ResultStatus.PROJECT_IS_EXIST.getCode(), ResultStatus.PROJECT_IS_EXIST.getMessage());
            }
            if (projectDTO.getVisibleUserId() == null) return ResultData.notFound();

            target = projectDTO;
            target.setCreateUserId(user.getId());
            // 未上线
            target.setHasOnLine(0);
            if (flag) target.setIsDraftBox(BaseEnums.NO.getCode());
            else target.setIsDraftBox(BaseEnums.YES.getCode());

            //圈子不是空
            if (StringUtils.isNotBlank(target.getCircleId()))
                target.setCircleBelongs(this.getCircleBelongs(target.getCircleId()));

            target.setStMark(BaseEnums.NO.getCode());
            projectDao.insert(target);

            //path有值，则调用Excel导入
            if (StringUtils.isNotBlank(projectDTO.getFilePath())) excelDataService.analysisExcel(user, projectDTO.getFilePath(), target.getId());

            //保存为草稿箱，需保存草稿箱关联表
            if (!flag && StringUtils.isNotBlank(user.getId())) {
                DraftBox draftBox = new DraftBox();
                draftBox.setName(projectDTO.getName());
                draftBox.setProjectId(target.getId());
                draftBox.setSysUserId(user.getId());
                draftBoxDao.insert(draftBox);
            }

            //可见范围：部门
            if (projectDTO.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE) && !CollectionUtils.isEmpty(projectDTO.getDepartmentIds())) {
                projectDepartmentService.save(target.getId(), projectDTO.getDepartmentIds());
            }
            //存入默认项目与角色关系：圈长及对应人员信息、项目经理及对应人员信息
            this.saveProjectRoleAndUser(target.getId(), target.getCircleId(), target.getProjectUserId(), user.getId());
        } else {
            target = projectDao.selectById(projectDTO.getId());
            projectDTO.setUpdateUserId(user.getId());

            if (StringUtils.equals(projectDTO.getName(), "")) return new ResultData(ResultStatus.PROJECT_NAME_IS_NOT_EMPTY.getCode(), ResultStatus.PROJECT_NAME_IS_NOT_EMPTY.getMessage());
            //项目名称不能重复
            if (StringUtils.isNotBlank(projectDTO.getName()) && !projectDTO.getName().equals(target.getName())){
                List<Project> projects = new LambdaQueryChainWrapper<Project>(projectDao).eq(Project::getName, projectDTO.getName()).list();
                if (!CollectionUtils.isEmpty(projects)) return new ResultData(ResultStatus.PROJECT_NAME_IS_EXIST.getCode(), ResultStatus.PROJECT_NAME_IS_EXIST.getMessage());
            }

            //由草稿箱保存为项目
            if (target.getIsDraftBox().equals(BaseEnums.YES.getCode()) && flag) {
                projectDTO.setIsDraftBox(BaseEnums.NO.getCode());
                projectDao.updateById(projectDTO);
                //需删除草稿箱
                draftBoxDao.delete(new LambdaQueryWrapper<DraftBox>().eq(DraftBox::getProjectId, target.getId()).eq(DraftBox::getSysUserId, user.getId()));
            }
            //草稿箱更新为草稿箱 项目更新 则不需要更改
            else {
                projectDTO.setIsDraftBox(target.getIsDraftBox());
                projectDao.updateById(projectDTO);
                //更新草稿箱
                if (!flag) {
                    DraftBox lastBox = new LambdaQueryChainWrapper<DraftBox>(draftBoxDao).eq(DraftBox::getProjectId, target.getId()).eq(DraftBox::getSysUserId, user.getId()).one();
                    if (!StringUtils.equals(lastBox.getName(), projectDTO.getName())) {
                        lastBox.setName(projectDTO.getName());
                        draftBoxDao.updateById(lastBox);
                    }
                }
            }

            //隶属圈子变更
            if (!StringUtils.equals(target.getCircleId(), projectDTO.getCircleId()) && StringUtils.isNotBlank(projectDTO.getCircleId())){
                Circle circle = new LambdaQueryChainWrapper<Circle>(circleDao).eq(BaseEntity::getId, projectDTO.getCircleId()).select(Circle::getOwnerUid).one();
                //圈长及对应人员信息
                projectRoleUserService.saveOrUpdateRoleUser(target.getId(), CircleOperationType.CIRCLE_OWNER, circle.getOwnerUid(), user.getId());
                //更新当前项目下所有任务、里程碑的圈子ID
                projectMilestoneService.updateMilestoneAndTaskCircleIdByProjectId(projectDTO.getCircleId(), target.getId());

                //更新项目所属的圈子目录
                LambdaUpdateWrapper<Project> projectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                projectLambdaUpdateWrapper.eq(BaseEntity::getId, target.getId());
                projectLambdaUpdateWrapper.set(Project::getCircleBelongs, this.getCircleBelongs(projectDTO.getCircleId()));
                this.update(projectLambdaUpdateWrapper);
            }
            //项目负责人变更
            if (!StringUtils.equals(target.getProjectUserId(), projectDTO.getProjectUserId()) && StringUtils.isNotBlank(projectDTO.getProjectUserId())) {
                projectRoleUserService.saveOrUpdateRoleUser(target.getId(), ProjectRelatedType.PROJECT_LEADER, projectDTO.getProjectUserId(), user.getId());
            }

            //可见范围变更 之前是2，现在不是2
            if (target.getVisibleUserId() != null && projectDTO.getVisibleUserId() != null && target.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE)
                    &&  !projectDTO.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE))
                projectDepartmentService.deleteByProjectId(target.getId());
                //之前不是2，现在是2
            else if (target.getVisibleUserId() != null && projectDTO.getVisibleUserId() != null && !target.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE)
                   && projectDTO.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE) && !CollectionUtils.isEmpty(projectDTO.getDepartmentIds()))
                projectDepartmentService.save(target.getId(), projectDTO.getDepartmentIds());
                //一直是2，判断部门是否变更
            else if(target.getVisibleUserId() != null && projectDTO.getVisibleUserId() != null && target.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE)
                    && target.getVisibleUserId().equals(projectDTO.getVisibleUserId()) && !CollectionUtils.isEmpty(projectDTO.getDepartmentIds()))
                projectDepartmentService.compareProjectDepartment(target.getId(), projectDTO.getDepartmentIds());

            //保存修改Log: 项目名字的变更、隶属圈子的变更、项目负责人的变更
            String changeLog = beanChangeUtilService.contrastObj(target, projectDTO);
            if (StringUtils.isNotBlank(changeLog)){
                ProjectLog projectLog = new ProjectLog();
                projectLog.setCreateUserId(user.getId());
                projectLog.setContent(changeLog);
                projectLog.setProjectId(target.getId());
                projectLogDao.insert(projectLog);
            }
        }
        return ResultData.ok();
    }

    /**
     * 根据圈子ID，获取名称目录
     * @param circleId 圈子ID
     * @return .
     */
    private String getCircleBelongs(String circleId){
        List<Circle> circles = new ArrayList<>();
        this.findAllParentCircle(circleId, circles);
        Collections.reverse(circles);
        List<String> circleNames = circles.stream().map(Circle::getName).collect(Collectors.toList());
        return StringUtils.join(circleNames.toArray(), ">");
    }

    /**
     * 删除项目及相关表
     * @param projectId 项目ID
     * @return .
     */
    @Override
    public boolean deleteById(String projectId, String userId) {
        //删除项目部门表
        projectDepartmentDao.delete(new LambdaQueryWrapper<ProjectDepartment>().eq(ProjectDepartment::getProjectId, projectId));

        //删除项目角色表、项目角色用户表
        List<ProjectRole> projectRoleList = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).eq(ProjectRole::getProjectId, projectId).select(BaseEntity::getId).list();
        if (!CollectionUtils.isEmpty(projectRoleList)){
            List<String> projectRoleIds = projectRoleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            projectRoleService.batchDelete(projectRoleIds.toArray(new String[projectRoleIds.size()]), userId);
        }

        //删除项目收藏
        projectCollectionDao.delete(new LambdaQueryWrapper<ProjectCollection>().eq(ProjectCollection::getProjectId, projectId));

        //删除项目里程碑
        projectMilestoneService.deleteByProjectId(projectId, userId);

        //删除项目属性
        projectAttrDao.delete(new LambdaQueryWrapper<ProjectAttr>().eq(ProjectAttr::getProjectId, projectId));

        //删除Log
        projectLogDao.delete(new LambdaQueryWrapper<ProjectLog>().eq(ProjectLog::getProjectId, projectId));

        //删除项目
        projectDao.deleteById(projectId);

        //删除关联redis
        redisTemplate.delete(RedisTableName.PROJECT.getCode() + projectId);

        return true;
    }

    /**
     * 当前用户参与的项目
     * @param userId 当前用户ID
     * @return 项目
     */
    @Override
    public List<Project> findCurUserProject(String userId, String searchName, Integer stMark) {
        return new ArrayList<>(projectDao.findCurUserProject(userId, BaseEnums.NO.getCode(), searchName, stMark));
    }

    /**
     * 当前用户所有可见项目；其中，管理员可见所有项目
     */
    @Override
    public List<Project> findAllVisibleProjectContainsAdmin(String userId, String searchName, Integer stMark) {
        //判断当前用户是否为管理员
        SysUserRole adminSysUserRole = sysUserRoleDao.findByUserIdAndRoleNameLimit1(userId, FixedField.ADMIN_ROLE);
        if (adminSysUserRole == null)
            return this.findAllVisibleProject(userId, searchName, stMark);
        else {
            //查找所有已上线不是草稿箱的项目
            Project allProject = new Project();
            allProject.setIsDraftBox(BaseEnums.NO.getCode());
            allProject.setKeyword(searchName);
            allProject.setName(searchName);
            allProject.setStMark(stMark);
            return projectDao.findByProject(allProject);
        }
    }

    /**
     * 分页
     */
    @Override
    public Page<Project> findPageAllVisibleProjectContainsAdmin(String userId, String searchName, Integer stMark, Integer page, Integer size) {
        Page<Project> projectPage = new Page<>(page, size);
        //判断当前用户是否为管理员
        SysUserRole adminSysUserRole = sysUserRoleDao.findByUserIdAndRoleNameLimit1(userId, FixedField.ADMIN_ROLE);
        Project allProject = new Project();
        allProject.setIsDraftBox(BaseEnums.NO.getCode());
        allProject.setName(searchName);
        allProject.setStMark(stMark);
        if (adminSysUserRole == null)
            return projectPage.setRecords(projectDao.findPageByProjectAndUserId(projectPage, allProject, userId));
        else
            //查找所有已上线不是草稿箱的项目
            return projectPage.setRecords(projectDao.findPageByProject(projectPage, allProject));
    }

    /**
     * 当前用户所有可见项目
     * @param userId 当前用户ID
     * @return 项目
     */
    @Override
    public List<Project> findAllVisibleProject(String userId, String searchName, Integer stMark) {
        Set<Project> allProjectList = new HashSet<>();
        //查找所有人可见的项目
        Project allProject = new Project();
        allProject.setVisibleUserId(ProjectRelatedType.ALL_THE_PEOPLE);
        allProject.setIsDraftBox(BaseEnums.NO.getCode());
        allProject.setName(searchName);
        allProject.setStMark(stMark);
        List<Project> allPeopleProjects = projectDao.findByProject(allProject);


        if (!CollectionUtils.isEmpty(allPeopleProjects))
            allProjectList.addAll(allPeopleProjects);

        //查找项目人可见的项目 当前用户是否为参与人
        List<Project> curUserProject = this.findCurUserProject(userId, searchName, stMark);
        if (!CollectionUtils.isEmpty(curUserProject)) {
            allProjectList.addAll(curUserProject);
        }

        //查找部门可见的项目 当前用户是否在选定部门下
        Set<Project> departmentRangeProject = projectDao.findDepartmentRangeProject(userId, BaseEnums.NO.getCode(), searchName, stMark);
        if (!CollectionUtils.isEmpty(departmentRangeProject))
            allProjectList.addAll(departmentRangeProject);

        //根据创建时间排序
        ArrayList<Project> projects = new ArrayList<>(allProjectList);
        projects.sort(Comparator.comparing(BaseEntity::getCreateAt));

        return projects;
    }

    /**
     * 圈子下的项目 当前用户是否可见
     * @param circleId 圈子ID
     * @param searchName 搜索的名称
     * @return 项目
     */
    @Override
    public List<Project> findByCircleIdAndName(String circleId, String searchName, String userId) {
        //当前圈子下的所有项目
        Project circleProject = new Project();
        circleProject.setCircleId(circleId);
        circleProject.setIsDraftBox(BaseEnums.NO.getCode());
        circleProject.setName(searchName);
        List<Project> projectList = projectDao.findByProject(circleProject);
        if (CollectionUtils.isEmpty(projectList)) return new ArrayList<Project>();

        //过滤出当前用户可见的项目 可见范围  1：项目参与人； 0：所有人 ；2：选定部门
        List<Project> projects = new ArrayList<>();

        //当前用户为圈长
        Circle circle = circleDao.selectById(circleId);
        if (circle != null && StringUtils.equals(circle.getOwnerUid(), userId)){
            projects.addAll(projectList);
        } else {
            //当前用户为项目经理，则可以查看
            List<Project> projectUserList = projectList.stream().filter(project -> project.getProjectUserId().equals(userId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(projectUserList)) projects.addAll(projectUserList);

            //0：所有人
            List<Project> allPeopleProjects = projectList.stream().filter(project -> project.getVisibleUserId().equals(ProjectRelatedType.ALL_THE_PEOPLE)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(allPeopleProjects)) projects.addAll(allPeopleProjects);
            //1：项目参与人
            List<Project> userProjects = projectList.stream().filter(project -> project.getVisibleUserId().equals(ProjectRelatedType.PROJECT_PARTICIPANT)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(userProjects)){
                for (Project project: userProjects) {
                    List<ProjectRole> projectRoles = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).eq(ProjectRole::getProjectId, project.getId()).select(BaseEntity::getId).list();
                    if (CollectionUtils.isEmpty(projectRoles)) continue;
                    List<ProjectRoleUser> userList = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao)
                            .in(ProjectRoleUser::getProjectRoleId, projectRoles.stream().map(BaseEntity::getId).collect(Collectors.toList()))
                            .select(ProjectRoleUser::getSysUserId).list();
                    if (CollectionUtils.isEmpty(userList)) continue;
                    Set<String> userIds = userList.stream().map(ProjectRoleUser::getSysUserId).collect(Collectors.toSet());
                    if (userIds.contains(userId)) projects.add(project);
                }
            }
            //2：选定部门
            List<Project> departmentRangeProject = projectList.stream().filter(project -> project.getVisibleUserId().equals(ProjectRelatedType.DEPARTMENTAL_VISIBILITY_RANGE)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(departmentRangeProject)) {
                //获取当前用户所在的部门
                String curDepartmentId = sysUserDao.selectById(userId).getDepartmentId();
                for (Project project: departmentRangeProject){
                    List<ProjectDepartment> projectDepartments = new LambdaQueryChainWrapper<ProjectDepartment>(projectDepartmentDao)
                            .eq(ProjectDepartment::getProjectId, project.getId()).select(ProjectDepartment::getSysDepartmentId).list();
                    if (CollectionUtils.isEmpty(projectDepartments)) continue;
                    Set<String> departmentIds = projectDepartments.stream().map(ProjectDepartment::getSysDepartmentId).collect(Collectors.toSet());
                    if (departmentIds.contains(curDepartmentId)) projects.add(project);
                }
            }
        }

        ArrayList<Project> projectArrayList = new ArrayList<>(new HashSet<>(projects));
        projectArrayList.sort(Comparator.comparing(BaseEntity::getCreateAt));
        return projectArrayList;
    }

    /**
     * 保存初始化角色
     * @param projectId 项目ID
     * @param circleId 圈子ID
     * @param projectUserId 项目负责人ID
     */
    public void saveProjectRoleAndUser(String projectId, String circleId, String projectUserId, String createUserId) {
        if (StringUtils.isNotBlank(circleId)) {
            Circle circle = new LambdaQueryChainWrapper<Circle>(circleDao).eq(BaseEntity::getId, circleId).select(Circle::getOwnerUid).one();
            //圈长及对应人员信息
            projectRoleUserService.saveOrUpdateRoleUser(projectId, CircleOperationType.CIRCLE_OWNER, circle.getOwnerUid(), createUserId);
        }
        if (StringUtils.isNotBlank(projectUserId))
            //项目经理及对应人员信息
            projectRoleUserService.saveOrUpdateRoleUser(projectId, ProjectRelatedType.PROJECT_LEADER, projectUserId, createUserId);
    }


    /**
     * 平台开阀：当前用户所有可见项目 包含项目所属父圈 分页
     */
/*    @Override
    public List<Project> platformOpenList(Integer pageIndex, Integer pageSize, String userId, String searchName){
        List<Project> projectList = this.findAllVisibleProject(userId, searchName);
        //分页
//        List<Project> projectPageList = this.pageFromList(projectList, pageIndex, pageSize);
        //查找项目所属父圈
        projectList = projectList.stream().peek(project -> {
            if (StringUtils.isNotBlank(project.getCircleId())){
                List<Circle> circles = new ArrayList<>();
                this.findAllParentCircle(project.getCircleId(), circles);
                Collections.reverse(circles);
                project.setCircles(circles);
            }
        }).collect(Collectors.toList());

        return projectList;
    }*/

    /**
     * 根据项目ID查找所有平台开阀的任务及对应里程碑
     * @param projectIds 项目ID
     * @return .
     */
    @Override
    public PlatformOpenDTO findAllPlatformOpenByProjectId(String[] projectIds) {
        List<String> projectIdList = Arrays.asList(projectIds);
        List<Task> allTaskList = new LambdaQueryChainWrapper<Task>(taskDao).in(Task::getProjectId, projectIdList).like(Task::getType, TaskTypeEnum.OPEN_TASK.getCode()).orderByAsc(Task::getSort).list();
        Map<String, List<Task>> projectMilestoneIdMap = allTaskList.stream().collect(Collectors.groupingBy(Task::getMilestoneId));
        List<ProjectMilestone> projectMilestoneList = new ArrayList<>();
        projectMilestoneIdMap.forEach((projectMilestoneId, taskList) -> {
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(projectMilestoneId);
            if (projectMilestone != null){
                projectMilestone.setTaskList(taskList);
                projectMilestoneList.add(projectMilestone);
            }
        });
        projectMilestoneList.sort(Comparator.comparing(ProjectMilestone::getSort));
        Map<String, List<ProjectMilestone>> projectIdProjectMilestoneMap = projectMilestoneList.stream().collect(Collectors.groupingBy(ProjectMilestone::getProjectId));
        List<Project> projectList = new ArrayList<>();
        projectIdProjectMilestoneMap.forEach((projectId, projectMilestones) -> {
            Project project = projectDao.selectById(projectId);
            if (project != null){
                project.setProjectMilestones(projectMilestones);
                project.setCircle(circleDao.selectById(project.getCircleId()));
                projectList.add(project);
            }
        });

        projectList.sort(Comparator.comparing(BaseEntity::getCreateAt));

        PlatformOpenDTO platformOpenDTO = new PlatformOpenDTO();
        //项目中平台开阀的任务及对应里程碑
        platformOpenDTO.setProjects(projectList);

        //查找模板中未导入的质量阀任务
        Map<String, List<Task>> fromTemplateTaskMap = allTaskList.stream().filter(task -> StringUtils.isNotBlank(task.getProjectTemplateId())).collect(Collectors.groupingBy(Task::getProjectTemplateId));
        Set<ProjectTemplateTask> projectTemplateTasks = new HashSet<>();
        fromTemplateTaskMap.forEach((projectTemplateId, tasks) -> {
            List<String> projectTemplateTaskIds = tasks.stream().filter(task -> StringUtils.isNotBlank(task.getProjectTemplateTaskId())).map(Task::getProjectTemplateTaskId).distinct().collect(Collectors.toList());
            List<ProjectTemplateTask> templateTasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateId, projectTemplateId).like(ProjectTemplateTask::getType, TaskTypeEnum.OPEN_TASK.getCode()).list();
            if (!CollectionUtils.isEmpty(templateTasks))
                projectTemplateTasks.addAll(templateTasks.stream().filter(templateTask -> !projectTemplateTaskIds.contains(templateTask.getId())).collect(Collectors.toList()));
        });
        platformOpenDTO.setProjectTemplateTasks(new ArrayList<>(projectTemplateTasks));

        return platformOpenDTO;
    }

    @Override
    public List<Project> findCurUserNotOnlineProject(Page<Project> page, String userId, String searchName) {
        List<String> roleNames = new ArrayList<>();
        roleNames.add(CircleOperationType.CIRCLE_OWNER);
        roleNames.add(ProjectRelatedType.PROJECT_LEADER);
        return projectDao.findCurUserNotOnlineProject(page, userId, BaseEnums.NO.getCode(), searchName, roleNames);
//        projectList = projectList.stream().peek(project -> {
//            if (StringUtils.isNotBlank(project.getCircleId())){
////                List<Circle> circles = new ArrayList<>();
////                this.findAllParentCircle(project.getCircleId(), circles);
////                Collections.reverse(circles);
////                project.setCircles(circles);
//                project.setProjectUser(sysUserDao.findByUserId(project.getProjectUserId()));
//            }
//        }).collect(Collectors.toList());
    }

    /**
     * 导入已有项目：获取里程碑、任务树状结构
     * @param projectId
     * @return
     */
    @Override
    public TreeResultDTO findTreeResultByProjectId(String projectId) {
        Project curProject = projectDao.findTreeByProjectId(projectId);
        TreeResultDTO templatesTreeResult = new TreeResultDTO();
        templatesTreeResult.setId(curProject.getId());
        templatesTreeResult.setName(curProject.getName());
        templatesTreeResult.setType(TreeResultDTO.PROJECT);
        templatesTreeResult.setStatus(TreeResultStatus.ENABLE.getCode());
        templatesTreeResult.setForbiddenStatus(TreeResultStatus.NOT_FORBIDDEN.getCode());
        templatesTreeResult.setChildren(curProject.getProjectMilestones().stream().map(m -> {
            TreeResultDTO milestoneTreeResult = new TreeResultDTO();
            milestoneTreeResult.setId(m.getId());
            milestoneTreeResult.setName(m.getName());
            milestoneTreeResult.setType(TreeResultDTO.PROJECT_MILESTONE);
            milestoneTreeResult.setStatus(m.getEnabled() == 1? TreeResultStatus.ENABLE.getCode(): TreeResultStatus.NOT_ENABLE.getCode());
            milestoneTreeResult.setForbiddenStatus(m.getForbidden() == 1? TreeResultStatus.FORBIDDEN.getCode(): TreeResultStatus.NOT_FORBIDDEN.getCode());
            milestoneTreeResult.setChildren(m.getTaskList().stream().map(t -> {
                TreeResultDTO TaskTreeResult = new TreeResultDTO();
                TaskTreeResult.setId(t.getId());
                TaskTreeResult.setName(t.getName());
                TaskTreeResult.setType(TreeResultDTO.PROJECT_TASK);
                TaskTreeResult.setStatus(t.getEnabled() == 1? TreeResultStatus.ENABLE.getCode(): TreeResultStatus.NOT_ENABLE.getCode());
                TaskTreeResult.setForbiddenStatus(t.getForbidden() == 1? TreeResultStatus.FORBIDDEN.getCode(): TreeResultStatus.NOT_FORBIDDEN.getCode());
                TaskTreeResult.setChildren(t.getChildren().stream().map(child -> {
                    TreeResultDTO childrenTreeResult = new TreeResultDTO();
                    childrenTreeResult.setId(child.getId());
                    childrenTreeResult.setName(child.getName());
                    childrenTreeResult.setType(TreeResultDTO.PROJECT_TASK_CHILD);
                    childrenTreeResult.setStatus(child.getEnabled() == 1? TreeResultStatus.ENABLE.getCode(): TreeResultStatus.NOT_ENABLE.getCode());
                    childrenTreeResult.setForbiddenStatus(child.getForbidden() == 1? TreeResultStatus.FORBIDDEN.getCode(): TreeResultStatus.NOT_FORBIDDEN.getCode());
                    childrenTreeResult.setChildren(new ArrayList<TreeResultDTO>());
                    return childrenTreeResult;
                }).collect(Collectors.toList()) );
                return TaskTreeResult;
            }).collect(Collectors.toList()));
            return milestoneTreeResult;
        }).collect(Collectors.toList()));
        return templatesTreeResult;
    }

    /**
     * 上线项目
     * @param projectId 项目ID
     * @param hasOnLine 上线、禁用、暂停
     * @return .
     */
    @Override
    public ResultData onlineProject(String projectId, Integer hasOnLine) {
        Project project = projectDao.selectById(projectId);
        if (project == null) return ResultData.errorRequest();
        // 需要截止时间，责任角色，审核角色才允许上线
        List<Task> taskList = new ArrayList<>();
        if (hasOnLine == 1) {
            taskList = new LambdaQueryChainWrapper<Task>(taskDao)
                    .eq(Task::getProjectId, projectId)
                    .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .eq(Task::getStMark, BaseEnums.NO.getCode()).list();
            if (!CollectionUtils.isEmpty(taskList)) {
                List<Task> estEndTimeIsNullList = taskList.stream().filter(task -> task.getEstEndTime() == null).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(estEndTimeIsNullList))
                    return new ResultData(ResultStatus.EST_END_TIME_IS_NULL.getCode(), ResultStatus.EST_END_TIME_IS_NULL.getMessage());
                List<Task> roleIsNullList = taskList.stream().filter(task -> StringUtils.isBlank(task.getReviewProjectRoleId()) || StringUtils.isBlank(task.getConfirmProjectRoleId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(roleIsNullList))
                    return new ResultData(ResultStatus.ROLE_IS_NULL.getCode(), ResultStatus.ROLE_IS_NULL.getMessage());
            }
            List<ProjectMilestone> enableProjectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao)
                    .eq(ProjectMilestone::getProjectId, projectId)
                    .eq(ProjectMilestone::getEnabled, BaseEnums.YES.getCode()).list();
            if (CollectionUtils.isEmpty(taskList) && CollectionUtils.isEmpty(enableProjectMilestones))
                return new ResultData(ResultStatus.BLANK_PAGES.getCode(), ResultStatus.BLANK_PAGES.getMessage());
        }
        project.setHasOnLine(hasOnLine);
        projectDao.updateById(project);

        // 点击项目上线，邮件提醒 项目下负责或审核项目的相关人员
        this.sendOnLineEmail(hasOnLine, taskList, project);

        return ResultData.ok();
    }

    /**
     * 项目归档 任务需为完成状态
     *
     * @param id 项目id
     * @return .
     */
    @Override
    public boolean letProjectToStMark(String id, String userId) {
        Integer notCompletedTaskCount = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getProjectId, id).ne(Task::getStMark, BaseEnums.YES.getCode())
                .eq(Task::getEnabled, BaseEnums.YES.getCode()).eq(Task::getForbidden, BaseEnums.NO.getCode())
                .ne(Task::getStatus, TaskStatusEnum.COMPLETED.getCode()).count();
        if (notCompletedTaskCount > 0) return false;
        //归档
        LambdaUpdateWrapper<Project> projectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        projectLambdaUpdateWrapper.eq(BaseEntity::getId, id);
        projectLambdaUpdateWrapper.set(Project::getStMark, BaseEnums.YES.getCode());
        projectLambdaUpdateWrapper.set(Project::getUpdateUserId, userId);
        projectLambdaUpdateWrapper.set(BaseEntity::getUpdateAt, LocalDateTime.now());
        this.update(projectLambdaUpdateWrapper);
        return true;
    }

    /**
     * 点击项目上线，邮件提醒 项目下负责或审核项目的相关人员
     * @param hasOnLine 上线标识
     * @param taskList 任务列表
     */
    private void sendOnLineEmail(Integer hasOnLine, List<Task> taskList, Project project) {
        if (BaseEnums.YES.getCode().equals(hasOnLine) && !CollectionUtils.isEmpty(taskList)) {

            Map<SysUser, Set<Task>> hashMap = new HashMap<>();

            //相关的任务
            List<Task> existsUserInIds = taskDao.findExistsUserInTaskIds(taskList.stream().map(BaseEntity::getId).collect(Collectors.toList()));
            existsUserInIds = existsUserInIds.stream().peek(task -> task.setFlag(true)).collect(Collectors.toList());

            Map<SysUser, List<Task>> reviewListMap = existsUserInIds.stream().filter(task -> task.getReviewUser() != null).collect(Collectors.groupingBy(Task::getReviewUser));
            this.getUserAndTasksMap(reviewListMap, hashMap);
            Map<SysUser, List<Task>> confirmListMap = existsUserInIds.stream().filter(task -> task.getConfirmUser() != null).collect(Collectors.groupingBy(Task::getConfirmUser));
            this.getUserAndTasksMap(confirmListMap, hashMap);

            // 项目角色下的人员，查看是否有负责的任务
            if (!CollectionUtils.isEmpty(hashMap)) {
                hashMap.forEach((sysUser, taskSet) -> {
                    if (sysUser != null && StringUtils.isNotBlank(sysUser.getEmail())) {
                        List<AttachmentDTO> attachmentDTOList = new ArrayList<>();
                        ArrayList<Task> tasks = new ArrayList<>(taskSet);
                        tasks.sort(Comparator.comparing(Task::getSort));

                        List<Task> taskTreeList = this.treeByExistTasks(tasks);
                        List<ProjectMilestone> results = new ArrayList<>();
                        Map<String, List<Task>> milestoneTaskMap = taskTreeList.stream().collect(Collectors.groupingBy(Task::getMilestoneId));
                        for (Map.Entry<String, List<Task>> map : milestoneTaskMap.entrySet()) {
                            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(map.getKey());
                            projectMilestone.setTaskList(map.getValue());
                            results.add(projectMilestone);
                        }
                        results.sort(Comparator.comparing(ProjectMilestone::getSort));

                        results.forEach(projectMilestone -> {
                            List<Task> parentTasks = projectMilestone.getTaskList();
                            parentTasks.forEach(parentTask -> {
                                //父任务是否存在 该标识为了在生成的表中，即使父任务不在当前用户管理中，表格顺序与页面相同
                                if (parentTask.getFlag())
                                    this.attachmentDTO(projectMilestone.getName(), parentTask, attachmentDTOList, parentTask.getParentId() == null ? null : taskDao.selectById(parentTask.getParentId()).getName());
                                if (!CollectionUtils.isEmpty(parentTask.getChildren()))
                                    parentTask.getChildren().forEach(childTask -> this.attachmentDTO(projectMilestone.getName(), childTask, attachmentDTOList, parentTask.getName()));
                            });
                        });
                        try {
                            iMailService.sendAttendedFileMail(sysUser.getEmail(), sysUser.getRealName(), attachmentDTOList, pathPrefix + MessageModuleEnum.PROJECT_WILL_DELAY.getType().toLowerCase() + "?id=" + project.getId(), project.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private List<Task> treeByExistTasks(ArrayList<Task> tasks) {
        List<Task> taskTreeList = new ArrayList<>();
        //获取父任务
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            if (com.touchspring.core.utils.StringUtils.isBlank(task.getParentId())) {
                taskTreeList.add(task);
                it.remove();
            }
        }
        //remainTasks 存放无父任务的子任务
        ArrayList<Task> remainTasks = new ArrayList<>(tasks);
        if (!CollectionUtils.isEmpty(tasks)) {
            for (Task task : tasks) {
                for (Task c : taskTreeList) {
                    if (StringUtils.equals(c.getId(), task.getParentId())) {
                        if (c.getChildren() == null)
                            c.setChildren(new ArrayList<>());
                        c.getChildren().add(task);
                        remainTasks.remove(task);
                        break;
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(remainTasks)) {
            //根据父任务分组
            Map<String, List<Task>> sameParentIdTasks = remainTasks.stream().collect(Collectors.groupingBy(Task::getParentId));
            sameParentIdTasks.forEach((k, v) -> {
                Task parentTask = projectMilestoneService.getParentTask(k, v);
                parentTask.setFlag(false);
                taskTreeList.add(parentTask);
            });
        }

        taskTreeList.sort(Comparator.comparing(Task::getSort));
        return taskTreeList;
    }

    private void attachmentDTO(String milestoneName, Task task, List<AttachmentDTO> attachmentDTOList, String parentName) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setMilestoneName(milestoneName);
        attachmentDTO.setTaskName(task.getName());
        attachmentDTO.setReviewUser(task.getReviewUser() == null ? null : task.getReviewUser().getRealName());
        attachmentDTO.setConfirmUser(task.getConfirmUser() == null ? null : task.getConfirmUser().getRealName());
        attachmentDTO.setEstEndTime(task.getEstEndTime());
        attachmentDTO.setParentTaskName(parentName);
        attachmentDTOList.add(attachmentDTO);
    }

    /**
     * 获取用户及其相关任务
     */
    private void getUserAndTasksMap(Map<SysUser, List<Task>> listMap, Map<SysUser, Set<Task>> hashMap) {
        listMap.forEach((sysUser, tasks) -> {
            if (CollectionUtils.isEmpty(hashMap) || CollectionUtils.isEmpty(hashMap.get(sysUser)))
                hashMap.put(sysUser, new HashSet<>(tasks));
            else {
                Set<Task> lastTasks = hashMap.get(sysUser);
                lastTasks.addAll(tasks);
                hashMap.put(sysUser, lastTasks);
            }
        });
    }

    /**
     * 查找所有父圈
     * @param circleId 圈子ID
     * @param circles 圈子集合
     */
    private void findAllParentCircle(String circleId, List<Circle> circles){
        Circle circle = circleDao.selectById(circleId);
        if (circle == null) return;
        circles.add(circle);
        if (StringUtils.isNotBlank(circle.getParentId()))
            this.findAllParentCircle(circle.getParentId(), circles);
    }

    /**
     * 对已有List分页
     * @param projectList 项目List
     * @param pageIndex 下标
     * @param pageSize 页面大小
     * @return 新的分页List
     */
    private List<Project> pageFromList(List<Project> projectList, Integer pageIndex, Integer pageSize){
        PagingDTO pagingDTO = PagingDTO.pagination(projectList.size(), pageSize, pageIndex);
        Integer fromIndex = pagingDTO.getQueryIndex();
        int toIndex = 0;
        if (fromIndex + pagingDTO.getPageSize() >= projectList.size()){
            toIndex = projectList.size();
        }else {
            toIndex = fromIndex + pagingDTO.getPageSize();
        }
        if (fromIndex > toIndex){
            return new ArrayList<Project>();
        }
        return projectList.subList(fromIndex,toIndex);
    }
}
