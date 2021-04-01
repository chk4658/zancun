package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字典数据表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysDictDataDao extends BaseMapper<SysDictData> {

    List<SysDictData> findByDictId(@Param("dictId") String dictId);

    List<SysDictData> findDataByDictName(@Param("name") String name);

}
