package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.entity.agile.ProjectTemplateType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目模板类型表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateTypeService extends IService<ProjectTemplateType> {

    boolean save(ProjectTemplateType projectTemplateType);

    ProjectTemplateType findByProjectTemplateId(String projectTemplateId);

    boolean delete(String id);

}
