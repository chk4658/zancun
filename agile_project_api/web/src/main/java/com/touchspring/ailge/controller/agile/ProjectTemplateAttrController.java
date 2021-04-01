package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.dao.agile.ProjectTemplateAttrDao;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;
import com.touchspring.ailge.service.agile.ProjectTemplateAttrService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 模板项目属性字段表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template-attr")
public class ProjectTemplateAttrController {

    private final ProjectTemplateAttrDao projectTemplateAttrDao;
    private final ProjectTemplateAttrService projectTemplateAttrService;

    public ProjectTemplateAttrController(ProjectTemplateAttrDao projectTemplateAttrDao, ProjectTemplateAttrService projectTemplateAttrService) {
        this.projectTemplateAttrDao = projectTemplateAttrDao;
        this.projectTemplateAttrService = projectTemplateAttrService;
    }

    /**
     * 保存
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplateAttr projectTemplateAttr){
        projectTemplateAttrService.saveOrUpdate(projectTemplateAttr);
        return ResultData.ok().putDataValue("projectTemplateAttr",projectTemplateAttr);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id) {
        projectTemplateAttrService.delete(id);
        return ResultData.ok();
    }


}
