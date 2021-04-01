package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.EquivalentExcelDTO;
import com.touchspring.ailge.entity.agile.Equivalent;
import com.touchspring.ailge.entity.sys.SysUser;

import java.util.List;

public interface EquivalentService extends IService<Equivalent> {

    /**
     * 保存或更新
     */
    boolean save(Equivalent equivalent);

    /**
     * 根据用户id 获取项目当量
     */
    Integer getProjectEquivalentBy(String userId);

    boolean excelImport(List<EquivalentExcelDTO> list, SysUser createSysUser, String month);


}
