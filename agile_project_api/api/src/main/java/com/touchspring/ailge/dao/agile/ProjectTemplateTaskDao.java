package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 项目模板任务表
 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateTaskDao extends BaseMapper<ProjectTemplateTask> {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao.findChildListByParentId")),
            @Result(column = "id", property = "taskDataList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskTemplateDataDao.findByTemplateTaskId"))
    })
    @Select("select * from PROJECT_TEMPLATE_TASK where is_delete = 0 and PARENT_ID IS NULL and PROJECT_TEMPLATE_MILESTONE_ID = #{projectTemplateMilestoneId}")
    List<ProjectTemplateTask> findParentListByProjectTemplateMilestoneId(String projectTemplateMilestoneId);


    @Select("select * from PROJECT_TEMPLATE_TASK where is_delete = 0 and PARENT_ID = #{parentId}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskDataList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskTemplateDataDao.findByTemplateTaskId"))
    })
    List<ProjectTemplateTask> findChildListByParentId(String parentId);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao.findChildListByParentIdNotContainsData"))
    })
    @Select("select * from PROJECT_TEMPLATE_TASK where is_delete = 0 and PARENT_ID IS NULL and PROJECT_TEMPLATE_MILESTONE_ID = #{projectTemplateMilestoneId} order by sort")
    List<ProjectTemplateTask> findParentListByTemplateMilestoneIdNotContainsData(String projectTemplateMilestoneId);

    @Select("select * from PROJECT_TEMPLATE_TASK where is_delete = 0 and PARENT_ID = #{parentId} order by sort")
    List<ProjectTemplateTask> findChildListByParentIdNotContainsData(String parentId);
}
