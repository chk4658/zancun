package com.touchspring.ailge.controller.agile;


import com.touchspring.ailge.dao.agile.ProjectAttrDao;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.service.agile.ProjectAttrService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 项目任务属性字段表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/project-attr")
public class ProjectAttrController {

    private final ProjectAttrDao projectAttrDao;
    private final ProjectAttrService projectAttrService;

    public ProjectAttrController(ProjectAttrDao projectAttrDao, ProjectAttrService projectAttrService) {
        this.projectAttrDao = projectAttrDao;
        this.projectAttrService = projectAttrService;
    }

    /**
     * 保存
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectAttr projectAttr){
        projectAttrService.saveOrUpdate(projectAttr);
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id) {
        projectAttrService.delete(id);
        return ResultData.ok();
    }


}
