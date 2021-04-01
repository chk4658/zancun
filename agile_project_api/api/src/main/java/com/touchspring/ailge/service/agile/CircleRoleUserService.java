package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.CircleRoleUser;
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
public interface CircleRoleUserService extends IService<CircleRoleUser> {

    Page<SysUser> findByCircleRoleId(Integer page, Integer size, String circleRoleId);

    boolean delete(String circleRoleId, String userId);

    boolean batchDelete(String circleRoleId, String[] userIds);

    boolean save(String circleRoleId, String[] userIds, String userId);

    Integer[] allCircleNum(String parentCircleId);

    void insertOrUpdateUserByCircleRole(String circleId, String roleName, String userId);

}
