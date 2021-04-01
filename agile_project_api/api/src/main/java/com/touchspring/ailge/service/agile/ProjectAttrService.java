package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目任务属性字段表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectAttrService extends IService<ProjectAttr> {

    boolean delete(String id);

    List<ProjectAttr> findByProjectId(String projectId);

}
