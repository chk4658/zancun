package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectTemplateMilestoneDao;
import com.touchspring.ailge.dto.ProjectTemplateMilestoneDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;
import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import com.touchspring.ailge.entity.agile.ProjectTemplateType;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateAttrService;
import com.touchspring.ailge.service.agile.ProjectTemplateMilestoneService;
import com.touchspring.ailge.service.agile.ProjectTemplateTypeService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目模板里程碑表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template-milestone")
public class ProjectTemplateMilestoneController {

    private final ProjectTemplateMilestoneDao projectTemplateMilestoneDao;
    private final ProjectTemplateMilestoneService projectTemplateMilestoneService;
    private final ProjectTemplateAttrService projectTemplateAttrService;
    private final ProjectTemplateTypeService projectTemplateTypeService;

    public ProjectTemplateMilestoneController(ProjectTemplateMilestoneDao projectTemplateMilestoneDao, ProjectTemplateMilestoneService projectTemplateMilestoneService, ProjectTemplateAttrService projectTemplateAttrService, ProjectTemplateTypeService projectTemplateTypeService) {
        this.projectTemplateMilestoneDao = projectTemplateMilestoneDao;
        this.projectTemplateMilestoneService = projectTemplateMilestoneService;
        this.projectTemplateAttrService = projectTemplateAttrService;
        this.projectTemplateTypeService = projectTemplateTypeService;
    }

    /**
     * 根据项目模板ID获取里程碑&任务 / 根据名称查找
     */
    @GetMapping("")
    public ResultData list(String projectTemplateId,
                           @RequestParam(required = false) String searchName,
                           @RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "5", required = false) Integer size){
        Page<ProjectTemplateMilestone> templateMilestonePage = new Page<>(page, size);
        templateMilestonePage.setRecords(projectTemplateMilestoneService.findByProjectTemplateId(templateMilestonePage, projectTemplateId, searchName));
//        List<ProjectTemplateMilestone> projectTemplateMilestones = ;
        List<ProjectTemplateAttr> projectTemplateAttrs = projectTemplateAttrService.findByProjectTemplateId(projectTemplateId);
        //模板类型
//        ProjectTemplateType projectTemplateType = projectTemplateTypeService.findByProjectTemplateId(projectTemplateId);
        return ResultData.ok().putDataValue("projectTemplateMilestones", templateMilestonePage.getRecords())
                .putDataValue("totalAmount", templateMilestonePage.getTotal())
                .putDataValue("projectTemplateAttrs", projectTemplateAttrs);
//                .putDataValue("projectTemplateType", projectTemplateType);
    }


    @GetMapping("assemble/{id}")
    public ResultData getById(@PathVariable("id") String id){
        ProjectTemplateMilestone projectTemplateMilestone = projectTemplateMilestoneService.getId(id);
        List<ProjectTemplateAttr> projectTemplateAttrs = projectTemplateAttrService.findByProjectTemplateId(projectTemplateMilestone.getProjectTemplateId());
        return ResultData.ok().putDataValue("projectTemplateMilestone",projectTemplateMilestone )
                .putDataValue("projectTemplateAttrs", projectTemplateAttrs);
    }



    @GetMapping("assemble/attrs/{projectTemplateId}")
    public ResultData getProjectTemplateAttrsByProjectTemplateId(@PathVariable("projectTemplateId") String projectTemplateId){
        List<ProjectTemplateAttr> projectTemplateAttrs = projectTemplateAttrService.findByProjectTemplateId(projectTemplateId);
        return ResultData.ok().putDataValue("projectTemplateAttrs", projectTemplateAttrs);
    }

    /**
     * 保存 / 更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplateMilestoneDTO projectTemplateMilestoneDTO){
        projectTemplateMilestoneService.save(projectTemplateMilestoneDTO);
        return ResultData.ok().putDataValue("id",projectTemplateMilestoneDTO.getId());
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        boolean delete = projectTemplateMilestoneService.delete(id);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectTemplateMilestone", projectTemplateMilestoneDao.selectById(id));
    }

    /**
     * 上移
     */
    @GetMapping("up")
    public ResultData upMilestone(String id) {
        return projectTemplateMilestoneService.upMilestone(id);
    }

    /**
     * 下移
     */
    @GetMapping("down")
    public ResultData downMilestone(String id) {
        return projectTemplateMilestoneService.downMilestone(id);
    }



}
