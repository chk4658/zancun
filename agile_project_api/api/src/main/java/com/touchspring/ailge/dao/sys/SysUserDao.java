package com.touchspring.ailge.dao.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.sys.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysUserDao extends BaseMapper<SysUser> {

    /**
     * @param page 翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     */
    List<SysUser> findByCompanyAndDepartment(Page page, @Param("companyId") String companyId, @Param("departmentId")String departmentId,
                                             @Param("name") String name, @Param("enabled")Long enabled);

    @Select("select u.*, d.full_name departmentName from sys_user u left join sys_department d on u.department_id = d.id where u.id = #{id} and u.is_delete = 0")
    SysUser findByUserId(@Param("id") String id);

    List<SysUser> findByCircleRoleId(Page page, @Param("circleRoleId") String circleRoleId);

    List<SysUser> findByUserIds(Page page, @Param("userIds") List<String> userIds);

    SysUser findByProjectRoleId(@Param("projectRoleId") String projectRoleId);

    List<SysUser> findInSqlByUserIds(Page page, @Param("str") String str);

    List<SysUser> findInUserIdsContainsDepartmentName(@Param("userIds") List<String> userIds);

    List<SysUser> findPageUserByRoleId(Page page, @Param("roleId") String roleId, @Param("searchName") String searchName);
}