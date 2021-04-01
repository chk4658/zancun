package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.CircleDao;
import com.touchspring.ailge.dao.agile.CircleLogDao;
import com.touchspring.ailge.dao.agile.CircleRoleDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.Circle;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.ailge.entity.agile.CircleRoleUser;
import com.touchspring.ailge.dao.agile.CircleRoleUserDao;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.MessageModuleEnum;
import com.touchspring.ailge.service.agile.CircleRoleUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.service.agile.CircleService;
import com.touchspring.ailge.service.agile.MessageUserService;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.ailge.utils.PlaceholderUtil;
import com.touchspring.ailge.utils.PropertyMsg;
import com.touchspring.core.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * <p>
 * 圈子角色与人员关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@Service
public class CircleRoleUserServiceImpl extends ServiceImpl<CircleRoleUserDao, CircleRoleUser> implements CircleRoleUserService {

    @Autowired
    private CircleRoleUserDao circleRoleUserDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private CircleDao circleDao;
    @Autowired
    private CircleRoleDao circleRoleDao;
    @Autowired
    private CircleLogDao circleLogDao;
    @Autowired
    private MessageUserService messageUserService;

    @Override
    public Page<SysUser> findByCircleRoleId(Integer page, Integer size, String circleRoleId) {
        Page<SysUser> sysUserPage = new Page<>(page, size);
        return sysUserPage.setRecords(sysUserDao.findByCircleRoleId(sysUserPage, circleRoleId));
    }

    @Override
    public boolean delete(String circleRoleId, String userId) {
        LambdaQueryWrapper<CircleRoleUser> queryWrapper = new LambdaQueryWrapper<CircleRoleUser>().eq(CircleRoleUser::getCircleRoleId, circleRoleId).eq(CircleRoleUser::getSysUserId, userId);
        circleRoleUserDao.delete(queryWrapper);
        return true;
    }

    @Override
    public boolean batchDelete(String circleRoleId, String[] userIds) {
        Arrays.asList(userIds).forEach(userId -> this.delete(circleRoleId, userId));
        return true;
    }

    @Override
    public boolean save(String circleRoleId, String[] userIds, String createUserId) {
//        circleRoleUserDao.delete(new LambdaQueryWrapper<CircleRoleUser>().eq(CircleRoleUser::getCircleRoleId,circleRoleId));
        //获取上次的用户
        CircleRoleUser lastCircleRoleUser = new LambdaQueryChainWrapper<CircleRoleUser>(circleRoleUserDao).eq(CircleRoleUser::getCircleRoleId, circleRoleId).one();
        circleRoleUserDao.deleteByCircleRoleId(circleRoleId);

        List<CircleRoleUser> circleRoleUsers = new ArrayList<>();
        Arrays.asList(userIds).forEach(userId -> {
            CircleRoleUser circleRoleUser = new CircleRoleUser();
            circleRoleUser.setCircleRoleId(circleRoleId);
            circleRoleUser.setSysUserId(userId);
            circleRoleUsers.add(circleRoleUser);
        });
        this.saveBatch(circleRoleUsers);

        // 圈子角色变更，消息给（圈子角色人员、圈长）
        CircleRole circleRole = circleRoleDao.selectById(circleRoleId);
        Circle circle = circleDao.selectById(circleRole.getCircleId());
        List<String> receiveUserIds = new ArrayList<>();
        receiveUserIds.add(circle.getOwnerUid());

        if (lastCircleRoleUser == null && ArrayUtils.isNotEmpty(userIds))
            receiveUserIds.add(userIds[0]);
        else if (lastCircleRoleUser != null && ArrayUtils.isEmpty(userIds))
            receiveUserIds.add(lastCircleRoleUser.getSysUserId());
        else if (lastCircleRoleUser != null && !StringUtils.equals(lastCircleRoleUser.getSysUserId(), userIds[0])){
            receiveUserIds.add(lastCircleRoleUser.getSysUserId());
            receiveUserIds.add(userIds[0]);
        }

        if (receiveUserIds.size() > 1) {
            String curUserName = ArrayUtils.isNotEmpty(userIds) ? sysUserDao.selectById(userIds[0]).getRealName() : "";
            Map<String,String> messageExt = new HashMap<>();
            messageExt.put("userName", sysUserDao.selectById(createUserId).getRealName());
            messageExt.put("roleName", circleRole.getRoleName());
            messageExt.put("circleName", circle.getName());
            messageUserService.saveMessageToUser(MessageModuleEnum.CIRCLE_ROLE_CHANGE.getType(),
                    circleRole.getCircleId(),
                    PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.CIRCLE_ROLE_CHANGE.getMsg(), messageExt),
                    receiveUserIds.toArray(new String[0]), null, circle.getName(), circleRole.getRoleName(), curUserName);
        }

        return true;
    }

    /**
     * 查找当前圈子及子圈的所有角色人数
     * @param parentCircleId 当前圈子ID
     * @return .
     */
    @Override
    public Integer[] allCircleNum(String parentCircleId) {
        List<String> circleIds = new ArrayList<>();
        //当前圈子ID
        circleIds.add(parentCircleId);
        //获取所有子圈ID
        this.findCircleIds(parentCircleId, circleIds, false);
        //当前圈子下的人员
        Set<String> curUserIds = new HashSet<>();
        //获取所有的角色下的成员
        Set<String> allUserIds = new HashSet<>();
        for (String circleId: circleIds) {
            List<CircleRole> circleRoleList = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(CircleRole::getCircleId, circleId).select(CircleRole::getId).list();
            if (CollectionUtils.isEmpty(circleRoleList)) continue;
            circleRoleList.forEach(circleRole -> {
                List<CircleRoleUser> circleRoleUserList = new LambdaQueryChainWrapper<CircleRoleUser>(circleRoleUserDao).eq(CircleRoleUser::getCircleRoleId, circleRole.getId()).select(CircleRoleUser::getSysUserId).list();
                if (!CollectionUtils.isEmpty(circleRoleUserList))
                    circleRoleUserList.forEach(circleRoleUser -> {
                        allUserIds.add(circleRoleUser.getSysUserId());
                        if (StringUtils.equals(parentCircleId, circleId))
                            curUserIds.add(circleRoleUser.getSysUserId());
                    });
            });
        }
        return new Integer[]{curUserIds.size(), allUserIds.size()};
    }

    /**
     * 在指定角色名称下添加或更新人员
     * @param circleId 圈子ID
     * @param roleName 角色名称
     */
    @Override
    public void insertOrUpdateUserByCircleRole(String circleId, String roleName, String userId) {
        CircleRole circleRole = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(CircleRole::getCircleId, circleId)
                .eq(CircleRole::getRoleName, roleName).select(BaseEntity::getId).one();
        if (circleRole == null) return;
        CircleRoleUser circleRoleUser = new LambdaQueryChainWrapper<CircleRoleUser>(circleRoleUserDao).eq(CircleRoleUser::getCircleRoleId, circleRole.getId()).one();
        if (circleRoleUser == null) {
            circleRoleUser = new CircleRoleUser();
            circleRoleUser.setCircleRoleId(circleRole.getId());
            circleRoleUser.setSysUserId(userId);
            circleRoleUserDao.insert(circleRoleUser);
        } else {
            circleRoleUser.setSysUserId(userId);
            circleRoleUserDao.updateById(circleRoleUser);
        }
    }

    /**
     * 获取子圈ID
     * @param parentId 父圈ID
     * @param ids 子圈ID列表
     */
    public List<Circle> findCircleIds(String parentId, List<String> ids, boolean flag) {
        List<Circle> circles = new LambdaQueryChainWrapper<Circle>(circleDao).eq(Circle::getParentId, parentId).list();
        List<Circle> childCircles = new ArrayList<>();
        if (flag) childCircles.addAll(circles);
        for (Circle circle : circles) {
            this.findCircleIds(circle.getId(), ids, false);
            ids.add(circle.getId());
        }
        return childCircles;
    }

}
