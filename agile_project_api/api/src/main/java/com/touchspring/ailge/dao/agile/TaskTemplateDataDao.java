package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.TaskTemplateData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 草稿箱 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface TaskTemplateDataDao extends BaseMapper<TaskTemplateData> {

    List<TaskTemplateData> findByTemplateTaskId(@Param("templateTaskId") String templateTaskId);

}
