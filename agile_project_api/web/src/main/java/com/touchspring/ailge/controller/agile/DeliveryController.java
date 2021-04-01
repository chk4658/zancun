package com.touchspring.ailge.controller.agile;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Delivery;
import com.touchspring.ailge.service.agile.DeliveryService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 交付物 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("//delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * 检索所有的交付物及对应的项目 任务 上传者关系 分页
     */
    @GetMapping("all")
    public ResultData findAllDelivery(@RequestParam(defaultValue = "1", required = false) Integer page,
                              @RequestParam(defaultValue = "20", required = false) Integer size, String deliveryName){
        Page<Delivery> allDelivery = deliveryService.findAllDelivery(page, size, deliveryName);
        return ResultData.ok().putDataValue("deliveryList", allDelivery.getRecords()).putDataValue("totalAmount", allDelivery.getTotal());
    }

}
