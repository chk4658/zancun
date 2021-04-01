package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Delivery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 交付物 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface DeliveryDao extends BaseMapper<Delivery> {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "taskDeliveryList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.TaskDeliveryDao.findByDeliveryId"))
    })
    @Select("select * from DELIVERY where IS_DELETE = 0 and NAME like '%${name}%' order by CREATE_AT")
    List<Delivery> findByName(Page page, @Param("name") String name);

}
