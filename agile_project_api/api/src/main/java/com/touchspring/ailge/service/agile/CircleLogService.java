package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.entity.agile.ProjectLog;

import java.util.List;

/**
 * <p>
 * 圈子记录表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleLogService extends IService<CircleLog> {

    void saveRoleChangeToLog(List<String> roleNames, String userId, String type, String circleId);

    void saveChildCircleChange(String type, String childCircleName, String userId, String parentId);

    void saveCircleLog(String content, String circleId, String userId);

}
