package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.entity.sys.SysDictData;
import com.touchspring.ailge.dao.sys.SysDictDataDao;
import com.touchspring.ailge.service.sys.SysDictDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataDao, SysDictData> implements SysDictDataService {

    @Autowired
    private SysDictDataDao sysDictDataDao;

    /**
     * 保存
     */
    @Override
    public boolean save(SysDictData sysDictData) {
        if (StringUtils.isEmpty(sysDictData.getName())) return true;
        SysDictData target;
        if (StringUtils.isNotBlank(sysDictData.getId())){
            target = sysDictDataDao.selectById(sysDictData.getId());

            //判断是否已存在相关词典命名
//            if (!StringUtils.equals(target.getCode(), sysDictData.getCode())) {
//                LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(SysDictData::getSysDictId, sysDictData.getSysDictId()).eq(SysDictData::getCode, sysDictData.getCode());
//                List<SysDictData> sysDictDataList = sysDictDataDao.selectList(queryWrapper);
//                if (!CollectionUtils.isEmpty(sysDictDataList)) return false;
//            }
//            if (!StringUtils.equals(target.getName(), sysDictData.getName())) {
//                LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(SysDictData::getSysDictId, sysDictData.getSysDictId()).eq(SysDictData::getName, sysDictData.getName());
//                List<SysDictData> sysDictDataList = sysDictDataDao.selectList(queryWrapper);
//                if (!CollectionUtils.isEmpty(sysDictDataList)) return false;
//            }
            target.setCode(sysDictData.getCode());
            target.setName(sysDictData.getName());
            target.setColor(sysDictData.getColor());
            sysDictDataDao.updateById(target);
        } else {
            //查看是否已存在该词典
            target = sysDictData;
//            List<SysDictData> sysDictDataList = new LambdaQueryChainWrapper<SysDictData>(sysDictDataDao).eq(SysDictData::getSysDictId, sysDictData.getSysDictId()).and(i -> i.nested(
//                    j -> j.eq(SysDictData::getCode, sysDictData.getCode())).or(j -> j.eq(SysDictData::getName, sysDictData.getName()))).list();
//            if (!CollectionUtils.isEmpty(sysDictDataList)) return false;
            sysDictDataDao.insert(target);
        }
        return true;
    }
}
