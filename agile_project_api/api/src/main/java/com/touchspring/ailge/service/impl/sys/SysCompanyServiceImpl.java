package com.touchspring.ailge.service.impl.sys;

import com.touchspring.ailge.dao.sys.SysDepartmentDao;
import com.touchspring.ailge.entity.sys.SysCompany;
import com.touchspring.ailge.dao.sys.SysCompanyDao;
import com.touchspring.ailge.entity.sys.SysDepartment;
import com.touchspring.ailge.service.sys.SysCompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 公司信息表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyDao, SysCompany> implements SysCompanyService {

    @Autowired
    private SysCompanyDao sysCompanyDao;
    @Autowired
    private SysDepartmentDao sysDepartmentDao;

    @Override
    public Set<SysCompany> findByFullName(String fullName) {

        //查找相关公司及对应的部门
        Set<SysCompany> allSysCompany = sysCompanyDao.findByFullName(fullName);
        if (!CollectionUtils.isEmpty(allSysCompany)){
            allSysCompany = allSysCompany.stream().peek(sysCompany -> {
                List<SysDepartment> sysDepartmentList = sysDepartmentDao.findByCompanyIdAndFullName(sysCompany.getId(), fullName);
                sysCompany.setDepartments(sysDepartmentList);
            }).collect(Collectors.toSet());
        }

        return allSysCompany;
    }
}
