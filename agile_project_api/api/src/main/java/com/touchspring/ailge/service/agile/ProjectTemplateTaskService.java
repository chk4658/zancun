package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.dto.ProjectTemplateTaskDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

/**
 * <p>
 * 项目模板任务表
 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateTaskService extends IService<ProjectTemplateTask> {

    boolean save(ProjectTemplateTaskDTO projectTemplateTaskDTO);

    boolean delete(String id);

    boolean deleteByProjectTemplateMilestoneId(String templateMilestoneId);

    ResultData upTask(String id);

    ResultData downTask(String id);

    boolean batchDelete(String[] ids);
}
