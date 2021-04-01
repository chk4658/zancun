package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectIssue;

public interface ProjectIssueService extends IService<ProjectIssue> {


    /**
     * 保存或者更新
     * @param projectIssue
     * @param userId
     * @return
     */
    boolean saveOrUpdate(ProjectIssue projectIssue,String userId) throws Exception;

    /**
     * 设置项目所处阶段
     * @param stageCode
     * @param projectIssueId
     * @param userId
     */
    ProjectIssue setProjectIssueStage(int stageCode,String  projectIssueId,String userId);
}
