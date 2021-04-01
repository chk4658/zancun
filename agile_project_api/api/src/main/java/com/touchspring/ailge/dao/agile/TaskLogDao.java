package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.TaskLog;
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
public interface TaskLogDao extends BaseMapper<TaskLog> {

    List<TaskLog> findByTaskId(Page page, @Param("taskId") String taskId, @Param("searchName") String searchName);
}
