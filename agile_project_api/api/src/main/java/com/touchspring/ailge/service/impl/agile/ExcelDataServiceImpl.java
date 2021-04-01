package com.touchspring.ailge.service.impl.agile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysDictDataDao;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.dto.ExcelData;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.ProjectRole;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.sys.SysDictData;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.LogTypeEnum;
import com.touchspring.ailge.enums.PriorityEnum;
import com.touchspring.ailge.enums.TaskStatusEnum;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.exception.ExcelFormatException;
import com.touchspring.ailge.exception.ExcelNullException;
import com.touchspring.ailge.exception.ExcelRoleException;
import com.touchspring.ailge.listener.ExcelListener;
import com.touchspring.ailge.service.agile.ExcelDataService;
import com.touchspring.ailge.service.agile.ProjectLogService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.mvc.ui.ResultData;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-27 17:22
 **/
@Service
public class ExcelDataServiceImpl extends ServiceImpl<ExcelDataDao, ExcelData> implements ExcelDataService {

    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private SysDictDataDao sysDictDataDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private ProjectMilestoneServiceImpl projectMilestoneService;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectLogService projectLogService;
    @Autowired
    private ExcelDataService excelDataService;

    @Override
    public boolean save(List<ExcelData> list,String sheetName,SysUser sysUser,String projectId) {
        List<Task> taskList = new ArrayList<>();
        ProjectMilestone projectMilestone = new ProjectMilestone();
        Task task;
        //整个角色模板
        List<SysRoleTemplate> sysRolesTemplate = sysRoleTemplateDao.selectList(null);
        //当前项目的所有角色
        List<ProjectRole> sysRoles = projectRoleDao.findByProjectId(projectId);
        List<SysDictData> types = sysDictDataDao.findDataByDictName("任务类型");
        List<SysDictData> priorities = sysDictDataDao.findDataByDictName("优先级");
        //整个角色模板名
        List<String> templateName = sysRolesTemplate.stream().map(SysRoleTemplate::getName).collect(Collectors.toList());
        List<String> names = sysRoles.stream().map(ProjectRole::getRoleName).collect(Collectors.toList());
        List<String> typeNames = types.stream().map(SysDictData::getName).collect(Collectors.toList());
        Project project = projectDao.selectById(projectId);
        //保存里程碑信息
        if(!StringUtils.isEmpty(sheetName)){
            projectMilestone.setProjectId(projectId);
            projectMilestone.setName(sheetName);
            projectMilestone.setSort(IdWorker.nextId());
            projectMilestone.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            projectMilestone.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));
            projectMilestone.setCreateUserId(sysUser.getId());
            //圈子ID
            projectMilestone.setCircleId(project == null? null: project.getCircleId());
            projectMilestoneDao.insert(projectMilestone);
            //保存redis中里程碑信息
            projectMilestoneService.updateMilestoneInRedisByMilestone(projectMilestone);
        }
        //保存task信息
        for (ExcelData str : list) {
            if (StringUtils.isEmpty(str.getName()) || StringUtils.isEmpty(str.getConfirmProjectRoleId()) || StringUtils.isEmpty(str.getReviewProjectRoleId()) || StringUtils.isEmpty(str.getEndTime())) {
                throw new ExcelNullException("文件数据必填项有空值！");
            }else {
                if(!templateName.contains(str.getConfirmProjectRoleId()) || !templateName.contains(str.getReviewProjectRoleId())){
                    throw new ExcelRoleException("负责人或审核人角色不在角色模板库中！");
                }else {
                    task = new Task();
                    //存入任务名称
                    task.setName(str.getName());
                    //存入任务优先级
                    if(str.getPriority() == null){
                        task.setPriority(PriorityEnum.LOW.getCode());
                    }else {
                        String code = priorities.stream().filter(priority -> priority.getName().equals(str.getPriority())).findFirst().get().getCode();
                        task.setPriority(code);
                    }
                    //存入负责人角色
                    if(names.contains(str.getReviewProjectRoleId())){
                        ProjectRole role1 = sysRoles.stream().filter(sysRole -> sysRole.getRoleName().equals(str.getReviewProjectRoleId())).findFirst().get();
                        task.setReviewProjectRoleId(role1.getId());
                    }else {
                        SysRoleTemplate template = sysRolesTemplate.stream().filter(role -> role.getName().equals(str.getReviewProjectRoleId())).findFirst().get();
                        //如果此角色不在模板库中不需要存储
                        if(template!=null){
                            ProjectRole role = new ProjectRole();
                            role.setRoleName(template.getName());
                            role.setRoleDescription(template.getDescription());
                            role.setProjectId(projectId);
                            role.setDuty(template.getDuty());
                            role.setCreateUserId(sysUser.getId());
                            projectRoleDao.insert(role);
                            task.setReviewProjectRoleId(role.getId());
                            //项目角色加入新增名称
                            names.add(template.getName());
                            //新增项目角色
                            sysRoles.add(role);
                        }
                    }
                    //存入审核人角色
                    if(names.contains(str.getConfirmProjectRoleId())){
                        ProjectRole role2 = sysRoles.stream().filter(sysRole -> sysRole.getRoleName().equals(str.getConfirmProjectRoleId())).findFirst().get();
                        task.setConfirmProjectRoleId(role2.getId());
                    }else {
                        SysRoleTemplate template = sysRolesTemplate.stream().filter(role -> role.getName().equals(str.getConfirmProjectRoleId())).findFirst().get();
                        //如果此角色不在模板库中不需要存储
                        if(template!=null){
                            ProjectRole role = new ProjectRole();
                            role.setRoleName(template.getName());
                            role.setRoleDescription(template.getDescription());
                            role.setProjectId(projectId);
                            role.setDuty(template.getDuty());
                            role.setCreateUserId(sysUser.getId());
                            projectRoleDao.insert(role);
                            task.setConfirmProjectRoleId(role.getId());
                            //项目角色加入新增名称
                            names.add(template.getName());
                            //新增项目角色
                            sysRoles.add(role);
                        }
                    }
                    //存入截止时间
                    try {
                        Instant instant = str.getEndTime().toInstant();
                        ZoneId zoneId = ZoneId.systemDefault();
                        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                        task.setEstEndTime(localDateTime);
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new ExcelFormatException("截止时间格式有误！");
                    }
                    //存入交付要求
                    if(str.getIsRequirement() == null){
                        task.setIsRequirement(0L);
                    }else {
                        switch (str.getIsRequirement()){
                            case "是":
                                task.setIsRequirement(1L);
                                break;
                            case "否":
                                task.setIsRequirement(0L);
                        }
                    }
                    //存入任务类型
                    String type = "";
                    if(!StringUtils.isEmpty(str.getType())){
                        String[] split = str.getType().split("，");
                        for(String s : split){
                            if(typeNames.contains(s)){
                                SysDictData sysDictData = types.stream().filter(data -> data.getName().equals(s)).findFirst().get();
                                String code = sysDictData.getCode();
                                type = type + code+",";
                            }
                        }
                        task.setType(type.substring(0,type.length()-1));
                        //判断是否有质量阀任务,有则开阀条件必填
                        if(Arrays.asList(split).contains("质量阀任务")){
                            if(Strings.isEmpty(str.getOpenConditions())){
                                return false;
                            }else {
                                task.setOpenConditions(str.getOpenConditions());
                            }
                        }
                    }
                    //存入开阀描述
                    task.setOpenDescription(str.getOpenDescription());

                    //默认存入
                    task.setProjectId(projectId);
                    task.setMilestoneId(projectMilestone.getId());
                    //圈子ID
                    task.setCircleId(project == null? null: project.getCircleId());
                    task.setSort(IdWorker.nextId());
                    task.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
                    task.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));

                    task.setCreateUserId(sysUser.getId());

                    //插入是否为临时任务
                    task.setIsTemporary(0);
                    //插入任务归档
                    task.setStMark(0);
                    task.setStatus(TaskStatusEnum.NOT_STARTED.getCode());

                    // 时间状态
                    String dateStatus = taskService.getDateStatus(TaskStatusEnum.NOT_STARTED.getCode(), task.getEstEndTime());
                    task.setDateStatus(dateStatus);

                    taskList.add(task);

//                    taskDao.insert(task);

                }
            }
        }

        //批量插入task
        taskList.forEach(x->{
            taskDao.insert(x);
            //批量插入rdis
            taskService.updateTaskInRedisByTaskId(x.getId());
        });

        // 里程碑状态
        taskService.setMilestoneStatusByTask(projectMilestone.getId(), true);


        return true;

    }

    @Override
    public void saveProjectRole(SysUser sysUser, String projectId, List<String> roleList) {
        //查询projectId相关角色
        List<ProjectRole> projectRoles = projectRoleDao.findByProjectId(projectId);
        List<String> collect = projectRoles.stream().map(ProjectRole::getRoleName).collect(Collectors.toList());
        List<String> collect1 = projectRoles.stream().map(ProjectRole::getRoleName).collect(Collectors.toList());
        //角色合并去重
        collect.addAll(roleList);
        List<String> collect2 = collect.stream().distinct().collect(Collectors.toList());
        if(collect2.size() > collect1.size()){
            //整个角色模板
            List<SysRoleTemplate> sysRolesTemplate = sysRoleTemplateDao.selectList(null);
            collect2.forEach( x-> {
                if(!collect1.contains(x)){
                    try {
                        SysRoleTemplate template = sysRolesTemplate.stream().filter(role -> role.getName().equals(x)).findFirst().get();
                        //如果此角色不在模板库中不需要存储
                        if(template!=null){
                            ProjectRole role = new ProjectRole();
                            role.setRoleName(x);
                            role.setRoleDescription(template.getDescription());
                            role.setProjectId(projectId);
                            role.setDuty(template.getDuty());
                            role.setCreateUserId(sysUser.getId());
                            projectRoleDao.insert(role);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 项目Excel导入
     */
    @Override
    public ResultData analysisExcel(SysUser user, String path, String projectId) {
        Date beginDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String realPath = new File("").getAbsolutePath();
        try {
            InputStream inputStream = new FileInputStream(realPath + File.separator + path);
//            InputStream inputStream = new FileInputStream(path);
            ExcelReader excelReader = EasyExcel.read(inputStream, ExcelData.class, new ExcelListener(excelDataService, user, projectId)).doReadAll();
            //关闭资源
            excelReader.finish();
            //保存log
            Date endDate = new Date();
            List<ProjectMilestone> projectMilestones = projectMilestoneDao.findByUserIdAndTime(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            List<String> milestoneNameList = projectMilestones.stream().filter(projectMilestone -> com.touchspring.core.utils.StringUtils.isNotBlank(projectMilestone.getName())).map(ProjectMilestone::getName).collect(Collectors.toList());
            projectLogService.batchSaveMilestoneChange(LogTypeEnum.INSERT.getMsg(), milestoneNameList, projectId, user.getId());
            // 项目状态
            taskService.setProjectStatusByMilestone(projectId);

        } catch (Exception e) {
            log.error(e.getMessage());
            Date endDate = new Date();
            //删除redis中已存储数据
            List<ProjectMilestone> list = projectMilestoneDao.findByUserIdAndTime(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            list.forEach(x->{
                projectMilestoneService.deleteFromRedisByProjectIdAndMilestoneId(projectId, x.getId());
            });
            List<Task> taskList = taskDao.findByUserIdAndTime(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            taskService.deleteTaskInRedis(taskList);

            //删除oracle数据
            //删除project milestone数据
            projectMilestoneDao.deleteByBatch(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            //删除task数据
            taskDao.deleteByBatch(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            //删除project role数据
            projectRoleDao.deleteByBatch(user.getId(), dateFormat.format(beginDate), dateFormat.format(endDate));
            return new ResultData("100", e.getMessage());
        }
        return ResultData.ok();
    }

    @Override
    public boolean check(List<ExcelData> list, String sheetName) {
        List<SysRoleTemplate> sysRolesTemplate = sysRoleTemplateDao.selectList(null);
        List<String> templateName = sysRolesTemplate.stream().map(SysRoleTemplate::getName).collect(Collectors.toList());
        for (ExcelData str : list) {
            if (StringUtils.isEmpty(str.getName()) || StringUtils.isEmpty(str.getConfirmProjectRoleId()) || StringUtils.isEmpty(str.getReviewProjectRoleId()) || StringUtils.isEmpty(str.getEndTime())) {
                throw new ExcelNullException("文件数据必填项有空值！");
            }else {
                if(!templateName.contains(str.getConfirmProjectRoleId()) || !templateName.contains(str.getReviewProjectRoleId())){
                    throw new ExcelRoleException("负责人或审核人角色不在角色模板库中！");
                }else {
                    //存入截止时间
                    try {
                        Instant instant = str.getEndTime().toInstant();
                        ZoneId zoneId = ZoneId.systemDefault();
                        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new ExcelFormatException("截止时间格式有误！");
                    }
                }
            }
        }
        return true;
    }
}
