package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.DraftBoxDao;
import com.touchspring.ailge.dto.ProjectDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.DraftBox;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.DraftBoxService;
import com.touchspring.ailge.service.agile.ProjectService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 草稿箱 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//draft-box")
public class DraftBoxController {

    private final DraftBoxDao draftBoxDao;
    private final DraftBoxService draftBoxService;
    private final ProjectService projectService;


    public DraftBoxController(DraftBoxDao draftBoxDao, DraftBoxService draftBoxService, ProjectService projectService) {
        this.draftBoxDao = draftBoxDao;
        this.draftBoxService = draftBoxService;
        this.projectService = projectService;
    }


    /**
     * 保存为草稿箱
     */
    @PostMapping("")
    public ResultData saveToDraftBox(@RequestHeader(SystemConfig.TOKEN) String token, ProjectDTO projectDTO){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        projectService.save(projectDTO, false, user);
        return ResultData.ok();
    }


    /**
     * 查看草稿箱
     */
    @GetMapping("all")
    public ResultData findAllByUser(@RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<DraftBox> draftBoxList = new LambdaQueryChainWrapper<DraftBox>(draftBoxDao).eq(DraftBox::getSysUserId, user.getId()).orderByAsc(BaseEntity::getCreateAt).list();
        return ResultData.ok().putDataValue("draftBoxList", draftBoxList);
    }

    /**
     * 删除草稿箱及关联项目、关联表
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token){
        boolean flag = draftBoxService.delete(id, JWTUtils.unSign(token, SysUser.class).getId());
        if (flag) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取草稿箱
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        Project project = draftBoxService.findByDraftBoxId(id);
        return ResultData.ok().putDataValue("draftBox", draftBoxDao.selectById(id)).putDataValue("project", project);
    }

}
