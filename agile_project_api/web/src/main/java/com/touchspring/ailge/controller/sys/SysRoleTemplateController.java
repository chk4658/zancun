package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysRoleTemplateService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统角色信息表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//sys-role-template")
public class SysRoleTemplateController {

    private final SysRoleTemplateDao sysRoleTemplateDao;
    private final SysRoleTemplateService sysRoleTemplateService;

    @Autowired
    public SysRoleTemplateController(SysRoleTemplateDao sysRoleTemplateDao, SysRoleTemplateService sysRoleTemplateService) {
        this.sysRoleTemplateDao = sysRoleTemplateDao;
        this.sysRoleTemplateService = sysRoleTemplateService;
    }


    /**
     * 角色列表 默认未禁用
     * @param enabled 是否禁用 默认未禁用 0禁用；1未禁用
     * @return .
     */
    @GetMapping("")
    public ResultData list(@RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size,
                           @RequestParam(required = false)Long enabled){
        Page<SysRoleTemplate> roleTemplatePage = sysRoleTemplateService.findAll(page, size, enabled);
        return ResultData.ok().putDataValue("SysRoleTemplateList", roleTemplatePage.getRecords()).putDataValue("totalAmount", roleTemplatePage.getTotal());
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysRoleTemplate sysRoleTemplate){
        boolean save = sysRoleTemplateService.save(sysRoleTemplate);
        if (save) return ResultData.ok();
        return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        int flag = sysRoleTemplateDao.deleteById(id);
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 批量删除
     */
    @DeleteMapping("batch-delete")
    public ResultData batchDelete(String[] tempRoleIds){
        int flag = sysRoleTemplateDao.deleteBatchIds(Arrays.asList(tempRoleIds));
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("SysRoleTemplate", sysRoleTemplateDao.selectById(id));
    }

    /**
     * 批量禁用
     */
    @DeleteMapping("batch-enabled")
    public ResultData batchEnabled(String[] roleIds){
        boolean flag = sysRoleTemplateService.batchEnabled(roleIds, true);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.ENABLED_ERROR.getCode(), ResultStatus.ENABLED_ERROR.getMessage());
    }

    /**
     * 批量可用
     */
    @DeleteMapping("batch-available")
    public ResultData batchAvailable(String[] roleIds){
        boolean flag = sysRoleTemplateService.batchEnabled(roleIds, false);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.ENABLED_ERROR.getCode(), ResultStatus.ENABLED_ERROR.getMessage());
    }

    /**
     * 搜索
     * @param roleName 搜索的名称
     * @param enabled 是否禁用
     * @return .
     */
    @PostMapping("search")
    public ResultData search(@RequestParam(defaultValue = "1", required = false) Integer page,
                             @RequestParam(defaultValue = "20", required = false) Integer size,
                             String roleName,
                             Long enabled){
        Page<SysRoleTemplate> circleLogPage = new Page<>(page, size);
        circleLogPage.setRecords(sysRoleTemplateService.searchByPage(circleLogPage, roleName, enabled));
        return ResultData.ok().putDataValue("search", circleLogPage.getRecords()).putDataValue("totalAmount", circleLogPage.getTotal());
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("sysRoleTemplateList", new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao)
                .eq(SysRoleTemplate::getEnabled,0)
                .orderByAsc(BaseEntity::getCreateAt).list());
    }


    /**
     * 列表
     */
    @GetMapping("roleNames")
    public ResultData listByRoleNames(@RequestParam("roleNames") List<String> roleNames){
        return ResultData.ok().putDataValue("sysRoleTemplateList", sysRoleTemplateService.findListByRoleNames(roleNames));
    }
}
