package com.touchspring.ailge.controller.agile;


import com.touchspring.ailge.dao.agile.CircleUserDao;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.CircleUserService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 圈子角色与人员关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@RestController
@RequestMapping("//circle-user")
public class CircleUserController {

    private final CircleUserDao circleUserDao;
    private final CircleUserService circleUserService;

    public CircleUserController(CircleUserDao circleUserDao, CircleUserService circleUserService) {
        this.circleUserDao = circleUserDao;
        this.circleUserService = circleUserService;
    }

    /**
     * 查找成员
     * @param circleId 圈子ID
     * @param type 授权类型
     * @return .
     */
    @GetMapping("user")
    public ResultData findUserByCircleIdAndType(String circleId, String type){
        List<SysUser> sysUsers = circleUserService.findByCircleIdAndType(circleId, type);
//        if (sysUsers == null) return new ResultData(ResultStatus.NOT_EXIST.getCode(), ResultStatus.NOT_EXIST.getMessage());
        return ResultData.ok().putDataValue("sysUsers", sysUsers);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        int flag = circleUserDao.deleteById(id);
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }
}
