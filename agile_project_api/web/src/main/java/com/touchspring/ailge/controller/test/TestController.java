package com.touchspring.ailge.controller.test;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.dao.agile.ProjectMilestoneDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dao.ldap.UserDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysCompany;
import com.touchspring.ailge.entity.sys.SysDepartment;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.IMailService;
import com.touchspring.ailge.service.agile.TaskService;
import com.touchspring.ailge.service.ldap.UserService;
import com.touchspring.ailge.service.sys.SysCompanyService;
import com.touchspring.ailge.service.sys.SysDepartmentService;
import com.touchspring.ailge.task.EquivalentCalculateTask;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.ailge.utils.WebSocketServer;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * <p>
 * 圈子信息表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

//    @Autowired
//    private LdapServiceImpl ldapService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BeanChangeUtilService<Project> beanChangeUtilService;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private EquivalentCalculateTask equivalentCalculateTask;
    @Autowired
    private UserService userService;
    @Autowired
    private LdapTemplate ldapTemplate;
    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private SysDepartmentService sysDepartmentService;


    @Autowired
    private IMailService mailService;
    @Value("${path.prefix}")
    private String pathPrefix;

    @GetMapping("testEquivalentCal")
    public ResultData testEquivalentCal(String month){
        equivalentCalculateTask.calculate(month);
        return ResultData.ok();
    }

    @GetMapping("1")
    public void test() throws Exception {
        mailService.sendMessageMail("571563809@qq.com", "测试邮件内容", "亲爱的wj，\n您有新任务需要处理：",
                "http://192.168.2.174:8081/#/project?id=2409a06f942b7789d80c5a216ce004e3",
                "零件状态确认，PPQP交样文件通过后截图给PPO仓库，零件临时包装状态确认，保证零件无变形，打印SN",
                "1212",
                "零件状态确认，PPQP交样文件通过后截图给PPO仓库，零件临时包装状态确认，保证零件无变形，打印SNR发运通知单、二维码，标签张贴后通知物流零件发运",
                "殷斌", "王飞飞", null, "TASK", null, null, null);
    }

    @GetMapping("2")
    public ResultData webSocket(){
        return ResultData.ok();
    }


    @GetMapping("ldap")
    public ResultData ldap(
            String ldapAccount,
            String ldapPwd) {
        log.info("ldapAccount:=======================================================》{}", ldapAccount);
        log.info("ldapPwd:=======================================================》{}", ldapPwd);
        String uid = null;
        DirContext dirContext = null;
        User user = null;
        boolean v = false;
        try {
            dirContext = ldapTemplate.getContextSource().getContext("SSDTC\\"+ldapAccount, ldapPwd);
            v = true;
            ldapTemplate.setIgnorePartialResultException (true);
            List<User> users = ldapTemplate.search(
                    query().where("objectclass")
                            .is("person").and("sAMAccountName").is(ldapAccount),
                    (AttributesMapper<User>) attrs -> {
                        User ldapUser = new User();
                        Attribute attr = attrs.get("sAMAccountName");
                        if (attr != null && attr.get() != null) {
                            ldapUser.setAccount(attr.get().toString());
                        }
                        return ldapUser;
                    });
            log.info("user======================>{}",users.size());
            if (!CollectionUtils.isEmpty(users)) user = users.get(0);
            //使用Jwt加密用户名和用户所属组信息
        } catch (org.springframework.ldap.NamingException e) {
            log.info(e.getMessage());
        } finally {
            LdapUtils.closeContext(dirContext);
        }
        return ResultData.ok().putDataValue("user",user).putDataValue("verifyLDAP",v);
    }

    @GetMapping("ldap2")
    public ResultData ldap(String sAMAccountName) {

        User user = userService.findOneBySAMAccountName(sAMAccountName);

        if (user == null) return ResultData.errorRequest();



        return ResultData.ok();
    }


    @GetMapping("caculateProjectMilestoneStatus")
    public ResultData caculateProjectMilestoneStatus(){
        List<ProjectMilestone> projectMilestones = projectMilestoneDao.selectList(null);
        projectMilestones.forEach(projectMilestone -> this.setMilestoneStatusByTask(projectMilestone.getId()));

        return ResultData.ok();
    }

    @GetMapping("caculateProjectStatus")
    public ResultData caculateProjectStatus(){

        List<Project> projects = projectDao.selectList(null);
        projects.forEach(project -> this.setProjectStatusByMilestone(project.getId()));

        return ResultData.ok();

    }



    /**
     * 生成test环境的时间状态
     */
    @GetMapping("updateTaskDateStatus")
    public ResultData updateTaskDateStatus(){

        //查询状态为未完成，且状态、截止日期不为空的任务
        List<Task> taskList = new LambdaQueryChainWrapper<Task>(taskDao).isNotNull(Task::getStatus).isNotNull(Task::getEstEndTime).list();
        if (CollectionUtils.isEmpty(taskList)) return ResultData.notFound();

        //任务状态更新
        List<Task> differentTaskList = taskList.stream().peek(task -> task.setDateStatus(this.getDateStatus(task.getStatus(), task.getEstEndTime(), task.getActEndTime()))).collect(Collectors.toList());
        taskService.updateBatchById(differentTaskList);

        // 更新Redis--->只更新启用状态
        List<Task> enableTasks = differentTaskList.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                && BaseEnums.NO.getCode().equals(task.getStMark())).collect(Collectors.toList());
        taskService.batchUpdateTaskInRedis(enableTasks);

        return ResultData.ok();
    }

    /**
     * 批量修改Redis数据 带条件通过
     */
    @GetMapping("updateTaskInRedis")
    public ResultData updateTaskInRedis( @RequestBody String taskIdStr){
        List<String> taskIds = JSON.parseArray(taskIdStr,String.class);
        if (CollectionUtils.isEmpty(taskIds)) return ResultData.notFound();
        List<Task> taskList = taskDao.selectBatchIds(taskIds);


        taskList = taskList.stream().peek(task -> {
            task.setStatus(TaskStatusEnum.CONDITIONAL_PASS.getCode());
            task.setDateStatus(DateStatusEnum.CONDITIONAL_PASS.getCode());
        }).collect(Collectors.toList());
        taskService.updateBatchById(taskList);
        // 更新Redis--->只更新启用状态
        List<Task> enableTasks = taskList.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                && BaseEnums.NO.getCode().equals(task.getStMark())).collect(Collectors.toList());
        taskService.batchUpdateTaskInRedis(enableTasks);


        // 更新里程碑状态 更新项目状态
        Set<String> milestoneIds = taskList.stream().filter(task -> org.apache.commons.lang3.StringUtils.isNotBlank(task.getMilestoneId())).map(Task::getMilestoneId).collect(Collectors.toSet());
        milestoneIds.forEach(milestoneId -> taskService.setMilestoneStatusByTask(milestoneId, true));

        // 项目状态 --> 找到所属里程碑最大的状态
        Set<String> projectIds = taskList.stream().filter(task -> org.apache.commons.lang3.StringUtils.isNotBlank(task.getProjectId())).map(Task::getProjectId).collect(Collectors.toSet());
        projectIds.forEach(projectId -> taskService.setProjectStatusByMilestone(projectId));

        return ResultData.ok();
    }

    private void setMilestoneStatusByTask(String milestoneId) {
        ProjectMilestone projectMilestone = projectMilestoneDao.selectById(milestoneId);
        if (projectMilestone == null) return;
        String maxDateStatus = taskDao.getMaxDateStatusByMilestoneIdAndEnable(milestoneId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
        if (com.touchspring.core.utils.StringUtils.equals(maxDateStatus, DateStatusEnum.DELAY_COMPLETION.getCode())
                || StringUtils.equals(maxDateStatus, DateStatusEnum.NOT_TO_THE_NODE.getCode()))
            maxDateStatus = DateStatusEnum.FINISH_ON_TIME.getCode();
        projectMilestone.setStatus(maxDateStatus);
        LocalDateTime maxTaskEstEndTime = taskDao.getMaxEstEndTimeByMilestoneIdAndEnable(milestoneId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
        projectMilestone.setEstEndTime(maxTaskEstEndTime);
        projectMilestoneDao.updateById(projectMilestone);
        if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
            redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                    RedisTableName.PROJECT_MILESTONE.getCode() + milestoneId,
                    JSON.toJSONString(projectMilestoneDao.findById(milestoneId)));
    }

    private String getDateStatus(String taskStatus, LocalDateTime estEndTime, LocalDateTime actEndTime) {

        //状态为待条件通过，则时间状态为默认
        if (StringUtils.equals(taskStatus, TaskStatusEnum.CONDITIONAL_PASS.getCode()))
            return DateStatusEnum.CONDITIONAL_PASS.getCode();
        String dateStatus = null;
        LocalDateTime now = LocalDateTime.now();
        if (actEndTime != null) now = actEndTime;
        if (!taskStatus.equals(TaskStatusEnum.COMPLETED.getCode())) {
            //往前7天的零点 -- 当天的最后时间
            LocalDateTime lastSevenDay = LocalDateTime.of(estEndTime.plusDays(-6).toLocalDate(), LocalTime.MIN);
            if (now.isBefore(lastSevenDay))
                dateStatus = DateStatusEnum.NOT_TO_THE_NODE.getCode();
            else if (now.isAfter(LocalDateTime.of(estEndTime.toLocalDate(), LocalTime.MAX)))
                dateStatus = DateStatusEnum.DELAY.getCode();
            else
                dateStatus = DateStatusEnum.ADJACENT_NODE.getCode();
        } else {
            //比截止日期的最后时间大
            if (now.isAfter(LocalDateTime.of(estEndTime.toLocalDate(), LocalTime.MAX)))
                dateStatus = DateStatusEnum.DELAY_COMPLETION.getCode();
            else
                dateStatus = DateStatusEnum.FINISH_ON_TIME.getCode();
        }
        return dateStatus;
    }

    private void setProjectStatusByMilestone(String projectId) {
        Project project = projectDao.selectById(projectId);
        if (project != null){
            String maxMilestoneStatus = projectMilestoneDao.getMaxStatusByProjectIdAndEnable(projectId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
//            if (com.touchspring.core.utils.StringUtils.equals(maxMilestoneStatus, DateStatusEnum.ADJACENT_NODE.getCode()))
//                maxMilestoneStatus = DateStatusEnum.FINISH_ON_TIME.getCode();
            project.setStatus(maxMilestoneStatus);
            // 取最大的启用状态里程碑截止时间
            LocalDateTime maxProjectEstEndTime = projectMilestoneDao.getMaxEstEndTimeByProjectIdAndEnable(projectId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            project.setEstEndTime(maxProjectEstEndTime);
            projectDao.updateById(project);
        }
    }

    /**
     * 查找父圈列表 根据圈子名称
     *
     * @return .
     */
    @PostMapping("parent-list")
    public void connetLDAP(String filter) throws NamingException {
        // 连接Ldap需要的信息
        String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        String ldapUrl = "LDAP://10.250.20.1";
        String domain = "OU=SSDTC,DC=SSDTC,DC=com";
        String ldapAccount = "ssdt_bpm@SSDTC.com"; // 用户名
        String ldapPwd = "p@ssw0rd001";//密码
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
        // LDAP server
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapAccount);
        env.put(Context.SECURITY_CREDENTIALS, ldapPwd);
        LdapContext ctxTDS = null;
        try {
            ctxTDS = new InitialLdapContext(env, null);
            System.out.println("success");
        } catch (NamingException e) {
            e.printStackTrace();
        }


//        String filter = "(objectclass=*)";
//要获取的字段信息
        SearchControls searchControls = new SearchControls();//搜索控件
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);//搜索范围
//1.要搜索的上下文或对象的名称；2.过滤条件，可为null，默认搜索所有信息；3.搜索控件，可为null，使用默认的搜索控件
        NamingEnumeration<SearchResult> answer = ctxTDS.search("DC=SSDTC,DC=com", filter, searchControls);
        int count = 0;
        while (answer.hasMore()) {
            if (count < 100) {
                SearchResult result = (SearchResult) answer.next();
                NamingEnumeration attrs = result.getAttributes().getAll();
                while (attrs.hasMore()) {

                    Attribute attr = (Attribute) attrs.next();


                    String attrType = attr.getID();

                    NamingEnumeration values = attr.getAll();
                    while (values.hasMore()) {
                        Object oneVal = values.nextElement();
                        if (oneVal instanceof String) {
                            log.info(attrType + ": " + (String) oneVal);
                        } else {
                            log.info(attrType + ": " + oneVal.getClass().getName() + ":" + new String((byte[]) oneVal));
                        }
                    }


                }
                count++;
                log.info("--------------------------------------");
                log.info("--------------------------------------");
                log.info("--------------------------------------");
                log.info("--------------------------------------");
            } else {
                break;
            }
        }


//        System.out.println("验证结果：" + ldapService.ldapAuth("ssdt_bpm", "p@ssw0rd001"));


    }



}
