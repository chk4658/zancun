package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysCompanyDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysCompany;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysCompanyService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@RestController
@RequestMapping("//sys-company")
public class SysCompanyController {

    private final SysCompanyDao sysCompanyDao;
    private final SysCompanyService sysCompanyService;

    @Autowired
    public SysCompanyController(SysCompanyDao sysCompanyDao, SysCompanyService sysCompanyService) {
        this.sysCompanyDao = sysCompanyDao;
        this.sysCompanyService = sysCompanyService;
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysCompany sysCompany){
        sysCompanyService.saveOrUpdate(sysCompany);
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        int flag = sysCompanyDao.deleteById(id);
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("sysCompany", sysCompanyDao.selectById(id));
    }

    /**
     * 公司列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("sysCompanyList", new LambdaQueryChainWrapper<SysCompany>(sysCompanyDao).orderByAsc(BaseEntity::getCreateAt).list());
    }

    /**
     * 获取所有公司及对应部门
     * @return .
     */
    @GetMapping("get-list")
    public ResultData findAllCompanyAndDepartments(){
        List<SysCompany> allSysCompany = sysCompanyDao.findAllCompanyAndDepartments();
        return ResultData.ok().putDataValue("results", allSysCompany);
    }

    /**
     *  根据公司/部门名称查找
     */
    @GetMapping("name")
    public ResultData findByName(String fullName){
        Set<SysCompany> allSysCompany = sysCompanyService.findByFullName(fullName);
//        if (CollectionUtils.isEmpty(allSysCompany)) return new ResultData(ResultStatus.NOT_EXIST.getCode(), ResultStatus.NOT_EXIST.getMessage());
        return ResultData.ok().putDataValue("nameList", allSysCompany);
    }

}
