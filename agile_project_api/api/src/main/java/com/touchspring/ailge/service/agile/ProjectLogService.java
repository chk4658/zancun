package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.entity.agile.ProjectLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目日志表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectLogService extends IService<ProjectLog> {

    void saveMilestoneChange(String type, String milestoneName, String projectId, String userId);

    void batchSaveMilestoneChange(String type, List<String> milestoneNameList, String projectId, String userId);

    void save(String content, String projectId, String userId);

}
