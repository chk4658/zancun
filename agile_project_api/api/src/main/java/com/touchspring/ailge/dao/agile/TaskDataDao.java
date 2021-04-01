package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.TaskData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 任务数据表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskDataDao extends BaseMapper<TaskData> {

    List<TaskData> findByTaskId(@Param("taskId") String taskId);

}
