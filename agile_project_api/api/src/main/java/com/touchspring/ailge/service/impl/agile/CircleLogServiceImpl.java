package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.CircleLogDao;
import com.touchspring.ailge.dao.agile.ProjectLogDao;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.entity.agile.ProjectLog;
import com.touchspring.ailge.service.agile.CircleLogService;
import com.touchspring.ailge.service.agile.ProjectLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
public class CircleLogServiceImpl extends ServiceImpl<CircleLogDao, CircleLog> implements CircleLogService {

    @Autowired
    private CircleLogDao circleLogDao;

    /**
     * 圈子角色变更
     * @param roleNames 角色名称
     * @param userId 创建用户
     * @param type 执行操作类型
     * @param circleId 圈子ID
     */
    @Override
    public void saveRoleChangeToLog(List<String> roleNames, String userId, String type, String circleId) {
        if (CollectionUtils.isEmpty(roleNames)) return;
        CircleLog circleLog = new CircleLog();
        circleLog.setCreateUserId(userId);
        circleLog.setContent(type + "圈子角色: " + StringUtils.join(roleNames.toArray(), ","));
        circleLog.setCircleId(circleId);
        circleLogDao.insert(circleLog);
    }

    /**
     * 子圈操作
     * @param type 执行操作类型
     * @param childCircleName 子圈名称
     * @param userId 用户ID
     * @param parentId 父圈ID
     */
    @Override
    public void saveChildCircleChange(String type, String childCircleName, String userId, String parentId) {
        CircleLog circleLog = new CircleLog();
        circleLog.setCreateUserId(userId);
        circleLog.setContent(type + "子圈: " + childCircleName);
        circleLog.setCircleId(parentId);
        circleLogDao.insert(circleLog);
    }

    @Override
    public void saveCircleLog(String content, String circleId, String userId) {
        CircleLog circleLog = new CircleLog();
        circleLog.setCreateUserId(userId);
        circleLog.setContent(content);
        circleLog.setCircleId(circleId);
        circleLogDao.insert(circleLog);
    }
}
