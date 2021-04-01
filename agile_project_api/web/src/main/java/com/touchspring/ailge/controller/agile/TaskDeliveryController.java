package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.TaskDeliveryDao;
import com.touchspring.ailge.entity.agile.TaskDelivery;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.TaskDeliveryService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 任务交付物关系表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@RestController
@RequestMapping("//task-delivery")
public class TaskDeliveryController {

    private final TaskDeliveryDao taskDeliveryDao;
    private final TaskDeliveryService taskDeliveryService;

    public TaskDeliveryController(TaskDeliveryDao taskDeliveryDao, TaskDeliveryService taskDeliveryService) {
        this.taskDeliveryDao = taskDeliveryDao;
        this.taskDeliveryService = taskDeliveryService;
    }

    /**
     * 列表
     */
    @GetMapping("all")
    public ResultData list(){
        return ResultData.ok().putDataValue("taskDeliveryList",taskDeliveryDao.selectList(null));
    }

    /**
     * 新增或更改
     */
    @PostMapping("")
    public ResultData saveOrUpdate(@RequestHeader(SystemConfig.TOKEN) String token, String taskId, TaskDelivery taskDelivery, String path, @RequestParam(defaultValue = "", required = false) String reUploadId) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        taskDeliveryService.save(taskId, taskDelivery, user.getId(), path, reUploadId);
        return ResultData.ok();
    }

    /**
     * 删除任务绑定的交付物
     */
    @DeleteMapping("delete")
    public ResultData deleteByIdAndReferId(String id, String referId, String afterReferList, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        taskDeliveryService.deleteByIdAndReferId(id, referId, afterReferList, user.getId());
        return ResultData.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id, @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean delete = taskDeliveryService.delete(id, user.getId());
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 当前任务的已有交付物
     */
    @GetMapping("get")
    public ResultData getByTaskId(String taskId) {
        return ResultData.ok().putDataValue("taskDeliveryList", taskDeliveryService.getByTaskId(taskId));
    }

    /**
     * 项目下的所有交付物 && 搜索 分页查询
     */
    @GetMapping("delivery-all")
    public ResultData findAll(@RequestParam(defaultValue = "1", required = false) Integer page,
                              @RequestParam(defaultValue = "20", required = false) Integer size,
                              String projectId,
                              @RequestParam(defaultValue = "", required = false) String[] deliveryNames,
                              @RequestParam(required = false) String searchName){

        Page<TaskDelivery> taskDeliveryPage = taskDeliveryService.findByProjectIdFilter(page, size, projectId, deliveryNames, searchName);
        List<TaskDelivery> byProjectId = taskDeliveryDao.findByProjectId(projectId);


        return ResultData.ok().putDataValue("taskDeliveryList", taskDeliveryPage.getRecords())
                .putDataValue("totalAmount", taskDeliveryPage.getTotal()).putDataValue("taskDeliveryListAll", byProjectId)
                .putDataValue("totalAmountAll",byProjectId.size());

    }
}
