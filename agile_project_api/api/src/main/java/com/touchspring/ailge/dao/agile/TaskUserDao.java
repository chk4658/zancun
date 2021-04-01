package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.entity.agile.TaskUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 临时任务-人 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface TaskUserDao extends BaseMapper<TaskUser> {

    @Delete("DELETE FROM TASK_USER WHERE TASK_ID = #{taskId}")
    int deleteByTaskId(@Param("taskId") String taskId);

}
