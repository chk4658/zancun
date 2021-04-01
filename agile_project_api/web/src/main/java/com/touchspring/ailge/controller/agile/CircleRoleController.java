package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.CircleRoleDao;
import com.touchspring.ailge.entity.agile.CircleRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.CircleRoleService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 圈子角色关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//circle-role")
public class CircleRoleController {

    private final CircleRoleDao circleRoleDao;
    private final CircleRoleService circleRoleService;

    public CircleRoleController(CircleRoleDao circleRoleDao, CircleRoleService circleRoleService) {
        this.circleRoleDao = circleRoleDao;
        this.circleRoleService = circleRoleService;
    }

    /**
     * 列表
     */
    @GetMapping("")
    public ResultData findAll(@RequestParam(defaultValue = "1", required = false) Integer page,
                              @RequestParam(defaultValue = "20", required = false) Integer size,
                              CircleRole circleRole){
//        LambdaQueryWrapper<CircleRole> queryWrapper = new LambdaQueryWrapper<CircleRole>().eq(CircleRole::getCircleId, circleId);
        Page<CircleRole> circleRolePage = new Page<>(page, size);
        circleRolePage.setRecords(circleRoleDao.findListPage(circleRolePage,circleRole ));
        return ResultData.ok().putDataValue("circleRoleList", circleRolePage.getRecords()).putDataValue("totalAmount", circleRolePage.getTotal());
    }

    /**
     * 添加或修改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(CircleRole circleRole, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        ResultData resultData = circleRoleService.insert(circleRole, user.getId());
        return new ResultData(resultData.getCode(), resultData.getMessage());
    }

    /**
     * 删除  并删除关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean flag = circleRoleService.batchDelete(new String[]{id}, user.getId());
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("circleRole", circleRoleDao.selectById(id));
    }

    /**
     * 批量删除 并删除关联表
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] ids, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean flag = circleRoleService.batchDelete(ids, user.getId());
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 根据圈子ID查询
     * @param circleId 圈子ID
     * @return .
     */
    @GetMapping("list")
    public ResultData findByCircleId(String circleId){
        List<CircleRole> circleRoleList = circleRoleService.findByCircleId(circleId);
        return ResultData.ok().putDataValue("circleRoleList", circleRoleList);
    }


}
