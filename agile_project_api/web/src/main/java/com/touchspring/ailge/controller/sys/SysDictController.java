package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.sys.SysDictDao;
import com.touchspring.ailge.dao.sys.SysDictDataDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysDict;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysDictService;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/sys-dict")
public class SysDictController {

    private final SysDictDao sysDictDao;
    private final SysDictService sysDictService;
    private final SysDictDataDao sysDictDataDao;

    @Autowired
    public SysDictController(SysDictDao sysDictDao, SysDictService sysDictService, SysDictDataDao sysDictDataDao) {
        this.sysDictDao = sysDictDao;
        this.sysDictService = sysDictService;
        this.sysDictDataDao = sysDictDataDao;
    }

    /**
     * 字典表 分页查询
     */
    @GetMapping("")
    public ResultData list(@RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<SysDict> SysDictList = sysDictDao.findAllSysDictAndDatas(new Page<>(page, size));
        Integer totalAmount = sysDictDao.selectCount(null);
        return ResultData.ok().putDataValue("SysDictList", SysDictList).putDataValue("totalAmount", totalAmount);
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData save(SysDict sysDict) {
        if (StringUtils.isBlank(sysDict.getName()) || StringUtils.isBlank(sysDict.getCode()))
            return new ResultData(ResultStatus.DICT_IS_FAIL.getCode(), ResultStatus.DICT_IS_FAIL.getMessage());
        boolean save = sysDictService.save(sysDict);
        if (!save) return new ResultData(ResultStatus.CODE_IS_EXIST.getCode(), ResultStatus.CODE_IS_EXIST.getMessage());
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id) {
        boolean flag = sysDictService.delete(id);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id) {
        return ResultData.ok().putDataValue("SysDict", sysDictService.findById(id));
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list() {
        return ResultData.ok().putDataValue("SysDictList", new LambdaQueryChainWrapper<SysDict>(sysDictDao).orderByAsc(BaseEntity::getCreateAt).list());
    }


    /**
     * 列表
     */
    @GetMapping("list")
    public ResultData allList() {
        List<SysDict> SysDictList = sysDictDao.findAllSysDictAndDatas();
        return ResultData.ok().putDataValue("SysDictList", SysDictList);
    }




    /**
     * 列表
     */
    @GetMapping("code-list")
    public ResultData allListByCode(String code) {
        List<SysDict> sysDictList = new LambdaQueryChainWrapper<SysDict>(sysDictDao).eq(SysDict::getCode, code).list();
        SysDict sysDict = null;
        if (!CollectionUtils.isEmpty(sysDictList))  {
            sysDict =  sysDictList.get(0);
            sysDict.setSysDictDataList(sysDictDataDao.findByDictId(sysDict.getId()));
        }
        return ResultData.ok().putDataValue("sysDict", sysDict);
    }
//    /**
//     * 根据Code获取枚举类
//     */
//    @GetMapping("{code}")
//    public ResultData get(@PathVariable("code") String code){
//        new
//        return ResultData.ok().putDataValue("SysDict", sysDictService.findById(id));
//    }

}
