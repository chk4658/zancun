package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.entity.agile.ProjectTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目模板表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateDao extends BaseMapper<ProjectTemplate> {

    List<ProjectTemplate> findByProjectTemplate(@Param("projectTemplate") ProjectTemplate projectTemplate);

    List<ProjectTemplate> findByProjectTemplateContainsAttr(@Param("projectTemplate") ProjectTemplate projectTemplate);

    List<ProjectTemplate> findAllByProjectTemplateId(@Param("projectTemplateId") String projectTemplateId);

}
