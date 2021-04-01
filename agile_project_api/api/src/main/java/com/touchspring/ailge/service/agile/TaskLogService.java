package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.TaskLog;

import java.util.List;

/**
 * <p>
 * 任务记录表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskLogService extends IService<TaskLog> {

    void saveAttrChange(String userId, String attrName, String taskId, String projectId, String lastValue, String curValue);

    void saveChildTaskChange(String userId, List<String> childNames, String parentId, String type, String projectId);

}
