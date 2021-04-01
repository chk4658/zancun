package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.TaskData;

/**
 * <p>
 * 任务数据表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskDataService extends IService<TaskData> {

    boolean save(TaskData taskData, String userId);

    boolean delete(String id);

}
