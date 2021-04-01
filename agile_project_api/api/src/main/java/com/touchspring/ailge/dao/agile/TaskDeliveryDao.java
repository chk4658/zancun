package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.TaskDelivery;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 任务交付物关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskDeliveryDao extends BaseMapper<TaskDelivery> {

    @Results({
            @Result(column = "DELIVERY_ID", property = "deliveryId"),
            @Result(column = "DELIVERY_ID", property = "deliveryList",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.DeliveryDao.selectById"))
    })
    @Select("select * from TASK_DELIVERY where is_delete = 0 and TASK_ID = #{taskId} order by CREATE_AT")
    List<TaskDelivery> getDeliveryByTaskId(@Param("taskId") String taskId);

    @Delete("DELETE FROM TASK_DELIVERY WHERE TASK_ID = #{taskId}")
    int deleteByTaskId(@Param("taskId") String taskId);

    List<TaskDelivery> findPageByProjectIdOrDeliveryNameFilter(Page page, @Param("projectId") String projectId, @Param("deliveryNames") String[] deliveryNames, @Param("searchName") String searchName);

    List<TaskDelivery> findByDeliveryId(@Param("deliveryId") String deliveryId);

    @Select("SELECT * FROM TASK_DELIVERY WHERE IS_DELETE = 0  AND PROJECT_ID = #{projectId} order by CREATE_AT")
    List<TaskDelivery> findByProjectId(@Param("projectId") String projectId);

}
