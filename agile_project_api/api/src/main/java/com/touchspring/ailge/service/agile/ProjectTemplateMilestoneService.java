package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dto.ProjectTemplateMilestoneDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;

/**
 * <p>
 * 项目模板里程碑表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateMilestoneService extends IService<ProjectTemplateMilestone> {

    List<ProjectTemplateMilestone> findByProjectTemplateId(Page<ProjectTemplateMilestone> templateMilestonePage, String projectTemplateId, String searchName);

    boolean save(ProjectTemplateMilestoneDTO projectTemplateMilestoneDTO);

    boolean delete(String id);

    boolean deleteByProjectTemplateId(String projectTemplateId);

    ResultData upMilestone(String id);

    ResultData downMilestone(String id);


    ProjectTemplateMilestone getId(String id);

}
