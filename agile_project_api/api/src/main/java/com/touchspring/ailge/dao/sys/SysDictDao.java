package com.touchspring.ailge.dao.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysDictDao extends BaseMapper<SysDict> {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "sysDictDataList",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysDictDataDao.findByDictId"))
    })
    @Select("select * from SYS_DICT where IS_DELETE = 0 order by CREATE_AT")
    List<SysDict> findAllSysDictAndDatas(Page page);


    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "sysDictDataList",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysDictDataDao.findByDictId"))
    })
    @Select("select * from SYS_DICT where IS_DELETE = 0 order by CREATE_AT")
    List<SysDict> findAllSysDictAndDatas();

}
