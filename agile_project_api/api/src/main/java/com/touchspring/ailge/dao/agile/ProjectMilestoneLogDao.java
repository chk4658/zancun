package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectMilestoneLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 任务记录表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface ProjectMilestoneLogDao extends BaseMapper<ProjectMilestoneLog> {

    List<ProjectMilestoneLog> findByProjectMilestoneId(Page page, @Param("projectMilestoneId") String projectMilestoneId, @Param("searchName") String searchName);
}
