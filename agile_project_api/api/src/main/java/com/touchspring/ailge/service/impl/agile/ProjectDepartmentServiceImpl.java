package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectDepartmentDao;
import com.touchspring.ailge.entity.agile.ProjectDepartment;
import com.touchspring.ailge.service.agile.ProjectDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目与部门关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Service
public class ProjectDepartmentServiceImpl extends ServiceImpl<ProjectDepartmentDao, ProjectDepartment> implements ProjectDepartmentService {

    @Autowired
    private ProjectDepartmentDao projectDepartmentDao;

    /**
     * 保存项目与部门关系
     * @param projectId 项目ID
     * @param sysDepartmentIds 可见部门ID集合
     */
    @Override
    public void save(String projectId, List<String> sysDepartmentIds) {
        List<ProjectDepartment> projectDepartments = new ArrayList<>();
        sysDepartmentIds.forEach(sysDepartmentId -> {
            ProjectDepartment projectDepartment = new ProjectDepartment();
            projectDepartment.setProjectId(projectId);
            projectDepartment.setSysDepartmentId(sysDepartmentId);
            projectDepartments.add(projectDepartment);
        });
        this.saveBatch(projectDepartments);
    }

    /**
     * 删除项目下所有可见的部门
     * @param projectId 项目ID
     */
    @Override
    public void deleteByProjectId(String projectId) {
        LambdaQueryWrapper<ProjectDepartment> queryWrapper = new LambdaQueryWrapper<ProjectDepartment>().eq(ProjectDepartment::getProjectId, projectId);
        projectDepartmentDao.delete(queryWrapper);
    }

    /**
     * 项目的可见部门是否修改
     * @param projectId 项目ID
     * @param curSysDepartmentIds 部门ID
     */
    @Override
    public void compareProjectDepartment(String projectId, List<String> curSysDepartmentIds) {
        //查询之前保存的部门
        List<ProjectDepartment> projectDepartments = new LambdaQueryChainWrapper<ProjectDepartment>(projectDepartmentDao).eq(ProjectDepartment::getProjectId, projectId).select(ProjectDepartment::getSysDepartmentId).list();
        List<String> lastDepartmentIds = projectDepartments.stream().map(ProjectDepartment::getSysDepartmentId).collect(Collectors.toList());

        //比较是否相等
        lastDepartmentIds.sort(Comparator.comparing(String::hashCode));
        curSysDepartmentIds.sort(Comparator.comparing(String::hashCode));
        if (lastDepartmentIds.toString().equals(curSysDepartmentIds.toString())) return;

        //比较人员ID，删除之前多余的
        List<String> deleteDepartmentIds = lastDepartmentIds.stream().filter(item -> !curSysDepartmentIds.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteDepartmentIds)){
            deleteDepartmentIds.forEach(departmentId -> {
                LambdaQueryWrapper<ProjectDepartment> queryWrapper = new LambdaQueryWrapper<ProjectDepartment>().eq(ProjectDepartment::getProjectId, projectId).eq(ProjectDepartment::getSysDepartmentId, departmentId);
                projectDepartmentDao.delete(queryWrapper);
            });
        }

        //添加新增的人员ID
        List<String> insertDepartmentIds = curSysDepartmentIds.stream().filter(item -> !lastDepartmentIds.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertDepartmentIds))
            this.save(projectId, insertDepartmentIds);

    }
}
