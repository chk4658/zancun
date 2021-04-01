package com.touchspring.ailge.controller.sys;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysDictDataDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysDictData;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysDictDataService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 字典数据表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//sys-dict-data")
public class SysDictDataController {
    private final SysDictDataDao sysDictDataDao;
    private final SysDictDataService sysDictDataService;

    @Autowired
    public SysDictDataController(SysDictDataDao sysDictDataDao, SysDictDataService sysDictDataService) {
        this.sysDictDataDao = sysDictDataDao;
        this.sysDictDataService = sysDictDataService;
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData save(SysDictData sysDictData){
        boolean save = sysDictDataService.save(sysDictData);
        if (!save) return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        int flag = sysDictDataDao.deleteById(id);
        if (flag > 0) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("SysDictData", sysDictDataDao.selectById(id));
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("SysDictDataList", new LambdaQueryChainWrapper<SysDictData>(sysDictDataDao).orderByAsc(BaseEntity::getCreateAt).list());
    }
}
