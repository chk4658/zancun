package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.sys.SysRoleDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysRoleService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("//sys-role")
public class SysRoleController {

    private final SysRoleDao sysRoleDao;
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleDao sysRoleDao, SysRoleService sysRoleService) {
        this.sysRoleDao = sysRoleDao;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 角色列表
     */
    @GetMapping("")
    public ResultData list(@RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size){
        Page<SysRole> sysRolePage = new LambdaQueryChainWrapper<SysRole>(sysRoleDao).orderByAsc(BaseEntity::getCreateAt).page(new Page<>(page, size));
        return ResultData.ok().putDataValue("sysRolePage", sysRolePage.getRecords()).putDataValue("totalAmount", sysRolePage.getTotal());
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysRole sysRole){
        boolean save = sysRoleService.save(sysRole);
        if (save) return ResultData.ok();
        return new ResultData(ResultStatus.ROLE_IS_EXIST.getCode(), ResultStatus.ROLE_IS_EXIST.getMessage());
    }

    /**
     * 根据角色id删除角色 及关联角色表
     * @param id 角色ID
     * @return .
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        boolean delete = sysRoleService.delete(id);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("SysRole", sysRoleDao.selectById(id));
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("sysRoleList", new LambdaQueryChainWrapper<SysRole>(sysRoleDao).orderByAsc(BaseEntity::getCreateAt).list());
    }

    /**
     * 搜索
     * @param roleName 角色名称
     * @return .
     */
    @PostMapping("search")
    public ResultData search(String roleName){
        List<SysRole> search = sysRoleService.search(roleName);
//        if (CollectionUtils.isEmpty(search)) return new ResultData(ResultStatus.NOT_EXIST.getCode(), ResultStatus.NOT_EXIST.getMessage());
        return ResultData.ok().putDataValue("search", search);
    }
}
