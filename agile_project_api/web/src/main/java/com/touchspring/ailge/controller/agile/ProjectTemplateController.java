package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectTemplateDao;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.ProjectTemplate;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.IsEnabled;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目模板表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-template")
public class ProjectTemplateController {

    private final ProjectTemplateDao projectTemplateDao;
    private final ProjectTemplateService projectTemplateService;

    public ProjectTemplateController(ProjectTemplateDao projectTemplateDao, ProjectTemplateService projectTemplateService) {
        this.projectTemplateDao = projectTemplateDao;
        this.projectTemplateService = projectTemplateService;
    }

    /**
     * 列表
     */
    @GetMapping("get-list")
    public ResultData findByProjectTemplateTypeId(String projectTemplateTypeId, String searchName) {
        List<ProjectTemplate> projectTemplates = projectTemplateService.findByProjectTemplateTypeId(projectTemplateTypeId, searchName,0L);
        return ResultData.ok().putDataValue("projectTemplates", projectTemplates);
    }


    /**
     * 根据模板类型ID 获取未禁用模板
     */
    @GetMapping("templatesByTypeId")
    public ResultData findTemplatesByTypeId(String projectTemplateTypeId){
        List<ProjectTemplate> projectTemplates = new LambdaQueryChainWrapper<ProjectTemplate>(projectTemplateDao).eq(ProjectTemplate::getProjectTemplateTypeId, projectTemplateTypeId)
                .eq(ProjectTemplate::getEnabled, IsEnabled.NOT_DISABLED.getCode()).orderByAsc(BaseEntity::getCreateAt).list();
        return ResultData.ok().putDataValue("projectTemplates", projectTemplates);
    }

    /**
     * 根据模板ID 返回树状结构
     */
    @GetMapping("tree")
    public ResultData findTreeResultByProjectTemplateId(String projectTemplateId) {
        List<TreeResultDTO> treeResultDTOS = projectTemplateService.findTreeResult(projectTemplateId);
        return ResultData.ok().putDataValue("treeResult", treeResultDTOS);
    }

    /**
     * 全部
     */
    @GetMapping("all")
    public ResultData findAll(String searchName){
        List<ProjectTemplate> projectTemplates = new ArrayList<>();
        if (StringUtils.isBlank(searchName))
            projectTemplates = projectTemplateDao.findByProjectTemplate(null);
        else
            projectTemplates = projectTemplateDao.findByProjectTemplate(new ProjectTemplate(searchName));
        return ResultData.ok().putDataValue("projectTemplates", projectTemplates);
    }

    /**
     * 已禁用模板
     */
    @GetMapping("enabled")
    public ResultData findEnabledTemplate(String searchName){
        ProjectTemplate projectTemplate = new ProjectTemplate();
        projectTemplate.setEnabled(IsEnabled.DISABLE.getCode());
        if (StringUtils.isNotBlank(searchName)) projectTemplate.setName(searchName);
        List<ProjectTemplate> projectTemplates = projectTemplateDao.findByProjectTemplate(projectTemplate);
        return ResultData.ok().putDataValue("projectTemplates", projectTemplates);
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectTemplate projectTemplate, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean save = projectTemplateService.save(projectTemplate, user.getId());
        if (save)
            return ResultData.ok();
        else
            return new ResultData(ResultStatus.TEMPLATE_IS_EXIST.getCode(), ResultStatus.TEMPLATE_IS_EXIST.getMessage());
    }

    /**
     * 禁用/恢复 flag: true -> 禁用 false -> 恢复
     */
    @GetMapping("disable-or-restore")
    public ResultData disableOrRestore(String id, boolean flag) {
        projectTemplateService.disableOrRestore(id, flag);
        return ResultData.ok();
    }

    /**
     * 删除项目模板及关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        boolean flag = projectTemplateService.delete(id);
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("projectTemplate", projectTemplateDao.selectById(id));
    }


    /**
     * 获取 对象下所有数据 包含 属性 任务 子任务 属性值等
     */
    @GetMapping("projectTemplateBean")
    public ResultData getProjectTemplateBean(ProjectTemplate projectTemplate){
        ProjectTemplate tagert = null;
        List<ProjectTemplate> projectTemplates = projectTemplateDao.findByProjectTemplateContainsAttr(projectTemplate);
        if (!CollectionUtils.isEmpty(projectTemplates)) tagert = projectTemplates.get(0);
        return ResultData.ok().putDataValue("projectTemplate", tagert);
    }


}
