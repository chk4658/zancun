package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskUser;

import java.util.List;

/**
 * <p>
 * 圈子记录表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface TaskUserService extends IService<TaskUser> {

    boolean save(TaskUser taskUser, String createUserId);

    List<Task> findCurUserTemporaryTask(String userId);

}
