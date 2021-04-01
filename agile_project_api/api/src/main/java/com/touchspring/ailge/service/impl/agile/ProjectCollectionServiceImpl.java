package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectCollectionDao;
import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectCollection;
import com.touchspring.ailge.service.agile.ProjectCollectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目与人员收藏关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Service
public class ProjectCollectionServiceImpl extends ServiceImpl<ProjectCollectionDao, ProjectCollection> implements ProjectCollectionService {

    @Autowired
    private ProjectCollectionDao projectCollectionDao;
    @Autowired
    private ProjectDao projectDao;

    /**
     * 添加/取消收藏 true -> 收藏 false -> 取消收藏
     */
    @Override
    public boolean addOrCancelCollection(String sysUserId, String projectId, boolean flag) {
        ProjectCollection lastProjectCollection = new LambdaQueryChainWrapper<ProjectCollection>(projectCollectionDao).eq(ProjectCollection::getProjectId, projectId)
                .eq(ProjectCollection::getSysUserId, sysUserId).one();
        if (flag){
            if (lastProjectCollection != null) return false;
            ProjectCollection projectCollection = new ProjectCollection();
            projectCollection.setProjectId(projectId);
            projectCollection.setSysUserId(sysUserId);
            projectCollectionDao.insert(projectCollection);
        } else {
            if (lastProjectCollection == null) return false;
            projectCollectionDao.deleteById(lastProjectCollection.getId());
        }
        return true;
    }

    /**
     * 当前用户的收藏项目
     */
    @Override
    public List<Project> userFavorites(String sysUserId, String searchName) {
        List<ProjectCollection> projectCollectionList = new LambdaQueryChainWrapper<ProjectCollection>(projectCollectionDao)
                .eq(ProjectCollection::getSysUserId, sysUserId).orderByAsc(BaseEntity::getCreateAt).select(ProjectCollection::getProjectId).list();
        List<Project> projects = new ArrayList<>();
        if (CollectionUtils.isEmpty(projectCollectionList)) return projects;
        List<String> projectIds = projectCollectionList.stream().filter(projectCollection -> projectCollection!= null && projectCollection.getProjectId() != null).map(ProjectCollection::getProjectId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(projectIds))
            projects = projectDao.findByProjectIdsAndName(projectIds, searchName);
        return projects;
    }
}
