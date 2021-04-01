package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.ProjectIssue;
import com.touchspring.ailge.entity.agile.ProjectIssueHistory;

import java.util.List;

public interface ProjectIssueHistoryDao extends BaseMapper<ProjectIssueHistory> {


    List<ProjectIssueHistory> findListByProjectIssueId(String projectIssueId);






}
