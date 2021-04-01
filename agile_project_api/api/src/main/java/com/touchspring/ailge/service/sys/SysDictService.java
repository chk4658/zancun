package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysDict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysDictService extends IService<SysDict> {

    boolean save(SysDict sysDict);

    boolean delete(String id);

    SysDict findById(String id);
}
