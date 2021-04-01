package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dto.PlatformOpenDTO;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.service.agile.ProjectService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台开阀
 */
@RestController
@RequestMapping("//platform-open")
public class PlatformOpenController {

    private final ProjectService projectService;

    public PlatformOpenController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 全部 平台开阀 ----> 全部项目当前用户所有可见项目  其中，管理员可见所有项目 todo 包含归档项目
     */
    @GetMapping("list")
    public ResultData findAllProject(@RequestParam(defaultValue = "1", required = false) Integer pageIndex,
                                     @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                     @RequestHeader(SystemConfig.TOKEN) String token,
                                     @RequestParam(required = false) String searchName){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        //全部
        List<Project> projects = projectService.findAllVisibleProjectContainsAdmin(user.getId(), searchName, null);
        //分页
//        List<Project> projects = projectService.platformOpenList(pageIndex, pageSize, user.getId(), searchName);
        return ResultData.ok().putDataValue("projects", projects).putDataValue("totalAmount", projects.size());
    }

    /**
     * 根据项目ID 查找开阀的任务及对应里程碑
     */
    @GetMapping("getOpenTask")
    public ResultData findAllPlatformOpenByProjectId(String[] projectIds){
        PlatformOpenDTO openByProjectId = projectService.findAllPlatformOpenByProjectId(projectIds);
        return ResultData.ok().putDataValue("projects", openByProjectId.getProjects()).putDataValue("templateTasks", openByProjectId.getProjectTemplateTasks());
    }

}
