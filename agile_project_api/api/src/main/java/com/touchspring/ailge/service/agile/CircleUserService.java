package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.entity.agile.CircleUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.sys.SysUser;

import java.util.List;

/**
 * <p>
 * 圈子角色与人员关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
public interface CircleUserService extends IService<CircleUser> {

    boolean save(String circleId, List<String> userList, String type);

    List<SysUser> findByCircleIdAndType(String circleId, String type);

    boolean delete(String circleId, String type);

    void compareCircleUser(String circleId, List<String> currentUserList, String type);
}
