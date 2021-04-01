package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectIssueDao;
import com.touchspring.ailge.dao.agile.ProjectIssueHistoryDao;
import com.touchspring.ailge.entity.agile.ProjectIssue;
import com.touchspring.ailge.entity.agile.ProjectIssueHistory;
import com.touchspring.ailge.service.agile.ProjectIssueService;
import com.touchspring.ailge.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class ProjectIssueServiceImpl extends ServiceImpl<ProjectIssueDao, ProjectIssue> implements ProjectIssueService {

    @Autowired
    private ProjectIssueDao projectIssueDao;
    @Autowired
    private ProjectIssueHistoryDao projectIssueHistoryDao;


    @Override
    public boolean saveOrUpdate(ProjectIssue projectIssue, String userId) throws Exception {
        projectIssue.setUpdateUserId(userId);
        projectIssue.setUpdateAt(new Date());
        projectIssue.setStage(this.getProjectIssueStage(projectIssue));
        projectIssue.setStatus(this.getProjectIssueStatus(projectIssue));

        int resultI;
        if (StringUtils.isEmpty(projectIssue.getId())) {
            projectIssue.setId(IdWorker.nextId());
            resultI = projectIssueDao.insert(projectIssue);
        } else {
            resultI = projectIssueDao.updateById(projectIssue);
        }
        ProjectIssueHistory projectIssueHistory = new ProjectIssueHistory();
        ProjectIssueHistory.fatherToChild(projectIssue,projectIssueHistory);
        projectIssueHistory.setProjectIssueId(projectIssue.getId());
        projectIssueHistory.setCreateAt(projectIssue.getCreateAt());
        projectIssueHistory.setCreateUserId(projectIssue.getCreateUserId());
        projectIssueHistory.setUpdateAt(new Date());
        projectIssueHistory.setUpdateUserId(userId);
        projectIssueHistory.setId(IdWorker.nextId());
        projectIssueHistoryDao.insert(projectIssueHistory);
        return resultI > 0;
    }

    @Override
    public ProjectIssue setProjectIssueStage(int stageCode, String projectIssueId, String userId)  {
        // 根据id 查询
        ProjectIssue projectIssue = projectIssueDao.selectById(projectIssueId);
        if (projectIssue == null) return null;
        // 设置更新人和更新时间
        projectIssue.setUpdateUserId(userId);
        projectIssue.setUpdateAt(new Date());

        // 插入历史数据
        ProjectIssueHistory projectIssueHistory = new ProjectIssueHistory();
        try {
            ProjectIssueHistory.fatherToChild(projectIssue,projectIssueHistory);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        projectIssueHistory.setProjectIssueId(projectIssue.getId());
        projectIssueHistory.setCreateAt(projectIssue.getCreateAt());
        projectIssueHistory.setCreateUserId(projectIssue.getCreateUserId());
        projectIssueHistory.setUpdateAt(new Date());
        projectIssueHistory.setUpdateUserId(userId);
        projectIssueHistory.setId(IdWorker.nextId());
        projectIssueHistory.setStage(stageCode);
        projectIssueHistory.setHasClose(com.touchspring.ailge.enums.ProjectIssue.Close.NO.getCode());
        projectIssueHistoryDao.insert(projectIssueHistory);
        // 重置数据
        projectIssue = this.setResetProjectIssueByStage(stageCode,projectIssue);
        projectIssueDao.updateById(projectIssue);
        return projectIssue;

    }



    private ProjectIssue setResetProjectIssueByStage(int stageCode, ProjectIssue projectIssue) {
        // 重置长期措施
        if (stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.LONG_MEASURE.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.SHORT_MEASURE.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.ANALYSIS.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.MANAGER.getCode()) {
            projectIssue.setLongMeasure(null);
            projectIssue.setLongMeasureActualTime(null);
            projectIssue.setLongMeasureExpectTime(null);
        }
        // 重置短期措施
        if (stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.SHORT_MEASURE.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.ANALYSIS.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.MANAGER.getCode()) {
            projectIssue.setShortMeasure(null);
            projectIssue.setShortMeasureActualTime(null);
            projectIssue.setShortMeasureExpectTime(null);
        }
        // 重置分析人阶段
        if (stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.ANALYSIS.getCode()
                || stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.MANAGER.getCode()) {
            projectIssue.setLongMeasureUserId(null);
            projectIssue.setShortMeasureUserId(null);
            projectIssue.setCause(null);
        }
        // 重置项目经理阶段
        if (stageCode == com.touchspring.ailge.enums.ProjectIssue.Stage.MANAGER.getCode()) {
            projectIssue.setPriority(null);
            projectIssue.setHasRepeat(null);
            projectIssue.setAnalysisUserId(null);

        }
        // 设置项目所处阶段
        projectIssue.setStage(stageCode - 1);
        // 设置项目状态
        projectIssue.setStatus(this.getProjectIssueStatus(projectIssue));
        return projectIssue;
    }


    /**
     * 项目当前状态
     * 1 如果问题关闭 状态：4/4表示已完成
     * 2 如果项目有长期措施并且实际时间<= 当前时间 状态3/4表示已实施待跟踪 反正 2/4表示有长期措施；
     * 4 如果项目有短期措施 状态1/4表示有短期措施；
     * 5 如果项目刚创建 0/4表示无措施
     */
    private int getProjectIssueStatus(ProjectIssue projectIssue) {
        int statusCode;
        if(projectIssue.getHasClose() != null && projectIssue.getHasClose().equals(com.touchspring.ailge.enums.ProjectIssue.Close.YES.getCode())) {
            statusCode = com.touchspring.ailge.enums.ProjectIssue.Status.COMPLETE.getCode();
        } else if (!StringUtils.isEmpty(projectIssue.getLongMeasure())) {
            if (projectIssue.getLongMeasureActualTime() != null && projectIssue.getLongMeasureActualTime().getTime() <= System.currentTimeMillis()) {
                statusCode = com.touchspring.ailge.enums.ProjectIssue.Status.TRACKING.getCode();
            } else {
                statusCode = com.touchspring.ailge.enums.ProjectIssue.Status.LONG_MEASURE.getCode();
            }
        } else if (!StringUtils.isEmpty(projectIssue.getShortMeasure())) {
            statusCode = com.touchspring.ailge.enums.ProjectIssue.Status.SHORT_MEASURE.getCode();
        } else {
            statusCode = com.touchspring.ailge.enums.ProjectIssue.Status.NO_MEASURE.getCode();
        }
        return statusCode;
    }


    /**
     * 项目所处阶段
     * 1 项目创建 错处状态创建
     * 2 项目已被关闭则完成状态
     * 3 项目有长期措施 则长期措施阶段
     * 4 项目有短期措施 则短期措施阶段
     * 5 项目有长期措施分析人 则分析人阶段
     * 6 项目有分析人 则项目经理阶段
     */
    private int getProjectIssueStage(ProjectIssue projectIssue) {
        int stageCode;
        if (projectIssue.getHasClose() != null
                && projectIssue.getHasClose().equals(com.touchspring.ailge.enums.ProjectIssue.Close.YES.getCode())) {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.CLOSE.getCode();
            // 长期措施阶段
        } else if (!StringUtils.isEmpty(projectIssue.getLongMeasure())) {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.LONG_MEASURE.getCode();
            // 短期措施阶段
        } else if (!StringUtils.isEmpty(projectIssue.getShortMeasure())) {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.SHORT_MEASURE.getCode();
            // 分析人阶段
        } else if (!StringUtils.isEmpty(projectIssue.getCause())) {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.ANALYSIS.getCode();
            // 项目经理阶段
        } else if (!StringUtils.isEmpty(projectIssue.getAnalysisUserId())) {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.MANAGER.getCode();
        } else {
            stageCode = com.touchspring.ailge.enums.ProjectIssue.Stage.CREATE.getCode();
        }
        return stageCode;
    }



}
