package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectTemplateAttrDao;
import com.touchspring.ailge.dao.agile.TaskTemplateDataDao;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;
import com.touchspring.ailge.entity.agile.TaskTemplateData;
import com.touchspring.ailge.service.agile.ProjectTemplateAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 项目任务属性字段表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateAttrServiceImpl extends ServiceImpl<ProjectTemplateAttrDao, ProjectTemplateAttr> implements ProjectTemplateAttrService {

    @Autowired
    private ProjectTemplateAttrDao projectTemplateAttrDao;
    @Autowired
    private TaskTemplateDataDao taskTemplateDataDao;

    @Override
    public List<ProjectTemplateAttr> findByProjectTemplateId(String projectTemplateId) {
        return new LambdaQueryChainWrapper<>(projectTemplateAttrDao).eq(ProjectTemplateAttr::getProjectTemplateId, projectTemplateId).list();
    }

    /**
     * 删除
     * @param id 属性ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
        //删除任务模板数据表
        taskTemplateDataDao.delete(new LambdaQueryWrapper<TaskTemplateData>().eq(TaskTemplateData::getProjectTemplateAttrId, id));
        projectTemplateAttrDao.deleteById(id);
        return true;
    }
}
