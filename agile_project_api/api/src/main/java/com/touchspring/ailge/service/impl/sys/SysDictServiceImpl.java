package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysDictDataDao;
import com.touchspring.ailge.entity.sys.SysDict;
import com.touchspring.ailge.dao.sys.SysDictDao;
import com.touchspring.ailge.entity.sys.SysDictData;
import com.touchspring.ailge.service.sys.SysDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDict> implements SysDictService {

    @Autowired
    private SysDictDao sysDictDao;
    @Autowired
    private SysDictDataDao sysDictDataDao;

    /**
     * 保存
     * @param sysDict
     * @return
     */
    @Override
    public boolean save(SysDict sysDict) {
        SysDict target;

        if (StringUtils.isNotBlank(sysDict.getId())) {
            target = sysDictDao.selectById(sysDict.getId());
            // 编码、名称 是否修改
            if (!StringUtils.equals(target.getCode(), sysDict.getCode())) {
                LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysDict::getCode, sysDict.getCode());
                List<SysDict> sysDictList = sysDictDao.selectList(queryWrapper);
                if (!CollectionUtils.isEmpty(sysDictList)) return false;
            }
//            if (!StringUtils.equals(target.getName(), sysDict.getName())) {
//                LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(SysDict::getName, sysDict.getName());
//                List<SysDict> sysDictList = sysDictDao.selectList(queryWrapper);
//                if (!CollectionUtils.isEmpty(sysDictList)) return false;
//            }
            target.setCode(sysDict.getCode());
            target.setName(sysDict.getName());
            sysDictDao.updateById(target);
        } else {
            target = sysDict;
            List<SysDict> sysDictList = new LambdaQueryChainWrapper<SysDict>(sysDictDao).eq(SysDict::getCode, sysDict.getCode())
//                    .or().eq(SysDict::getName, sysDict.getName())
                    .list();
            if (!CollectionUtils.isEmpty(sysDictList)) return false;
            sysDictDao.insert(target);
        }
        return true;
    }

    /**
     * 删除
     * @param id 词典ID
     * @return
     */
    @Override
    public boolean delete(String id) {
        //查找相关联 字典数据
        List<SysDictData> sysDictDataList = new LambdaQueryChainWrapper<SysDictData>(sysDictDataDao).eq(SysDictData::getSysDictId, id).list();
        if (!CollectionUtils.isEmpty(sysDictDataList))
            sysDictDataList.forEach(sysDictData -> sysDictDataDao.deleteById(sysDictData.getId()));
        sysDictDao.deleteById(id);
        return true;
    }

    /**
     * 根据ID获取
     * @param id 词典ID
     * @return .
     */
    @Override
    public SysDict findById(String id) {
        SysDict sysDict = sysDictDao.selectById(id);
        if (sysDict == null) return null;
        List<SysDictData> sysDictData = sysDictDataDao.findByDictId(id);
        sysDict.setSysDictDataList(sysDictData);
        return sysDict;
    }
}
