package com.touchspring.ailge.controller.agile;


import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectCollectionDao;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.ProjectCollectionService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目与人员收藏关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@RestController
@RequestMapping("/project-collection")
public class ProjectCollectionController {

    private final ProjectCollectionService projectCollectionService;
    private final ProjectCollectionDao projectCollectionDao;

    public ProjectCollectionController(ProjectCollectionService projectCollectionService, ProjectCollectionDao projectCollectionDao) {
        this.projectCollectionService = projectCollectionService;
        this.projectCollectionDao = projectCollectionDao;
    }

    /**
     * 添加/取消 收藏
     * @param token 当前token
     * @param projectId 项目ID
     * @param flag true -> 收藏 false -> 取消收藏
     * @return .
     */
    @GetMapping("addOrCancel")
    public ResultData addOrCancelCollection(@RequestHeader(SystemConfig.TOKEN) String token, String projectId, boolean flag){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean collection = projectCollectionService.addOrCancelCollection(user.getId(), projectId, flag);
//        if (!collection) return new ResultData(ResultStatus.IS_EXIST.getCode(), ResultStatus.IS_EXIST.getMessage());
        return ResultData.ok();
    }

    /**
     * 当前用户的收藏项目
     * @param token 当前用户token
     * @param searchName 要搜索的项目名称
     * @return .
     */
    @GetMapping("favorite")
    public ResultData userFavorites(@RequestHeader(SystemConfig.TOKEN) String token, @RequestParam(required = false) String searchName){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        List<Project> projects = projectCollectionService.userFavorites(user.getId(), searchName);
        return ResultData.ok().putDataValue("favoriteProject", projects);
    }

}
