package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.touchspring.ailge.entity.agile.ProjectLog;
import com.touchspring.ailge.dao.agile.ProjectLogDao;
import com.touchspring.ailge.service.agile.ProjectLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目日志表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectLogServiceImpl extends ServiceImpl<ProjectLogDao, ProjectLog> implements ProjectLogService {

    @Autowired
    private ProjectLogDao projectLogDao;

    @Override
    public void saveMilestoneChange(String type, String milestoneName, String projectId, String userId) {
        ProjectLog projectLog = new ProjectLog();
        projectLog.setContent(type + "里程碑: " + milestoneName);
        projectLog.setCreateUserId(userId);
        projectLog.setProjectId(projectId);
        projectLogDao.insert(projectLog);
    }

    /**
     * 批量创建里程碑
     * @param type 操作类型
     * @param milestoneNameList 里程碑名称列表
     * @param projectId 项目ID
     * @param userId 创建人ID
     */
    @Override
    public void batchSaveMilestoneChange(String type, List<String> milestoneNameList, String projectId, String userId) {
        if (CollectionUtils.isEmpty(milestoneNameList)) return;
        ProjectLog projectLog = new ProjectLog();
        projectLog.setContent(type + "里程碑: " + StringUtils.join(milestoneNameList.toArray(), ","));
        projectLog.setCreateUserId(userId);
        projectLog.setProjectId(projectId);
        projectLogDao.insert(projectLog);
    }

    @Override
    public void save(String content, String projectId, String userId) {
        ProjectLog projectLog = new ProjectLog();
        projectLog.setContent(content);
        projectLog.setCreateUserId(userId);
        projectLog.setProjectId(projectId);
        projectLogDao.insert(projectLog);
    }
}
