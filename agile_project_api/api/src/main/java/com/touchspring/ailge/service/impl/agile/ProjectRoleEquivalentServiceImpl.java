package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectRoleEquivalentDao;
import com.touchspring.ailge.dto.RoleEquivalentDTO;
import com.touchspring.ailge.entity.agile.ProjectRoleEquivalent;
import com.touchspring.ailge.service.agile.ProjectRoleEquivalentService;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectRoleEquivalentServiceImpl extends ServiceImpl<ProjectRoleEquivalentDao, ProjectRoleEquivalent> implements ProjectRoleEquivalentService {

    @Autowired
    private ProjectRoleEquivalentDao projectRoleEquivalentDao;

    /**
     * 更新和保存项目当量
     * @param projectRoleId 项目角色ID
     * @param roleEquivalent 角色当量信息
     * @return .
     */
    @Override
    public boolean updateAndSave(String projectRoleId, String roleEquivalent) {
        projectRoleEquivalentDao.deleteByProjectRoleId(projectRoleId);
        // 保存项目当量
        if (StringUtils.isNotBlank(roleEquivalent)){
            JSONArray jsonArray= JSONArray.parseArray(roleEquivalent);
            List<ProjectRoleEquivalent> projectRoleEquivalents = new ArrayList<>();
            for(Object object : jsonArray){
                //通过下标获取json数组中的数据
                RoleEquivalentDTO roleEquivalentDTO = JSON.toJavaObject((JSON) object, RoleEquivalentDTO.class);
                ProjectRoleEquivalent projectRoleEquivalent = new ProjectRoleEquivalent();
                projectRoleEquivalent.setProjectRoleId(projectRoleId);
                projectRoleEquivalent.setMonths(roleEquivalentDTO.getTime());
                projectRoleEquivalent.setValue(roleEquivalentDTO.getEquivalent() == null? 0: roleEquivalentDTO.getEquivalent());
                projectRoleEquivalents.add(projectRoleEquivalent);
            }
            this.saveBatch(projectRoleEquivalents);
        }
        return true;
    }

    /**
     * 根据项目角色ID获取项目当量并拼接
     * @param projectRoleId 项目角色ID
     * @return .
     */
    @Override
    public String getByProjectRoleId(String projectRoleId) {
        List<ProjectRoleEquivalent> roleEquivalents = new LambdaQueryChainWrapper<ProjectRoleEquivalent>(projectRoleEquivalentDao).eq(ProjectRoleEquivalent::getProjectRoleId, projectRoleId).orderByAsc(ProjectRoleEquivalent::getMonths).list();
        if (CollectionUtils.isEmpty(roleEquivalents)) return null;
        JSONArray array = new JSONArray();
        for (ProjectRoleEquivalent roleEquivalent: roleEquivalents) {
            RoleEquivalentDTO roleEquivalentDTO = new RoleEquivalentDTO();
            roleEquivalentDTO.setTime(roleEquivalent.getMonths());
            roleEquivalentDTO.setEquivalent(roleEquivalent.getValue());
            array.add(JSONObject.toJSON(roleEquivalentDTO));
        }
        return array.toString();
    }
}
