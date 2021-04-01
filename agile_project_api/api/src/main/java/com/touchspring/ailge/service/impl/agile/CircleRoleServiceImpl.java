package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.CircleLogDao;
import com.touchspring.ailge.dao.agile.CircleRoleDao;
import com.touchspring.ailge.dao.agile.CircleRoleUserDao;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.ailge.entity.agile.CircleRoleUser;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.enums.LogTypeEnum;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.CircleLogService;
import com.touchspring.ailge.service.agile.CircleRoleService;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 圈子角色关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class CircleRoleServiceImpl extends ServiceImpl<CircleRoleDao, CircleRole> implements CircleRoleService {

    @Autowired
    private CircleRoleDao circleRoleDao;
    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private CircleRoleUserDao circleRoleUserDao;
    @Autowired
    private BeanChangeUtilService<CircleRole> beanChangeUtilService;
    @Autowired
    private CircleLogDao circleLogDao;
    @Autowired
    private CircleLogService circleLogService;

    /**
     * 保存圈子与角色名称关系
     * @param circleId 圈子ID
     * @param roleNames 角色
     * @return .
     */
    @Override
    public boolean save(String circleId, List<String> roleNames) {
        if (CollectionUtils.isEmpty(roleNames)) return false;
        List<CircleRole> circleRoleList = new ArrayList<>();
        roleNames.forEach(roleName ->
            this.saveCircleRole(circleId, roleName, circleRoleList));
        this.saveBatch(circleRoleList);
        return true;
    }

    private void saveCircleRole(String circleId, String roleName, List<CircleRole> circleRoleList){
        List<SysRoleTemplate> sysRoleTemplates = new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).eq(SysRoleTemplate::getName, roleName).list();
        if (CollectionUtils.isEmpty(sysRoleTemplates)){
            CircleRole circleRole = new CircleRole();
            circleRole.setCircleId(circleId);
            circleRole.setRoleName(roleName);
            circleRoleList.add(circleRole);
        } else {
            sysRoleTemplates.forEach(roleTemplate -> {
                CircleRole circleRole = new CircleRole();
                circleRole.setCircleId(circleId);
                circleRole.setRoleName(roleName);
                circleRole.setRoleDescription(roleTemplate.getDescription());
                circleRole.setDuty(roleTemplate.getDuty());
                circleRoleList.add(circleRole);
            });
        }
    }

    /**
     * 圈子角色是否修改
     */
    @Override
    public void compareCircleRole(String circleId, List<String> currentRoleNames, String curUserId) {
        //查找之前绑定的角色名称
        List<CircleRole> circleRoles = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(CircleRole::getCircleId, circleId).list();
        List<String> lastRoleNames = circleRoles.stream().map(CircleRole::getRoleName).collect(Collectors.toList());
        //当前角色名称
//        List<String> currentRoleNames = currentRoleList.stream().map(SysRoleTemplate::getName).collect(Collectors.toList());
        //比较是否相等
        lastRoleNames.sort(Comparator.comparing(String::hashCode));
        currentRoleNames.sort(Comparator.comparing(String::hashCode));
        if (lastRoleNames.toString().equals(currentRoleNames.toString())) return;

        //比较名称，删除当前名称中没有的角色
        List<String> deleteNames = lastRoleNames.stream().filter(item -> !currentRoleNames.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteNames)){
            deleteNames.forEach(name -> {
                LambdaQueryWrapper<CircleRole> queryWrapper = new LambdaQueryWrapper<CircleRole>().eq(CircleRole::getCircleId, circleId).eq(CircleRole::getRoleName, name);
                circleRoleDao.delete(queryWrapper);
            });
            //log
            circleLogService.saveRoleChangeToLog(deleteNames, curUserId, LogTypeEnum.DELETE.getMsg(), circleId);
        }

        //添加新增的角色
        List<String> insertNames = currentRoleNames.stream().filter(item -> !lastRoleNames.contains(item)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertNames)){
            List<CircleRole> circleRoleList = new ArrayList<>();
            insertNames.forEach(name -> this.saveCircleRole(circleId, name, circleRoleList));
            this.saveBatch(circleRoleList);
            //log
            circleLogService.saveRoleChangeToLog(insertNames, curUserId, LogTypeEnum.INSERT.getMsg(), circleId);
        }
    }

    /**
     * 查找圈子下的角色名称
     * @param circleId 圈子ID
     * @return .
     */
    @Override
    public List<CircleRole> findByCircleId(String circleId) {
        return new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(CircleRole::getCircleId, circleId).list();
    }

    /**
     * 批量删除圈子角色和关联表
     * @param ids 圈子角色IDs
     * @return .
     */
    @Override
    public boolean batchDelete(String[] ids, String userId) {
        if (ArrayUtils.isEmpty(ids)) return true;
        //删除关联表 圈子角色成员
//        Arrays.asList(ids).forEach(circleRoleId -> {
//            LambdaQueryWrapper<CircleRoleUser> queryWrapper = new LambdaQueryWrapper<CircleRoleUser>().eq(CircleRoleUser::getCircleRoleId, circleRoleId);
//            circleRoleUserDao.delete(queryWrapper);
//        });
        circleRoleUserDao.delete(new LambdaQueryWrapper<CircleRoleUser>().in(CircleRoleUser::getCircleRoleId, Arrays.asList(ids)));
        List<CircleRole> circleRoleList = circleRoleDao.selectBatchIds(Arrays.asList(ids));
        List<String> roleNames = circleRoleList.stream().map(CircleRole::getRoleName).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roleNames))
            circleLogService.saveRoleChangeToLog(roleNames, userId, LogTypeEnum.DELETE.getMsg(), circleRoleList.get(0).getCircleId());
        //删除当前表
        circleRoleDao.deleteBatchIds(Arrays.asList(ids));
        return true;
    }

    @Override
    public ResultData insert(CircleRole circleRole, String userId) {
        if (StringUtils.isBlank(circleRole.getRoleName())) return new ResultData(ResultStatus.ROLE_NAME_IS_NOT_EMPTY.getCode(), ResultStatus.ROLE_NAME_IS_NOT_EMPTY.getMessage());
        CircleRole target;
        if (StringUtils.isBlank(circleRole.getId())) {
            target = circleRole;
            List<CircleRole> circleRoleList = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao)
                    .eq(CircleRole::getRoleName, target.getRoleName())
                    .eq(CircleRole::getCircleId, circleRole.getCircleId()).list();
            if (!CollectionUtils.isEmpty(circleRoleList)) return new ResultData(ResultStatus.ROLE_IS_EXIST.getCode(), ResultStatus.ROLE_IS_EXIST.getMessage());
            circleRoleDao.insert(target);
            List<String> roleNames = new ArrayList<>();
            roleNames.add(target.getRoleName());
            circleLogService.saveRoleChangeToLog(roleNames, userId, LogTypeEnum.INSERT.getMsg(), target.getCircleId());
        } else {
            target = circleRoleDao.selectById(circleRole.getId());
            if (!target.getRoleName().equals(circleRole.getRoleName())) {
                List<CircleRole> circleRoleList = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao)
                        .eq(CircleRole::getRoleName, circleRole.getRoleName())
                        .eq(CircleRole::getCircleId, circleRole.getCircleId()).list();
                if (!CollectionUtils.isEmpty(circleRoleList)) return new ResultData(ResultStatus.ROLE_IS_EXIST.getCode(), ResultStatus.ROLE_IS_EXIST.getMessage());
            }
            circleRoleDao.updateById(circleRole);
            String changeLog = beanChangeUtilService.contrastObj(target, circleRole);
            if (StringUtils.isNotBlank(changeLog)){
                CircleLog circleLog = new CircleLog();
                circleLog.setContent(changeLog);
                circleLog.setCreateUserId(userId);
                circleLog.setCircleId(circleRole.getCircleId());
                circleLogDao.insert(circleLog);
            }
        }
        return ResultData.ok();
    }



}
