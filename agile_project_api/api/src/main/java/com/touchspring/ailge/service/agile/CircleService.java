package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.CircleDTO;
import com.touchspring.ailge.entity.agile.Circle;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.CircleOperationType;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleService extends IService<Circle> {

    List<Circle> findAll(String userId);

    boolean save(CircleDTO circleDTO, String curUserId);

    List<Circle> findByName(String name);

    List<Circle> findByParentId(String parentId);

    boolean delete(String circleId, String userId);

    List<Circle> getMyCircles(SysUser sysUser, String searchName);

    /**
     * 根据id获取所有上级圈子 非树状结构 不包含本身
     * @param circleId
     * @return
     */
    List<Circle> findParentsById(String circleId);

    Integer[] projectNum(String parentCircleId);

    List<Circle> findByCircleIdAndSearchName(String parentId, String searchName);


    /**
     *  获取用户圈子权限
     * @param userId
     * @return
     */
    CircleOperationType.UserOperation getCircleRoleOperation(String userId);

}
