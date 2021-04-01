package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysUserService;
import com.touchspring.core.mvc.ui.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@Slf4j
@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysUserDao sysUserDao;

    @Autowired
    public SysUserController(SysUserService sysUserService, SysUserDao sysUserDao) {
        this.sysUserService = sysUserService;
        this.sysUserDao = sysUserDao;
    }

    /**
     * 根据账号或姓名 是否禁用 分页查找
     *
     * @param companyId    公司ID
     * @param departmentId 部门ID
     * @param name         账号/真实姓名
     * @param enabled      是否禁用
     * @return .
     */
    @PostMapping("list")
    public ResultData findByCompanyAndDepartment(@RequestParam(defaultValue = "1", required = false) Integer page,
                                                 @RequestParam(defaultValue = "20", required = false) Integer size,
                                                 @RequestParam(required = false) String companyId,
                                                 @RequestParam(required = false) String departmentId,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) Long enabled) {
        IPage<SysUser> sysUserIPage = sysUserService.findByCompanyAndDepartment(page, size, companyId, departmentId, name, enabled);
        return ResultData.ok().putDataValue("sysUserRecords", sysUserIPage.getRecords()).putDataValue("totalAmount", sysUserIPage.getTotal());
    }

    /**
     * 批量删除
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] userIds) {
        int flag = sysUserDao.deleteBatchIds(Arrays.asList(userIds));
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 批量禁用
     */
    @DeleteMapping("batch-enabled")
    public ResultData batchEnabled(String[] userIds) {
        boolean flag = sysUserService.batchEnabled(userIds, true);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.ENABLED_ERROR.getCode(), ResultStatus.ENABLED_ERROR.getMessage());
    }

    /**
     * 批量可用
     */
    @DeleteMapping("batch-available")
    public ResultData batchAvailable(String[] userIds) {
        boolean flag = sysUserService.batchEnabled(userIds, false);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.AVAILABLE_ERROR.getCode(), ResultStatus.AVAILABLE_ERROR.getMessage());
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysUser sysUser) {
        return ResultData.ok().putDataValue("restult"
                ,sysUserService.save(sysUser));
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id) {
        return ResultData.ok().putDataValue("sysUser", sysUserDao.selectById(id));
    }

    /**
     * 人员列表
     */
    @GetMapping("all")
    public ResultData list() {
        return ResultData.ok().putDataValue("sysUserList", new LambdaQueryChainWrapper<SysUser>(sysUserDao).orderByAsc(BaseEntity::getCreateAt).list());
    }

    @PostMapping("pull")
    public ResultData pullData(HttpServletRequest request) {
        if (sysUserService.pullData()) {
            log.info("success");
            return ResultData.ok();
        }
        return ResultData.errorRequest();
    }



    /**
     * 根据人员ids 获取数据
     */
    @GetMapping("ids")
    public ResultData listByIds(@RequestParam("ids")List<String> ids) {
        return ResultData.ok().putDataValue("sysUserList",
                new LambdaQueryChainWrapper<SysUser>(sysUserDao).in(SysUser::getId, ids).list());
    }

}
