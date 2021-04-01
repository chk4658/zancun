package com.touchspring.ailge.dao.sys;

import com.touchspring.ailge.entity.sys.SysDepartment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门管理表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
public interface SysDepartmentDao extends BaseMapper<SysDepartment> {

    List<SysDepartment> findByCompanyId(String companyId);

    List<SysDepartment> findByFullName(@Param("fullName")String fullName);

    List<SysDepartment> findByCompanyIdAndFullName(@Param("companyId")String companyId, @Param("fullName")String fullName);

}
