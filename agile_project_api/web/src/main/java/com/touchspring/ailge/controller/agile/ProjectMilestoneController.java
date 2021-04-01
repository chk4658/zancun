package com.touchspring.ailge.controller.agile;


import com.alibaba.excel.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectMilestoneDao;
import com.touchspring.ailge.dto.EnableOrForbiddenDTO;
import com.touchspring.ailge.dto.ProjectMilestoneDTO;
import com.touchspring.ailge.dto.ProjectRedisDTO;
import com.touchspring.ailge.entity.agile.ProjectAttr;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.service.agile.ProjectAttrService;
import com.touchspring.ailge.service.agile.ProjectMilestoneService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 里程碑信息表
 * 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//project-milestone")
public class ProjectMilestoneController {

    private final ProjectMilestoneDao projectMilestoneDao;
    private final ProjectMilestoneService projectMilestoneService;
    private final ProjectAttrService projectAttrService;

    public ProjectMilestoneController(ProjectMilestoneDao projectMilestoneDao, ProjectMilestoneService projectMilestoneService, ProjectAttrService projectAttrService) {
        this.projectMilestoneDao = projectMilestoneDao;
        this.projectMilestoneService = projectMilestoneService;
        this.projectAttrService = projectAttrService;
    }

    /**
     * 获取指定项目下的里程碑 正序排列
     */
    @GetMapping("")
    public ResultData list(String projectId, @RequestParam(required = false) String searchName) {
        List<ProjectMilestone> projectMilestones = projectMilestoneService.findByProjectId(projectId, searchName);
        //项目任务属性
        List<ProjectAttr> projectAttrs = projectAttrService.findByProjectId(projectId);
        return ResultData.ok().putDataValue("projectMilestones", projectMilestones)
                .putDataValue("projectAttrs", projectAttrs);
    }

    /**
     * 未启用、禁用 里程碑/任务列表
     */
    @GetMapping("enableOrForbidden")
    public ResultData enableOrForbidden(String projectId, String statusName) {
        List<ProjectMilestone> projectMilestones = projectMilestoneService.findByProjectIdAndStatusName(projectId, statusName);
        return ResultData.ok().putDataValue("projectMilestones", projectMilestones);
    }

    /**
     * 获取指定项目下的里程碑 from Redis ---》 只存放启用项目
     */
    @GetMapping("inRedis")
    public ResultData listInRedis(String projectId,
                                  @RequestParam(required = false) String searchName) {
        ProjectRedisDTO projectRedisDTO = projectMilestoneService.findAllInRedisByProjectId(projectId, searchName);
        //项目任务属性
        List<ProjectAttr> projectAttrs = projectAttrService.findByProjectId(projectId);
        return ResultData.ok().putDataValue("projectMilestoneList", projectRedisDTO.getProjectMilestoneList())
                .putDataValue("parentTaskList", projectRedisDTO.getParentTaskList())
                .putDataValue("childTaskList", projectRedisDTO.getChildTaskList())
                .putDataValue("projectAttrs", projectAttrs);
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(ProjectMilestoneDTO projectMilestoneDTO, @RequestHeader(SystemConfig.TOKEN) String token, boolean flag, boolean batchRorC) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return projectMilestoneService.save(projectMilestoneDTO, user.getId(), flag, batchRorC);
    }


    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = projectMilestoneService.delete(id, true, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }


    /**
     * 上移
     */
    @GetMapping("up")
    public ResultData upMilestone(String id) {
        return projectMilestoneService.upMilestone(id);
    }

    /**
     * 下移
     */
    @GetMapping("down")
    public ResultData downMilestone(String id) {
        return projectMilestoneService.downMilestone(id);
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id) {
        return ResultData.ok().putDataValue("projectMilestone", projectMilestoneDao.selectById(id));
    }

    /**
     * 解除禁用/禁用 操作Redis
     *
     * @param enableOrForbidden 0 -> 解除禁用 (1 -> 禁用)
     * @return .
     */
    @PostMapping("notForbidden")
    public ResultData updateForbidden(@RequestBody EnableOrForbiddenDTO enableOrForbidden) {
        projectMilestoneService.updateToNotForbidden(enableOrForbidden.getEnableOrForbiddens(), enableOrForbidden.getStatus());
        return ResultData.ok();
    }

    /**
     * 启用 操作Redis
     *
     * @param enableOrForbidden 1 -> 启用  (0 -> 未启用)
     * @return .
     */
    @PostMapping("toEnabled")
    public ResultData updateEnabled(@RequestBody EnableOrForbiddenDTO enableOrForbidden) {
        projectMilestoneService.updateEnabled(enableOrForbidden.getEnableOrForbiddens(), enableOrForbidden.getStatus());
        return ResultData.ok();
    }

    /**
     * 禁用 操作Redis
     *
     * @return .
     */
    @PostMapping("toForbidden")
    public ResultData updateToForbidden(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens) {
        projectMilestoneService.updateToForbidden(enableOrForbiddens);
        return ResultData.ok();
    }

    /**
     * 未启用 操作Redis
     *
     * @return .
     */
    @PostMapping("notEnabled")
    public ResultData updateToNotEnabled(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens) {
        projectMilestoneService.updateToNotEnabled(enableOrForbiddens);
        return ResultData.ok();
    }


    /**
     * 展开 收起相关
     * single标识单个里程碑的展开收起还是全部展开全部收起
     * 'ONE'=>milestoneId
     * 'ALL'=>projectId
     * flag  true表示展开动作，false表示收起动作
     *
     * @return .
     */
    @PostMapping("collapsed")
    public ResultData updateReferCollapsed(String proOrMileId, String single, boolean flag) {

        Integer motion = flag ? Integer.valueOf(TreeResultStatus.COLLAPSED.getCode()) : Integer.valueOf(TreeResultStatus.NOT_COLLAPSED.getCode());
        return projectMilestoneService.updateReferCollapsed(proOrMileId, single, motion);
    }

    /**
     * 复制里程碑及所属任务关联信息 只复制启用状态任务
     *
     * @param milestoneId 里程碑ID
     * @return .
     */
    @GetMapping("copy")
    public ResultData copyMilestone(String milestoneId) {
        projectMilestoneService.copyMilestone(milestoneId);
        return ResultData.ok();
    }

    /**
     * 获取根据项目id和time 获取里time 最近的时间 若为空则全部返回
     * @return
     */
    @GetMapping("getByProjectIdAndTime")
    public ResultData getByProjectIdAndTime(String projectId, Date time) {
        List<ProjectMilestone> list = new ArrayList<>();
        LambdaQueryWrapper<ProjectMilestone> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(ProjectMilestone::getProjectId,projectId)
                .apply("to_char(CREATE_AT,'yyyy-mm-dd') <= '" + DateUtils.format(time,"yyyy-MM-dd") + "'")
                .orderByDesc(ProjectMilestone::getCreateAt).orderByDesc(ProjectMilestone::getSort);

        List<ProjectMilestone> _list = projectMilestoneDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(_list)) list.add(_list.get(0));
        else {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper
                    .eq(ProjectMilestone::getProjectId,projectId);
            list.addAll(projectMilestoneDao.selectList(queryWrapper));
        }
        return ResultData.ok().putDataValue("projectMilestones",list);
    }

}
