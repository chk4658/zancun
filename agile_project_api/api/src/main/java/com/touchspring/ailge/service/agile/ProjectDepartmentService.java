package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectDepartment;

import java.util.List;

/**
 * <p>
 * 项目与部门关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
public interface ProjectDepartmentService extends IService<ProjectDepartment> {

    void save(String projectId, List<String> sysDepartmentIds);

    void deleteByProjectId(String projectId);

    void compareProjectDepartment(String projectId, List<String> curSysDepartmentIds);
}
