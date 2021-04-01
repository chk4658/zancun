package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dao.agile.TaskUserDao;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.agile.TaskUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.service.agile.TaskUserService;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 圈子记录表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class TaskUserServiceImpl extends ServiceImpl<TaskUserDao, TaskUser> implements TaskUserService {

    @Autowired
    private TaskUserDao taskUserDao;

    @Autowired
    private TaskDao taskDao;

    /**
     * 保存或更新
     */
    @Override
    public boolean save(TaskUser taskUser, String createUserId) {
        if (StringUtils.isBlank(taskUser.getTaskId())) return false;
        //删除
        taskUserDao.deleteByTaskId(taskUser.getTaskId());
        if (StringUtils.isBlank(taskUser.getReviewSysUserId()) && StringUtils.isBlank(taskUser.getConfirmSysUserId())) return true;
        //保存
        taskUser.setCreateUserId(createUserId);
        taskUser.setUpdateUserId(createUserId);
        taskUserDao.insert(taskUser);
        return true;
    }

    /**
     * 当前用户为审核人、负责人 或者临时任务创建人
     */
    @Override
    public List<Task> findCurUserTemporaryTask(String userId) {
        return taskDao.findCurUserTemporaryTask(userId, BaseEnums.YES.getCode());
    }

}
