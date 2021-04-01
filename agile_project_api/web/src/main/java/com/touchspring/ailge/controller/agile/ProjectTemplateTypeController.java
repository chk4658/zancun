package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.ProjectTemplateTypeDao;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.ProjectTemplateType;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateTypeService;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目模板类型表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template-type")
public class ProjectTemplateTypeController {

    private final ProjectTemplateTypeDao projectTemplateTypeDao;
    private final ProjectTemplateTypeService projectTemplateTypeService;

    public ProjectTemplateTypeController(ProjectTemplateTypeDao projectTemplateTypeDao, ProjectTemplateTypeService projectTemplateTypeService) {
        this.projectTemplateTypeDao = projectTemplateTypeDao;
        this.projectTemplateTypeService = projectTemplateTypeService;
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplateType projectTemplateType){
        boolean save = projectTemplateTypeService.save(projectTemplateType);
        if (save) return ResultData.ok();
        return new ResultData(ResultStatus.TEMPLATE_TYPE_IS_EXIST.getCode(), ResultStatus.TEMPLATE_TYPE_IS_EXIST.getMessage());
    }

    /**
     * 删除
     */
    @DeleteMapping("{id}")
    public ResultData delete(@PathVariable("id") String id){
        if (StringUtils.isBlank(id)) return ResultData.ok();
        boolean delete = projectTemplateTypeService.delete(id);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * all/模糊查询
     */
    @GetMapping("all")
    public ResultData list(String name){
        return ResultData.ok().putDataValue("projectTemplateTypes", projectTemplateTypeDao.findAllByNameAsc(name));
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectTemplateType", projectTemplateTypeDao.selectById(id));
    }


}
