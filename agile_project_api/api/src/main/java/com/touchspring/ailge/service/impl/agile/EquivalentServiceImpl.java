package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.EquivalentDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dto.EquivalentExcelDTO;
import com.touchspring.ailge.entity.agile.Equivalent;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.service.agile.EquivalentService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EquivalentServiceImpl extends ServiceImpl<EquivalentDao, Equivalent> implements EquivalentService {

    @Autowired
    private EquivalentDao equivalentDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 更新或者保存
     * @param equivalent
     * @return
     */
    @Override
    public boolean save(Equivalent equivalent) {
        if(StringUtils.isBlank(equivalent.getId())) {
//            equivalent.setId(IdWorker.nextId());
            equivalentDao.insert(equivalent);
        } else {
            equivalentDao.updateById(equivalent);
        }
        return true;
    }

    @Override
    public Integer getProjectEquivalentBy(String userId) {
        return null;
    }

    /**
     * Excel导入
     * @param list 解析出的数据
     * @param createSysUser 创建用户
     * @return .
     */
    @Override
    public boolean excelImport(List<EquivalentExcelDTO> list, SysUser createSysUser, String month) {
        if(CollectionUtils.isEmpty(list)) return true;
        List<Equivalent> equivalentList = new ArrayList<>();

        for (EquivalentExcelDTO equivalentExcelDTO: list){
            SysUser sysUser = new LambdaQueryChainWrapper<SysUser>(sysUserDao).eq(SysUser::getAccount, equivalentExcelDTO.getAccount()).one();
            if (sysUser == null) continue;
            Equivalent equivalent = new Equivalent();
            equivalent.setCreateUserId(createSysUser.getId());
            equivalent.setUpdateUserId(createSysUser.getId());
            equivalent.setOther(equivalentExcelDTO.getOther() == null? 0: Double.valueOf(equivalentExcelDTO.getOther()));
            equivalent.setDeliveryQuality(equivalentExcelDTO.getDeliveryQuality() == null? 0: Double.valueOf(equivalentExcelDTO.getDeliveryQuality()));
            equivalent.setKnowledge(equivalentExcelDTO.getKnowledge() == null? 0: Double.valueOf(equivalentExcelDTO.getKnowledge()));
            equivalent.setMonths(month);
            equivalent.setSysUserId(sysUser.getId());
            equivalentList.add(equivalent);
        }
        this.saveBatch(equivalentList);
        return true;
    }
}
