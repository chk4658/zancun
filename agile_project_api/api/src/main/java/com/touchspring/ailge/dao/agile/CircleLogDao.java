package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 圈子记录表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface CircleLogDao extends BaseMapper<CircleLog> {

    List<CircleLog> findByCircleId(Page page, @Param("circleId") String circleId, @Param("searchName") String searchName);

}
