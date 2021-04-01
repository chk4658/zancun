package com.touchspring.ailge.dao.agile;

import com.touchspring.ailge.dto.CircleChartDTO;
import com.touchspring.ailge.entity.agile.Circle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleDao extends BaseMapper<Circle> {

    List<Circle> findAllByCircleIdAndSearchName(@Param("parentId") String parentId, @Param("searchName") String searchName);

    List<Circle> findAllChildCircleIdAndSearchName(@Param("searchName") String searchName);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "operateUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findOperateListByCircleId")),
            @Result(column = "id", property = "addCircleUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findAddAddCircleListByCircleId")),
            @Result(column = "id", property = "addProjectUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findAddProjectListByCircleId"))
    })
    @Select("select * from CIRCLE where is_delete = 0")
    List<Circle> findAll();

    List<Circle> findByName(@Param("searchName") String searchName);

    List<Circle> findParentList(@Param("searchName") String searchName);

    List<Circle> findByCircleIdsAndName(@Param("circleIds") Set<String> circleIds, @Param("searchName") String searchName);


//    @Results({
//            @Result(column = "id", property = "id"),
//            @Result(column = "id", property = "operateUsers",
//                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findOperateListByCircleId")),
//            @Result(column = "id", property = "addCircleUsers",
//                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findAddAddCircleListByCircleId")),
//            @Result(column = "id", property = "addProjectUsers",
//                    many = @Many(select = "com.touchspring.ailge.dao.agile.CircleUserDao.findAddProjectListByCircleId"))
//    })
//    @Select("select * from CIRCLE where is_delete = 0 and (parent_id IS NULL OR parent_id = '')")
//    List<Circle> findListByParentIdIsNull();

    List<CircleChartDTO> getNewCircleNumPerMonth();

    List<CircleChartDTO> getTopTenTaskNumInCircle();

    List<CircleChartDTO> getTopTenUserNumInCircle();

    List<CircleChartDTO> getNewTaskNumPerDayInCircleIds(@Param("circleIds") List<String> circleIds, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
