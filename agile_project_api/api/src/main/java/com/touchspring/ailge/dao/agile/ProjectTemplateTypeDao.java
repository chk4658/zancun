package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.entity.agile.ProjectTemplateType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目模板类型表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateTypeDao extends BaseMapper<ProjectTemplateType> {

    List<ProjectTemplateType> findAllByNameAsc(@Param("name") String name);

}
