package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.ProjectRoleEquivalent;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 项目角色当量（项目当量）表 Mapper 接口
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectRoleEquivalentDao extends BaseMapper<ProjectRoleEquivalent> {

    Double getProjectEquivalentByUserIdAndMonth(@Param("userId") String userId, @Param("month") String month);

    @Delete("DELETE FROM PROJECT_ROLE_EQUIVALENT WHERE PROJECT_ROLE_ID = #{projectRoleId}")
    int deleteByProjectRoleId(@Param("projectRoleId") String projectRoleId);

}
