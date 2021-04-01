package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.enums.IsEnabled;
import com.touchspring.ailge.service.sys.SysRoleTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统角色信息表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysRoleTemplateServiceImpl extends ServiceImpl<SysRoleTemplateDao, SysRoleTemplate> implements SysRoleTemplateService {

    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;

    /**
     * 查找角色模板
     * @param page 当前页
     * @param size 页面数量
     * @param enabled 是否禁用 0禁用；1未禁用
     * @return .
     */
    @Override
    public Page<SysRoleTemplate> findAll(Integer page, Integer size, Long enabled) {
        if (enabled == null)
            enabled = IsEnabled.NOT_DISABLED.getCode();
        LambdaQueryWrapper<SysRoleTemplate> queryWrapper = new LambdaQueryWrapper<SysRoleTemplate>().eq(SysRoleTemplate::getEnabled, enabled)
                .orderByAsc(BaseEntity::getCreateAt);
        return sysRoleTemplateDao.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean batchEnabled(String[] roleIds, boolean flag) {
        Arrays.asList(roleIds).forEach(roleId -> {
            SysRoleTemplate sysRoleTemplate = sysRoleTemplateDao.selectById(roleId);
            if (sysRoleTemplate != null) {
                if (flag) sysRoleTemplate.setEnabled(IsEnabled.DISABLE.getCode());
                else sysRoleTemplate.setEnabled(IsEnabled.NOT_DISABLED.getCode());
                sysRoleTemplateDao.updateById(sysRoleTemplate);
            }
        });
        return true;
    }

    /**
     * 搜索
     * @param roleName 需要搜索的值
     * @return .
     */
    @Override
    public List<SysRoleTemplate> searchByPage(Page page, String roleName, Long enabled) {
        return sysRoleTemplateDao.findByRoleNameAndIsEnabled(page, roleName, enabled);
    }

    @Override
    public boolean save(SysRoleTemplate sysRoleTemplate) {
        SysRoleTemplate target;
        if (StringUtils.isNotBlank(sysRoleTemplate.getId())) {
            target = sysRoleTemplateDao.selectById(sysRoleTemplate.getId());
            if (!StringUtils.equals(target.getName(), sysRoleTemplate.getName())) {
                List<SysRoleTemplate> roleTemplates = new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).eq(SysRoleTemplate::getName, sysRoleTemplate.getName()).list();
                if (!CollectionUtils.isEmpty(roleTemplates)) return false;
            }
            sysRoleTemplateDao.updateById(sysRoleTemplate);
        } else {
            List<SysRoleTemplate> roleTemplates = new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).eq(SysRoleTemplate::getName, sysRoleTemplate.getName()).list();
            if (!CollectionUtils.isEmpty(roleTemplates)) return false;
            target = sysRoleTemplate;
//            target.setEnabled(IsEnabled.NOT_DISABLED.getCode());
            sysRoleTemplateDao.insert(target);
        }
        return true;
    }

    @Override
    public List<SysRoleTemplate> findListByRoleNames(List<String> roleNames) {
        return new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).in(SysRoleTemplate::getName, roleNames).list();
    }
}
