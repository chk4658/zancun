package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysCompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
public interface SysCompanyDao extends BaseMapper<SysCompany> {
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "departments",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysDepartmentDao.findByCompanyId"))
    })
    @Select("select * from sys_company where is_delete = 0 order by CREATE_AT")
    List<SysCompany> findAllCompanyAndDepartments();

    Set<SysCompany> findByFullName(@Param("fullName")String fullName);

}
