package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 项目任务属性字段表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateAttrDao extends BaseMapper<ProjectTemplateAttr> {

    @Select("select * from PROJECT_TEMPLATE_ATTR where is_delete = 0 and PROJECT_TEMPLATE_ID = #{projectTemplateId}")
    List<ProjectTemplateAttr> findListByProjectTemplateId(String projectTemplateId);

}
