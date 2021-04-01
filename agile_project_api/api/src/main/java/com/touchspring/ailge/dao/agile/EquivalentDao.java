package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.Equivalent;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface EquivalentDao extends BaseMapper<Equivalent> {

    List<Equivalent> findList(Page<Equivalent> equivalentPage, @Param("equivalent") Equivalent equivalent, @Param("searchName") String searchName,
                              @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "SYS_USER_ID", property = "sysUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.findByUserId")),
    })
    @Select("select * from EQUIVALENT where id = #{id}")
    Equivalent findById(String id);

}
