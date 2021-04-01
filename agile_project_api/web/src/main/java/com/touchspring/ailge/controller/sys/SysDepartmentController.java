package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysDepartmentDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysDepartment;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysDepartmentService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部门管理表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@RestController
@RequestMapping("/sys-department")
public class SysDepartmentController {

    private final SysDepartmentDao sysDepartmentDao;
    private final SysDepartmentService sysDepartmentService;

    @Autowired
    public SysDepartmentController(SysDepartmentDao sysDepartmentDao, SysDepartmentService sysDepartmentService) {
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysDepartmentService = sysDepartmentService;
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysDepartment sysDepartment){
        sysDepartmentService.saveOrUpdate(sysDepartment);
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        int flag = sysDepartmentDao.deleteById(id);
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("SysDepartment", sysDepartmentDao.selectById(id));
    }

    /**
     * 部门列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("SysDepartmentList", new LambdaQueryChainWrapper<SysDepartment>(sysDepartmentDao).orderByAsc(BaseEntity::getCreateAt).list());
    }

    /**
     * 根据部门名称搜索
     * @param fullName 部门名称
     */
    @GetMapping("get-list")
    public ResultData findByFullName(String fullName) {
        return ResultData.ok().putDataValue("DepartmentListByName", sysDepartmentDao.findByFullName(fullName));

    }
}
