package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.ProjectDepartment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目与部门关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
public interface ProjectDepartmentDao extends BaseMapper<ProjectDepartment> {

    @Results({
            @Result(column = "SYS_DEPARTMENT_ID", property = "sysDepartmentId"),
            @Result(column = "SYS_DEPARTMENT_ID", property = "sysDepartment",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysDepartmentDao.selectById")),
    })
    @Select("select * from PROJECT_DEPARTMENT where PROJECT_ID = #{projectId} and IS_DELETE = 0")
    List<ProjectDepartment> findByProjectId(@Param("projectId") String projectId);

}
