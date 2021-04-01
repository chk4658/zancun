package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dto.CircleDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.enums.ChangeEnum;
import com.touchspring.ailge.enums.CircleOperationType;
import com.touchspring.ailge.enums.LogTypeEnum;
import com.touchspring.ailge.service.agile.*;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.ailge.utils.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
@Transactional
public class CircleServiceImpl extends ServiceImpl<CircleDao, Circle> implements CircleService {

    @Autowired
    private CircleDao circleDao;
    @Autowired
    private CircleRoleService circleRoleService;
    @Autowired
    private CircleUserService circleUserService;
    @Autowired
    private CircleRoleDao circleRoleDao;
    @Autowired
    private CircleUserDao circleUserDao;
    @Autowired
    private CircleRoleUserDao circleRoleUserDao;
    @Autowired
    private CircleRoleUserServiceImpl circleRoleUserService;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private CircleLogDao circleLogDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private BeanChangeUtilService<Circle> beanChangeUtilService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private ProjectRoleUserService projectRoleUserService;
    @Autowired
    private CircleLogService circleLogService;

    /**
     * 获取圈子及子圈 父圈ID：最上层：null 其他保存圈子ID
     */
    @Override
    public List<Circle> findAll(String userId) {
        //获取父圈子
        List<Circle> circles = circleDao.findAll();
        List<Circle> _circles = this.createCircleTree(circles,userId);
        return _circles;
    }


    private List<Circle> createCircleTree(List<Circle> treeNodes,String userId){
        List<Circle> treeNodeList = new ArrayList<>();
        for (Circle treeNode : treeNodes) {
            treeNode.setOperation(this.getOperation(treeNode,userId));
            if ( StringUtils.isBlank(treeNode.getParentId())){
                treeNodeList.add(addChildNode(treeNode, treeNodes,userId));
            }
        }
        return treeNodeList;
    }

    private Circle addChildNode(Circle treeNode, List<Circle> treeNodes,String userId){

        List<Circle> treeNodeList = new ArrayList<>();
        for (Circle node : treeNodes) {
            treeNode.setOperation(this.getOperation(treeNode,userId));
            if (StringUtils.isNotBlank(node.getParentId()) && node.getParentId().equals(treeNode.getId())){
                treeNodeList.add(addChildNode(node, treeNodes,userId));
            }
        }
        treeNode.setChildren(treeNodeList);
        return treeNode;
    }

    /**
     * 圈子 新增
     */
    @Override
    public boolean save(CircleDTO currentCircleDTO, String curUserId) {
        Circle target;
        if (StringUtils.isBlank(currentCircleDTO.getId())) {
            target = currentCircleDTO;
            //创建人
            target.setCreateUserId(curUserId);
            target.setUpdateUserId(curUserId);
            circleDao.insert(target);
            //角色
            List<String> roleNames = currentCircleDTO.getRoleNames();
            roleNames.add(CircleOperationType.CIRCLE_OWNER);
            circleRoleService.save(target.getId(), new ArrayList<String>(new LinkedHashSet<String>(roleNames)));
            // 角色添加Log
            circleLogService.saveRoleChangeToLog(new ArrayList<String>(new LinkedHashSet<String>(roleNames)), curUserId, LogTypeEnum.INSERT.getMsg(), target.getId());

            //默认添加圈主对应的角色人员信息
            circleRoleUserService.insertOrUpdateUserByCircleRole(target.getId(), CircleOperationType.CIRCLE_OWNER, target.getOwnerUid());

            //允许操作圈子人员
            this.saveCircleUser(target.getIsOperate(), currentCircleDTO.getOperateIds(), target.getId(), CircleOperationType.CircleOperationTypeEnums.OPERATE.getCode(), target.getOwnerUid(), curUserId);
            //允许添加子圈人员
            this.saveCircleUser(target.getIsAddCircle(), currentCircleDTO.getAddCircleIds(), target.getId(), CircleOperationType.CircleOperationTypeEnums.ADD_CIRCLE.getCode(), target.getOwnerUid(), curUserId);
            //允许所有人添加项目人员
            this.saveCircleUser(target.getIsAddProject(), currentCircleDTO.getAddProjectIds(), target.getId(), CircleOperationType.CircleOperationTypeEnums.ADD_PROJECT.getCode(), target.getOwnerUid(), curUserId);

            //子圈创建Log
            if (StringUtils.isNotBlank(target.getParentId()) && !target.getParentId().equals("0"))
                circleLogService.saveChildCircleChange(LogTypeEnum.INSERT.getMsg(), target.getName(), curUserId, target.getParentId());
        } else {
            target = circleDao.selectById(currentCircleDTO.getId());
            currentCircleDTO.setUpdateUserId(curUserId);
            if (currentCircleDTO.getParentId().equals("0"))
                currentCircleDTO.setParentId(null);
            circleDao.updateById(currentCircleDTO);

            //角色是否变更
            List<String> roleNames = currentCircleDTO.getRoleNames();
            roleNames.add(CircleOperationType.CIRCLE_OWNER);
            circleRoleService.compareCircleRole(currentCircleDTO.getId(), new ArrayList<String>(new LinkedHashSet<String>(roleNames)), curUserId);

            //默认添加圈主对应的角色人员信息
            circleRoleUserService.insertOrUpdateUserByCircleRole(currentCircleDTO.getId(), CircleOperationType.CIRCLE_OWNER, currentCircleDTO.getOwnerUid());

            //操作圈子人员权限是否变更
            this.judgeAuthorityChange(target.getIsOperate(), currentCircleDTO.getIsOperate(), currentCircleDTO.getOperateIds(), currentCircleDTO.getId(), CircleOperationType.CircleOperationTypeEnums.OPERATE.getCode(), currentCircleDTO.getOwnerUid(), target.getCreateUserId());
            //添加子圈人员权限是否变更
            this.judgeAuthorityChange(target.getIsAddCircle(), currentCircleDTO.getIsAddCircle(), currentCircleDTO.getAddCircleIds(), currentCircleDTO.getId(), CircleOperationType.CircleOperationTypeEnums.ADD_CIRCLE.getCode(), currentCircleDTO.getOwnerUid(), target.getCreateUserId());
            //所有人添加项目人员权限是否变更
            this.judgeAuthorityChange(target.getIsAddProject(), currentCircleDTO.getIsAddProject(), currentCircleDTO.getAddProjectIds(), currentCircleDTO.getId(), CircleOperationType.CircleOperationTypeEnums.ADD_PROJECT.getCode(), currentCircleDTO.getOwnerUid(), target.getCreateUserId());

            // 圈子修改圈长，则需要更新当前圈子下的项目中，圈长对应的用户
            if (!target.getOwnerUid().equals(currentCircleDTO.getOwnerUid())){
                List<Project> projectList = new LambdaQueryChainWrapper<Project>(projectDao).eq(Project::getCircleId, target.getId()).select(BaseEntity::getId).list();
                if (!CollectionUtils.isEmpty(projectList)){
                    List<String> projectIds = projectList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                    List<ProjectRole> projectRoleList = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).in(ProjectRole::getProjectId, projectIds)
                            .eq(ProjectRole::getRoleName, CircleOperationType.CIRCLE_OWNER).select(BaseEntity::getId).list();
                    List<String> projectRoleIds = projectRoleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(projectRoleIds)){
                        List<ProjectRoleUser> projectRoleUsers = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).in(ProjectRoleUser::getProjectRoleId, projectRoleIds).list();
                        projectRoleUsers = projectRoleUsers.stream().peek(projectRoleUser -> projectRoleUser.setSysUserId(currentCircleDTO.getOwnerUid())).collect(Collectors.toList());
                        projectRoleUserService.updateBatchById(projectRoleUsers);
                    }
                }
            }

            //圈子名称是否更改
            if (!StringUtils.equals(target.getName(), currentCircleDTO.getName())) {
                Map<String, String> messageExt = new HashMap<>();
                messageExt.put("type", "圈子名称");
                messageExt.put("lastValue", target.getName());
                messageExt.put("curValue", currentCircleDTO.getName());
                circleLogService.saveCircleLog(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange, messageExt), target.getId(), curUserId);
            }

        }
        return true;
    }

    /**
     * 判断操作人员权限是否变更
     * @param lastIsOperation 历史操作范围
     * @param currentIsOperation 当前操作范围
     * @param userList 操作人员
     * @param circleId 圈子ID
     * @param type 操作类型
     */
    private void judgeAuthorityChange(Integer lastIsOperation, Integer currentIsOperation, List<String> userList, String circleId, String type, String ownerUId, String createUserId){
        //之前是all，现在不是all save
        if (lastIsOperation.equals(CircleOperationType.ALL) && !lastIsOperation.equals(currentIsOperation)){
            List<String> allUserIdList = new ArrayList<>();
            //圈长ID
            allUserIdList.add(ownerUId);
            //创建人ID
            if (StringUtils.isNotBlank(createUserId))
                allUserIdList.add(createUserId);
            if (!CollectionUtils.isEmpty(userList))
                allUserIdList.addAll(userList);
            circleUserService.save(circleId, new ArrayList<String>(new LinkedHashSet<String>(allUserIdList)), type);
        }
        //之前不是all，现在是all delete
        else if (currentIsOperation.equals(CircleOperationType.ALL) && !lastIsOperation.equals(currentIsOperation))
            circleUserService.delete(circleId, type);
        //之前不是all，现在不是all update
        else if (!lastIsOperation.equals(CircleOperationType.ALL) && !currentIsOperation.equals(CircleOperationType.ALL)){
            List<String> allUserIdList = new ArrayList<>();
            //圈长ID
            allUserIdList.add(ownerUId);
            //创建人ID
            if (StringUtils.isNotBlank(createUserId))
                allUserIdList.add(createUserId);
            if (!CollectionUtils.isEmpty(userList))
                allUserIdList.addAll(userList);
            circleUserService.compareCircleUser(circleId, new ArrayList<String>(new LinkedHashSet<String>(allUserIdList)), type);
        }
    }

    /**
     * 保存 圈子与人员
     * @param isOperation 操作范围
     * @param userList 操作人员
     * @param type 操作类型
     * @param ownerUId 圈长ID
     */
    private void saveCircleUser(Integer isOperation, List<String> userList, String circleId, String type, String ownerUId, String curUserId){
//        if (isOperation.equals(CircleOperationType.PART) && !CollectionUtils.isEmpty(userList)) {
//            userList.add(ownerUId);
//            circleUserService.save(circleId, new ArrayList<String>(new LinkedHashSet<String>(userList)), type);
//        }
        if (isOperation.equals(CircleOperationType.ALL)) return;
        List<String> allUserIdList = new ArrayList<>();
        //圈长ID
        allUserIdList.add(ownerUId);
        //创建人ID
        allUserIdList.add(curUserId);
        if (!CollectionUtils.isEmpty(userList))
            allUserIdList.addAll(userList);
        circleUserService.save(circleId, new ArrayList<String>(new LinkedHashSet<String>(allUserIdList)), type);
    }

    /**
     * 获取子圈
     */
    private List<Circle> findChildren(String parentId,String userId) {
        List<Circle> circles = this.findByParentId(parentId);
        for (Circle circle : circles) {
            circle.setOperation(this.getOperation(circle,userId));
            List<Circle> children = this.findChildren(circle.getId(),userId);
            circle.setChildren(children);
        }
        return circles;
    }

    /**
     * 获取父圈集合 非数状
     */
    private void findParents(String parentId,List<Circle> circles) {
        Circle circle = circleDao.selectById(parentId);
        if (circle == null) return;
        circles.add(circle);
        if (StringUtils.isNotBlank(circle.getParentId()) && !circle.getParentId().equals("0"))
            this.findParents(circle.getParentId(),circles);
//        return circles;
    }

    @Override
    public List<Circle> findByName(String name) {
        List<Circle> parentList = circleDao.findParentList(name);
        parentList.addAll(circleDao.findAllChildCircleIdAndSearchName(name));
        return parentList;
    }

    @Override
    public List<Circle> findByParentId(String parentId) {
        return new LambdaQueryChainWrapper<Circle>(circleDao).eq(Circle::getParentId, parentId).orderByAsc(BaseEntity::getCreateAt).list();
    }

    /**
     * 删除圈子及关联表
     * @param circleId
     * @return .
     */
    @Override
    public boolean delete(String circleId, String userId) {

        //获取圈子及子圈ID
        List<String> circleIds = new ArrayList<>();
        circleIds.add(circleId);
        this.getChildCircleIds(circleId, circleIds);


        //删除关联表：圈子角色 圈子角色成员
        List<CircleRole> circleRoleList = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).in(CircleRole::getCircleId, circleIds).select(CircleRole::getId).list();
        if (!CollectionUtils.isEmpty(circleRoleList)) {
            List<String> circleRoleIds = circleRoleList.stream().map(CircleRole::getId).collect(Collectors.toList());
            circleRoleService.batchDelete(circleRoleIds.toArray(new String[circleRoleIds.size()]), userId);
        }
        //删除关联表：圈子成员
        circleUserDao.delete(new LambdaQueryWrapper<CircleUser>().in(CircleUser::getCircleId, circleIds));

        //删除项目
        List<Project> projects = new LambdaQueryChainWrapper<Project>(projectDao).in(Project::getCircleId, circleIds).select(BaseEntity::getId).list();
        if (!CollectionUtils.isEmpty(projects)){
            List<String> projectIds = projects.stream().map(BaseEntity::getId).collect(Collectors.toList());
            projectIds.forEach(projectId -> projectService.deleteById(projectId, userId));
        }

        //删除Log
        circleLogDao.delete(new LambdaQueryWrapper<CircleLog>().in(CircleLog::getCircleId, circleIds));

        //子圈删除Log
        Circle circle = circleDao.selectById(circleId);
        if (StringUtils.isNotBlank(circle.getParentId()) && !circle.getParentId().equals("0"))
            circleLogService.saveChildCircleChange(LogTypeEnum.DELETE.getMsg(), circle.getName(), userId, circle.getParentId());

        //圈子
        circleDao.deleteBatchIds(circleIds);
        return true;
    }

    /**
     * 获取关联子菜单
     */
    private void getChildCircleIds(String id,List<String> circleIds){
        List<Circle> circles = new LambdaQueryChainWrapper<Circle>(circleDao).eq(Circle::getParentId, id).list();
        for (Circle circle: circles) {
            circleIds.add(circle.getId());
            List<Circle> circles1 = new LambdaQueryChainWrapper<Circle>(circleDao).eq(Circle::getParentId, circle.getId()).list();
            if(CollectionUtils.isEmpty(circles1)) continue;
            this.getChildCircleIds(circle.getId(), circleIds);
        }
    }

    /**
     * 获取我的圈子 -----》 项目角色人对应的直属圈子
     * @param sysUser 当前用户信息
     * @return .
     */
    @Override
    public List<Circle> getMyCircles(SysUser sysUser, String searchName) {
//        Set<String> circleIds = new HashSet<>();
//        List<CircleRoleUser> circleRoleUsers = new LambdaQueryChainWrapper<CircleRoleUser>(circleRoleUserDao).eq(CircleRoleUser::getSysUserId, sysUser.getId()).select(CircleRoleUser::getCircleRoleId).list();
//        if (CollectionUtils.isEmpty(circleRoleUsers)) return new ArrayList<Circle>();
//        new ArrayList<CircleRoleUser>(new LinkedHashSet<CircleRoleUser>(circleRoleUsers)).forEach(circleRoleUser -> {
//            List<CircleRole> circleRoles = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(BaseEntity::getId, circleRoleUser.getCircleRoleId()).select(CircleRole::getCircleId).list();
//            if (!CollectionUtils.isEmpty(circleRoles)){
//                circleRoles.forEach(circleRole -> circleIds.add(circleRole.getCircleId()));
//            }
//        });

        List<Circle> circles = new ArrayList<>();
        List<ProjectRoleUser> projectRoleUserList = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getSysUserId, sysUser.getId()).select(ProjectRoleUser::getProjectRoleId).list();
        if (CollectionUtils.isEmpty(projectRoleUserList)) return circles;
        List<String> projectRoleIds = projectRoleUserList.stream().map(ProjectRoleUser::getProjectRoleId).distinct().collect(Collectors.toList());
        List<ProjectRole> projectRoleList = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).in(BaseEntity::getId, projectRoleIds).select(ProjectRole::getProjectId).list();
        if (CollectionUtils.isEmpty(projectRoleList)) return circles;
        List<String> projectIds = projectRoleList.stream().map(ProjectRole::getProjectId).distinct().collect(Collectors.toList());
        //获取上线的项目
        List<Project> projectList = new LambdaQueryChainWrapper<Project>(projectDao).in(BaseEntity::getId, projectIds).eq(Project::getHasOnLine, BaseEnums.YES.getCode()).select(Project::getCircleId).list();
        if (CollectionUtils.isEmpty(projectList)) return circles;
        Set<String> circleIds = projectList.stream().filter(project -> StringUtils.isNotBlank(project.getCircleId())).map(Project::getCircleId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(circleIds)) return circles;
        circles = circleDao.findByCircleIdsAndName(circleIds, searchName);
        return circles;
    }

    /**
     * 根据id获取所有上级圈子集合
     * 非树状结构 不包含本身
     * @param circleId
     * @return
     */
    @Override
    public List<Circle> findParentsById(String circleId) {
        List<Circle> circles = new ArrayList<>();
        Circle circle = circleDao.selectById(circleId);
        if (circle == null || StringUtils.isBlank(circle.getParentId()) || circle.getParentId().equals("0")) return circles;
        this.findParents(circle.getParentId(),circles);
        return circles;
    }

    /**
     * 当前圈子下的项目 & 所有子圈的项目
     * @param parentCircleId 当前圈子ID
     * @return .
     */
    @Override
    public Integer[] projectNum(String parentCircleId) {

        List<String> circleIds = new ArrayList<>();
        //当前圈子ID
        circleIds.add(parentCircleId);
        //获取所有子圈ID
        List<Circle> childCircles = circleRoleUserService.findCircleIds(parentCircleId, circleIds, true);
        //当前圈子下的人员
        Set<String> curProjectIds = new HashSet<>();
        //获取所有的角色下的成员
        Set<String> allProjectIds = new HashSet<>();
        for (String circleId: circleIds) {
            List<Project> projectList = new LambdaQueryChainWrapper<Project>(projectDao).eq(Project::getCircleId, circleId).select(BaseEntity::getId).list();
            if (CollectionUtils.isEmpty(projectList)) continue;
            Set<String> projectIds = projectList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            allProjectIds.addAll(projectIds);
            if (StringUtils.equals(circleId, parentCircleId))
                curProjectIds.addAll(projectIds);
        }

        return new Integer[]{curProjectIds.size(), allProjectIds.size(), childCircles.size(), circleIds.size() - 1};
    }

    @Override
    public List<Circle> findByCircleIdAndSearchName(String parentId, String searchName) {
        List<Circle> childrenCircles = circleDao.findAllByCircleIdAndSearchName(parentId, searchName);
        if (!CollectionUtils.isEmpty(childrenCircles)) {
            childrenCircles = childrenCircles.stream().peek(circle -> {
                Integer[] projectNum = this.projectNum(circle.getId());
                circle.setAllProjectNum(projectNum[1]);
                circle.setAllChildrenCircleNum(projectNum[2]);
            }).collect(Collectors.toList());
        }
        return childrenCircles;
    }

    /**
     * 树结构
     * @param circles 圈子
     * @return
     */
    private List<Circle> findTreeMyCircles(List<Circle> circles){
        List<Circle> circleTreeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(circles)) {
            for (Circle circle : circles){
                if (com.touchspring.core.utils.StringUtils.isBlank(circle.getParentId())) {
                    circleTreeList.add(circle);
                }
                for (Circle c : circles){
                    if (c.getParentId().equals(circle.getId())) {
                        if (circle.getChildren() == null) {
                            List<Circle> myChildrenList = new ArrayList<>();
                            myChildrenList.add(c);
                            circle.setChildren(myChildrenList);
                        } else
                            circle.getChildren().add(c);
                    }
                }
            }
        }
        return circleTreeList;
    }



    /**
     * 获取用户圈子权限
     * @param userId
     * @return
     */
    @Override
    public CircleOperationType.UserOperation getCircleRoleOperation(String userId) {
        // 获取
        CircleOperationType.UserOperation userOperation = new CircleOperationType.UserOperation();
        userOperation.setUserId(userId);
        List<CircleOperationType.UserOperation.Operation> operations = new ArrayList<>();
        userOperation.setOperations(operations);
        List<Circle> circles = circleDao.findAll();
        for (Circle circle:  circles) {
            userOperation.getOperations().add(this.getOperation(circle,userId));
        }
        return userOperation;
    }

    /**
     * 获取权限
     * @param circle
     * @param userId
     * @return
     */
    private CircleOperationType.UserOperation.Operation getOperation(Circle circle,String userId) {
        CircleOperationType.UserOperation.Operation operation = new CircleOperationType.UserOperation.Operation();
        operation.setCircleId(circle.getId());
        operation.setCircleName(circle.getName());
        // 默认非空
        operation.setHasOperate(false);
        operation.setHasAddCircle(false);
        operation.setHasAddProject(false);
        // 该用户是圈主时 获取所有操作权限
//        if (circle.getOwnerUid().equals(userId) || circle.getCreateUserId().equals(userId)) {
        if (circle.getOwnerUid().equals(userId)) {
            operation.setHasOperate(true);
            operation.setHasAddCircle(true);
            operation.setHasAddProject(true);
        }  else {
            // 是否允许操作圈子
            if (circle.getIsOperate().equals(CircleOperationType.DEFAULT)) {
                operation.setHasOperate(false);
            } else if (circle.getIsOperate().equals(CircleOperationType.ALL)) {
                operation.setHasOperate(true);
            } else if (circle.getIsOperate().equals(CircleOperationType.PART)) {
                boolean isOperate = !CollectionUtils.isEmpty(circle.getOperateUsers()) && circle.getOperateUsers().stream().anyMatch(o -> o.getSysUserId().equals(userId));
                operation.setHasOperate(isOperate);
            }

            // 是否允许添加子圈
            if (circle.getIsAddCircle().equals(CircleOperationType.DEFAULT)) {
                operation.setHasAddCircle(false);
            } else if (circle.getIsAddCircle().equals(CircleOperationType.ALL)) {
                operation.setHasAddCircle(true);
            } else if (circle.getIsAddCircle().equals(CircleOperationType.PART)) {
                boolean isAddCircle = !CollectionUtils.isEmpty(circle.getAddCircleUsers()) &&  circle.getAddCircleUsers().stream().anyMatch(o ->o.getSysUserId().equals(userId));
                operation.setHasAddCircle(isAddCircle);
            }

            // 是否允许添加项目
            if (circle.getIsAddProject().equals(CircleOperationType.DEFAULT)) {
                operation.setHasAddProject(false);
            } else if (circle.getIsAddProject().equals(CircleOperationType.ALL)) {
                operation.setHasAddProject(true);
            } else if (circle.getIsAddProject().equals(CircleOperationType.PART)) {
                boolean isAddProject = !CollectionUtils.isEmpty(circle.getAddProjectUsers()) &&  circle.getAddProjectUsers().stream().anyMatch(o ->o.getSysUserId().equals(userId));
                operation.setHasAddProject(isAddProject);
            }
        }
        return operation;
    }
}
