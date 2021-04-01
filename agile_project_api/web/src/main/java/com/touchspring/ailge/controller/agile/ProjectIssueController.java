package com.touchspring.ailge.controller.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectIssueDao;
import com.touchspring.ailge.entity.agile.ProjectIssue;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.service.agile.ProjectIssueService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("//project-issue")
public class ProjectIssueController  {


    @Autowired
    private ProjectIssueDao projectIssueDao;
    @Autowired
    private ProjectIssueService projectIssueService;


    /**
     * 获取数据
     * @param page
     * @param size
     * @param projectIssue
     * @param token
     * @return
     */
    @GetMapping("")
    public ResultData findAll(@RequestParam(defaultValue = "1", required = false) Integer page,
                              @RequestParam(defaultValue = "20", required = false) Integer size,
                              ProjectIssue projectIssue,
                              @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        Page<ProjectIssue> projectIssuePage = new Page<>(page, size);
        projectIssuePage.setRecords(projectIssueDao.findPageByUserIdAndProjectIssue(projectIssuePage,user.getId(),projectIssue));
        return ResultData.ok().putDataValue("projectIssueList", projectIssuePage.getRecords()).putDataValue("totalAmount",projectIssuePage.getTotal());
    }


    @PostMapping("")
    public ResultData saveOrUpdate(ProjectIssue projectIssue,@RequestHeader(SystemConfig.TOKEN) String token) throws Exception {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        projectIssueService.saveOrUpdate(projectIssue,user.getId());
        return ResultData.ok();
    }

    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id,@RequestHeader(SystemConfig.TOKEN) String token){
        return ResultData.ok().putDataValue("projectIssue",projectIssueDao.findById(id));
    }

    /**
     * 获取所有阶段
     * @return
     */
    @GetMapping("getProjectIssueStatus")
    public ResultData getProjectIssueStatus() {
        // 返回所有阶段 过滤完成和创建
        List<com.touchspring.ailge.enums.ProjectIssue.StatusC> statusC= Arrays.stream(com.touchspring.ailge.enums.ProjectIssue.Status.values())
               .map(s ->  new  com.touchspring.ailge.enums.ProjectIssue.StatusC(s.getCode(),s.getMessage()) ).collect(Collectors.toList());
        return ResultData.ok().putDataValue("status",statusC);
    }


    /**
     * 获取所有阶段
     * @return
     */
    @GetMapping("getProjectIssueStage")
    public ResultData getProjectIssueStage() {
        // 返回所有阶段 过滤完成和创建
        List<com.touchspring.ailge.enums.ProjectIssue.StageC> stageCS = Arrays.stream(com.touchspring.ailge.enums.ProjectIssue.Stage.values())
                .filter(s ->
                        s.getCode() != com.touchspring.ailge.enums.ProjectIssue.Stage.CLOSE.getCode()
                        && s.getCode() != com.touchspring.ailge.enums.ProjectIssue.Stage.CREATE.getCode())
                .map(s ->  new  com.touchspring.ailge.enums.ProjectIssue.StageC(s.getCode(),s.getMessage()) ).collect(Collectors.toList());
        return ResultData.ok().putDataValue("stages",stageCS);
    }


    @PutMapping("setProjectIssueStage")
    public ResultData setProjectIssueStage(int stageCode,String  id,@RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        ProjectIssue projectIssue = projectIssueService.setProjectIssueStage(stageCode,id,user.getId());
        return ResultData.ok().putDataValue("projectIssue",projectIssue);
    }
























}
