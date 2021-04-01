package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysCompany;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 公司信息表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysCompanyService extends IService<SysCompany> {

    Set<SysCompany> findByFullName(String fullName);

}
