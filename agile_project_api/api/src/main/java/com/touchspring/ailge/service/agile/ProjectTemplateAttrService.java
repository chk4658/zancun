package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;

import java.util.List;

/**
 * <p>
 * 项目任务属性字段表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateAttrService extends IService<ProjectTemplateAttr> {

    List<ProjectTemplateAttr> findByProjectTemplateId(String projectTemplateId);

    boolean delete(String id);

}
