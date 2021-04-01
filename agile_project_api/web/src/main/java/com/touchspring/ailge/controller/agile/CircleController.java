package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dto.CircleDTO;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.CircleOperationType;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.CircleService;
import com.touchspring.ailge.service.agile.ProjectService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 圈子信息表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//circle")
public class CircleController {

    private final CircleDao circleDao;
    private final CircleService circleService;
    private final ProjectService projectService;
    private final CircleLogDao circleLogDao;
    private final CircleRoleDao circleRoleDao;
    private final ChatDao chatDao;
    private final CircleUserDao circleUserDao;
    private final CircleRoleUserDao circleRoleUserDao;

    public CircleController(CircleDao circleDao, CircleService circleService, ProjectService projectService, CircleLogDao circleLogDao, CircleRoleDao circleRoleDao, ChatDao chatDao, CircleUserDao circleUserDao, CircleRoleUserDao circleRoleUserDao) {
        this.circleDao = circleDao;
        this.circleService = circleService;
        this.projectService = projectService;
        this.circleLogDao = circleLogDao;
        this.circleRoleDao = circleRoleDao;
        this.chatDao = chatDao;
        this.circleUserDao = circleUserDao;
        this.circleRoleUserDao = circleRoleUserDao;
    }

    /**
     * 获取圈子及子圈 父圈ID：最上层：0 其他保存圈子ID
     */
    @GetMapping("")
    public ResultData findAllCircles( @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Circle> circleList = circleService.findAll(user.getId());
        return ResultData.ok().putDataValue("circles", circleList);
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id) {
        Circle circle = circleDao.selectById(id);
        List<Circle> parentsCircles = circleService.findParentsById(id);
        // 反转排序 最上一层父圈下标0
        Collections.reverse(parentsCircles);
        if (!CollectionUtils.isEmpty(parentsCircles)){
            parentsCircles.add(circle);
        }
        return ResultData.ok().putDataValue("circle",circle ).putDataValue("parentsCircles",parentsCircles);
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(CircleDTO circleDTO, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean save = circleService.save(circleDTO, user.getId());
        if (save) return ResultData.ok().putDataValue("circle",circleDTO);
        return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
    }


    /**
     * 保存/更新
     */
    @PostMapping("/updateNameAndDescription")
    public ResultData updateNameAndDescription(String id,String name,String description, @RequestHeader(SystemConfig.TOKEN) String token){
        Circle circle = circleDao.selectById(id);
        if (circle == null) return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
        // Log 圈子名称变更
        if (!StringUtils.equals(circle.getName(), name)) {
            CircleLog circleLog = new CircleLog();
            circleLog.setCreateUserId(JWTUtils.unSign(token, SysUser.class).getId());
            circleLog.setContent("Changed 圈子名称, From \"" + circle.getName() + "\", To \"" + name + "\"");
            circleLog.setCircleId(id);
            circleLogDao.insert(circleLog);
        }
        circle.setName(name);
        circle.setDescription(description);
        circleService.updateById(circle);
        return ResultData.ok();
    }

    /**
     * 全部-圈子列表
     * @param searchName 要搜索的名称 空则查所有
     * @return .
     */
    @GetMapping("all")
    public ResultData list(@RequestParam(required = false) String searchName){
        List<Circle> circles = circleDao.findByName(searchName);
        return ResultData.ok().putDataValue("circleList", circles);
    }

    /**
     * 查找父圈列表 根据圈子名称
     * @param searchName 圈子名称
     * @return .
     */
    @PostMapping("parent-list")
    public ResultData findByName(@RequestParam(required = false) String searchName) {
        List<Circle> circleList = circleService.findByName(searchName);
        return ResultData.ok().putDataValue("circleList", circleList);
    }

    /**
     * 获取当前父圈下的一级子圈 && 本圈下当前用户的可见项目
     * @param parentId 当前圈子ID
     * @param searchName 要搜索的名称 空则查所有
     * @return .
     */
    @GetMapping("children-list")
    public ResultData findByParentId(String parentId, @RequestParam(required = false) String searchName, @RequestHeader(SystemConfig.TOKEN) String token){
        List<Circle> childrenCircles = circleService.findByCircleIdAndSearchName(parentId, searchName);
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Project> projectList = projectService.findByCircleIdAndName(parentId, searchName, user.getId());
        return ResultData.ok().putDataValue("childrenCircles", childrenCircles).putDataValue("projectList", projectList);
    }


    /**
     * 删除圈子及关联表
     * @param id 角色ID
     * @return .
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean i = circleService.delete(id, user.getId());
        if (i) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 我的圈子-树结构 根据token获取当前用户信息
     * @param token token
     * @param searchName 要circleOperation搜索的名称 空则查所有
     * @return .
     */
    @GetMapping("my-circle")
    public ResultData findMyCircle(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Circle> myCircles = circleService.getMyCircles(user, searchName);
        return ResultData.ok().putDataValue("circleList", myCircles);
    }

    /**
     * 本圈项目个数 & 圈子下全部项目个数 所有子圈个数
     */
    @GetMapping("num")
    public ResultData findProjectNumByCircleId(String circleId){
        Integer[] projectNum = circleService.projectNum(circleId);
        //角色数
        Integer curCircleRoleNum = new LambdaQueryChainWrapper<CircleRole>(circleRoleDao).eq(CircleRole::getCircleId, circleId).count();
        //交流数
        Integer curCircleChatNum = new LambdaQueryChainWrapper<Chat>(chatDao).eq(Chat::getType, "CIRCLE").eq(Chat::getForeignId, circleId).count();
        return ResultData.ok().putDataValue("curCircleProjectNum", projectNum[0]).putDataValue("curCircleAllProjectNum", projectNum[1])
                .putDataValue("curCircleChildrenNum",  projectNum[2]).putDataValue("curCircleAllChildrenNum", projectNum[3])
                .putDataValue("curCircleRoleNum", curCircleRoleNum).putDataValue("curCircleChatNum", curCircleChatNum);
    }


    /**
     * 获取用户圈子权限
     */
    @GetMapping("circleOperation")
    public ResultData findCircleOperation(@RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        CircleOperationType.UserOperation circleOperation = circleService.getCircleRoleOperation(user.getId());
        return ResultData.ok().putDataValue("circleOperation",circleOperation);
    }

    @GetMapping("has/{id}")
    public ResultData hasByIdAndToken(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<CircleRoleUser> circleRoleUsers = circleRoleUserDao.findListByCircleIdAndUserId(id,user.getId());
        return ResultData.ok().putDataValue("hasCircle",!CollectionUtils.isEmpty(circleRoleUsers));
    }

}
