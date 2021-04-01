package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目日志表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectLogDao extends BaseMapper<ProjectLog> {

    List<ProjectLog> findByProjectId(Page page, @Param("projectId") String projectId, @Param("searchName") String searchName);

}
