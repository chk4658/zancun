package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;

/**
 * <p>
 * 圈子角色关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleRoleService extends IService<CircleRole> {

    boolean save(String circleId, List<String> roleNames);

    void compareCircleRole(String circleId, List<String> currentRoleList, String curUserId);

    List<CircleRole> findByCircleId(String circleId);

    boolean batchDelete(String[] ids, String userId);

    ResultData insert(CircleRole circleRole, String userId);



}
