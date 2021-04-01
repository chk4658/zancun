package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.DeliveryDao;
import com.touchspring.ailge.entity.agile.Delivery;
import com.touchspring.ailge.service.agile.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务记录表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryDao, Delivery> implements DeliveryService {

    @Autowired
    private DeliveryDao deliveryDao;

    /**
     * 查找所有交付物
     */
    @Override
    public Page<Delivery> findAllDelivery(Integer page, Integer size, String deliveryName) {
        Page<Delivery> deliveryPage = new Page<>(page, size);
        return deliveryPage.setRecords(deliveryDao.findByName(deliveryPage, deliveryName));
    }
}
