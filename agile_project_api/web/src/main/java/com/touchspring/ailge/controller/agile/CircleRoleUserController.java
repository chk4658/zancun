package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.CircleRoleUserDao;
import com.touchspring.ailge.entity.agile.CircleRoleUser;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.CircleRoleService;
import com.touchspring.ailge.service.agile.CircleRoleUserService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 圈子角色与人员关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@RestController
@RequestMapping("//circle-role-user")
public class CircleRoleUserController {

    private final CircleRoleUserDao circleRoleUserDao;
    private final CircleRoleUserService circleRoleUserService;

    public CircleRoleUserController(CircleRoleUserDao circleRoleUserDao, CircleRoleUserService circleRoleUserService) {
        this.circleRoleUserDao = circleRoleUserDao;
        this.circleRoleUserService = circleRoleUserService;
    }

    /**
     * 查询当前圈子角色下的人员信息
     * @return .
     */
    @GetMapping("")
    public ResultData findAll(@RequestParam(defaultValue = "1", required = false) Integer page,
                              @RequestParam(defaultValue = "20", required = false) Integer size,
                              String circleRoleId){
        Page<SysUser> sysUserPage = circleRoleUserService.findByCircleRoleId(page, size, circleRoleId);
        return ResultData.ok().putDataValue("sysUserList", sysUserPage.getRecords()).putDataValue("totalAmount", sysUserPage.getTotal());
    }

    /**
     * 删除
     * @param circleRoleId 圈子角色ID
     * @param userId 用户ID
     * @return .
     */
    @DeleteMapping("delete")
    public ResultData delete(String circleRoleId, String userId){
        circleRoleUserService.delete(circleRoleId, userId);
        return ResultData.ok();
    }

    /**
     * 保存
     * @param circleRoleId 圈子角色ID
     * @param userIds 用户IDs
     * @return .
     */
    @PostMapping("")
    public ResultData saveOrUpdate(String circleRoleId, String[] userIds, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        circleRoleUserService.save(circleRoleId, userIds, user.getId());
        return ResultData.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String circleRoleId, String[] userIds){
        circleRoleUserService.batchDelete(circleRoleId, userIds);
        return ResultData.ok();
    }

    /**
     * 根据圈子ID查找当前圈与所有子圈的人员个数
     */
    @GetMapping("num")
    public ResultData findUserNum(String circleId){
        Integer[] num = circleRoleUserService.allCircleNum(circleId);
        return ResultData.ok().putDataValue("curCircleUserNum", num[0]).putDataValue("allCircleUserNum", num[1]);
    }

}
