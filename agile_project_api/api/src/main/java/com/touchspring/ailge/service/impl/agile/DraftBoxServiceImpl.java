package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.DraftBoxDao;
import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.DraftBox;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.service.agile.DraftBoxService;
import com.touchspring.ailge.service.agile.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DraftBoxServiceImpl extends ServiceImpl<DraftBoxDao, DraftBox> implements DraftBoxService {

    @Autowired
    private DraftBoxDao draftBoxDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectService projectService;

    /**
     * 删除 删除草稿箱及关联项目、关联表
     * @param id 草稿箱ID
     * @return .
     */
    @Override
    public boolean delete(String id, String userId) {
        //删除草稿箱对应的项目
        DraftBox draftBox = new LambdaQueryChainWrapper<DraftBox>(draftBoxDao).eq(BaseEntity::getId, id).select(DraftBox::getProjectId).one();
        //根据项目ID删除关系表
        projectService.deleteById(draftBox.getProjectId(), userId);
        draftBoxDao.deleteById(id);
        return true;
    }

    /**
     * 根据草稿箱获取对应的项目内容
     * @param draftBoxId 草稿箱ID
     * @return .
     */
    @Override
    public Project findByDraftBoxId(String draftBoxId) {
        DraftBox draftBox = new LambdaQueryChainWrapper<DraftBox>(draftBoxDao).eq(BaseEntity::getId, draftBoxId).select(DraftBox::getProjectId).one();
        return projectDao.selectById(draftBox.getProjectId());
    }
}
