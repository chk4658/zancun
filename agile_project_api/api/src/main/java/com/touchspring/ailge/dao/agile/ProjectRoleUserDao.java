package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.CircleRoleUser;
import com.touchspring.ailge.entity.agile.ProjectRoleUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 项目角色与人员关系表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
public interface ProjectRoleUserDao extends BaseMapper<ProjectRoleUser> {

    @Delete("DELETE FROM PROJECT_ROLE_USER WHERE PROJECT_ROLE_ID = #{projectRoleId}")
    int deleteByProjectRoleId(@Param("projectRoleId") String projectRoleId);

    @Delete("DELETE FROM PROJECT_ROLE_USER WHERE PROJECT_ROLE_ID = #{projectRoleId} and SYS_USER_ID = #{sysUserId}")
    int deleteByProjectRoleIdAndUserId(@Param("projectRoleId") String projectRoleId, @Param("sysUserId") String sysUserId);


    @Select("select * from PROJECT_ROLE_USER where is_delete = 0 AND PROJECT_ROLE_ID = #{projectRoleId}")
    @Results({
            @Result(column = "SYS_USER_ID", property = "sysUserId"),
            @Result(column = "SYS_USER_ID", property = "sysUsers",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById"))
    })
    List<ProjectRoleUser> findListByProjectRoleId(String projectRoleId);

    @Select("SELECT pru.* FROM PROJECT_ROLE_USER  pru " +
            "LEFT JOIN PROJECT_ROLE pr on pru.PROJECT_ROLE_ID = pr.id WHERE pru.SYS_USER_ID = #{sysUserId} and pr.PROJECT_ID = #{projectId}")
    List<ProjectRoleUser> findListByCircleIdAndUserId(@Param("projectId") String projectId,@Param("sysUserId") String sysUserId);

}
