package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectIssue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectIssueDao extends BaseMapper<ProjectIssue> {

    /**
     * 获取列表
     * @param userId
     * @param projectIssue
     * @return
     */
    List<ProjectIssue> findPageByUserIdAndProjectIssue(Page<ProjectIssue> equivalentPage,
                                                       @Param("userId") String userId,
                                                       @Param("projectIssue") ProjectIssue projectIssue);



    ProjectIssue findById(@Param("id") String id);


}
