package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目模板里程碑表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateMilestoneDao extends BaseMapper<ProjectTemplateMilestone> {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao.findParentListByProjectTemplateMilestoneId")),
    })
    @Select("select * from PROJECT_TEMPLATE_MILESTONE where is_delete = 0 and PROJECT_TEMPLATE_ID = #{projectTemplateId}")
    List<ProjectTemplateMilestone> findListByProjectTemplateId(String projectTemplateId);

    List<ProjectTemplateMilestone> listByProjectTemplateId(Page<ProjectTemplateMilestone> templateMilestonePage,
                                                           @Param("projectTemplateId") String projectTemplateId, @Param("searchName") String searchName);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao.findParentListByTemplateMilestoneIdNotContainsData")),
    })
    @Select("select * from PROJECT_TEMPLATE_MILESTONE where is_delete = 0 and PROJECT_TEMPLATE_ID = #{projectTemplateId} order by sort")
    List<ProjectTemplateMilestone> findListByProjectTemplateIdNotContainsData(String projectTemplateId);


    ProjectTemplateMilestone getById(String id);

}
