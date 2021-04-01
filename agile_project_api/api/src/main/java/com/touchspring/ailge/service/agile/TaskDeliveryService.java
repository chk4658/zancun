package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.Delivery;
import com.touchspring.ailge.entity.agile.TaskDelivery;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 任务交付物关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskDeliveryService extends IService<TaskDelivery> {

    boolean save(String taskId, TaskDelivery taskDelivery, String sysUserId, String path, String reUploadId);

    boolean delete(String id, String sysUserId);

    boolean deleteByIdAndReferId(String id, String referId, String afterReferList, String sysUserId);

    List<TaskDelivery> getByTaskId(String taskId);


    Page<TaskDelivery> findByProjectIdFilter(Integer page, Integer size, String projectId, String[] deliveryNames, String searchName);

}
