package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.agile.CircleUser;
import com.touchspring.ailge.dao.agile.CircleUserDao;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.service.agile.CircleUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 圈子角色与人员关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@Service
public class CircleUserServiceImpl extends ServiceImpl<CircleUserDao, CircleUser> implements CircleUserService {

    @Autowired
    private CircleUserDao circleUserDao;
    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 保存圈子与人员关系
     * @param circleId 圈子ID
     * @param userList 人员列表
     * @param type 操作类型 -> CODE:OPERATE/ADD_CIRCLE/ADD_PROJECT
     * @return .
     */
    @Override
    public boolean save(String circleId, List<String> userList, String type) {
        List<CircleUser> circleUserList = new ArrayList<>();
        userList.forEach(userId ->
            this.saveCircleUser(userId, circleId, type, circleUserList));
        this.saveBatch(circleUserList);
        return true;
    }

    private void saveCircleUser(String userId, String circleId, String type, List<CircleUser> circleUserList){
        CircleUser circleUser = new CircleUser();
        circleUser.setSysUserId(userId);
        circleUser.setCircleId(circleId);
        circleUser.setType(type);
        circleUserList.add(circleUser);
    }

    /**
     * 查找当前权限下的用户信息
     * @param circleId 圈子ID
     * @param type 操作类型 -> CODE:OPERATE/ADD_CIRCLE/ADD_PROJECT
     * @return .
     */
    @Override
    public List<SysUser> findByCircleIdAndType(String circleId, String type) {
        List<CircleUser> circleUserList = new LambdaQueryChainWrapper<CircleUser>(circleUserDao).eq(CircleUser::getCircleId, circleId)
                .eq(CircleUser::getType, type).list();
        if (CollectionUtils.isEmpty(circleUserList)) return null;
        List<SysUser> sysUserList = new ArrayList<>();
        circleUserList.forEach(circleUser -> {
            SysUser sysUser = sysUserDao.findByUserId(circleUser.getSysUserId());
            if (sysUser != null) sysUserList.add(sysUser);
        });
        return sysUserList;
    }

    /**
     * 删除操作圈子与人员关系
     * @param circleId 圈子ID
     * @param type 操作类型 -> CODE:OPERATE/ADD_CIRCLE/ADD_PROJECT
     * @return .
     */
    @Override
    public boolean delete(String circleId, String type) {
        LambdaQueryWrapper<CircleUser> queryWrapper = new LambdaQueryWrapper<CircleUser>().eq(CircleUser::getCircleId, circleId).eq(CircleUser::getType, type);
        circleUserDao.delete(queryWrapper);
        return true;
    }

    /**
     * 圈子操作权限人员是否更改
     * @param circleId 圈子ID
     * @param currentSysUserIds 人员列表
     * @param type 操作类型 -> CODE:OPERATE/ADD_CIRCLE/ADD_PROJECT
     */
    @Override
    public void compareCircleUser(String circleId, List<String> currentSysUserIds, String type) {
        //查找之前保存的人员
        List<CircleUser> circleUserList = new LambdaQueryChainWrapper<CircleUser>(circleUserDao).eq(CircleUser::getCircleId, circleId).eq(CircleUser::getType, type).list();
        List<String> lastSysUserIds = circleUserList.stream().map(CircleUser::getSysUserId).collect(Collectors.toList());
        //当前员工ID
//        List<String> currentSysUserIds = currentUserList.stream().map(SysUser::getId).collect(Collectors.toList());
        //比较是否相等
        lastSysUserIds.sort(Comparator.comparing(String::hashCode));
        currentSysUserIds.sort(Comparator.comparing(String::hashCode));
        if (lastSysUserIds.toString().equals(currentSysUserIds.toString())) return;

        //比较人员ID，删除之前多余的
        List<String> deleteUserIds = lastSysUserIds.stream().filter(item -> !currentSysUserIds.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteUserIds)){
            deleteUserIds.forEach(userId -> {
                LambdaQueryWrapper<CircleUser> queryWrapper = new LambdaQueryWrapper<CircleUser>().eq(CircleUser::getCircleId, circleId).eq(CircleUser::getType, type).eq(CircleUser::getSysUserId, userId);
                circleUserDao.delete(queryWrapper);
            });
        }

        //添加新增的人员ID
        List<String> insertUserIds = currentSysUserIds.stream().filter(item -> !lastSysUserIds.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertUserIds)){
            List<CircleUser> circleUsers = new ArrayList<>();
            insertUserIds.forEach(userId -> this.saveCircleUser(userId, circleId, type, circleUsers));
            this.saveBatch(circleUsers);
        }
    }
}
