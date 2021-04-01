package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectIssueHistoryDao;
import com.touchspring.ailge.entity.agile.ProjectIssue;
import com.touchspring.ailge.entity.agile.ProjectIssueHistory;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("//project-issue-history")
public class ProjectIssueHistoryController {

    @Autowired
    private ProjectIssueHistoryDao projectIssueHistoryDao;

    @GetMapping("findListByProjectIssueId")
    public ResultData findListByProjectIssueId(
                              String projectIssueId,
                              @RequestHeader(SystemConfig.TOKEN) String token) {
        List<ProjectIssueHistory> projectIssueHistoryList = projectIssueHistoryDao.findListByProjectIssueId(projectIssueId);
        return ResultData.ok().putDataValue("projectIssueHistoryList", projectIssueHistoryList);
    }

}
