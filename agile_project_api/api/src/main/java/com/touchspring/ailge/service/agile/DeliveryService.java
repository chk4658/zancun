package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.Delivery;

/**
 * <p>
 * 任务记录表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface DeliveryService extends IService<Delivery> {

    Page<Delivery> findAllDelivery(Integer page, Integer size, String deliveryName);
}
