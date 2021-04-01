package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目模板表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateService extends IService<ProjectTemplate> {

    boolean save(ProjectTemplate projectTemplate, String userId);

    boolean disableOrRestore(String id, boolean flag);

    List<ProjectTemplate> findByProjectTemplateTypeId(String projectTemplateTypeId, String searchName,Long enabled);

    List<TreeResultDTO> findTreeResult(String projectTemplateId);

    boolean delete(String id);

}
