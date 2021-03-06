package com.touchspring.ailge.service.sys;

import com.touchspring.ailge.entity.sys.SysDictData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface SysDictDataService extends IService<SysDictData> {

    boolean save(SysDictData sysDictData);

}
