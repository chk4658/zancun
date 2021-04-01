package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.CascaderResultDTO;
import com.touchspring.ailge.entity.agile.QuestionType;

import java.util.List;

public interface QuestionTypeService extends IService<QuestionType> {

    /**
     * 更新获取插入
     * @param questionType
     * @param userId
     * @return
     */
    boolean saveOrUpdate(QuestionType questionType,String userId);
    /**
     * 组装级联选择器
     */
    List<CascaderResultDTO> getCascaderQuestionTypes();

    /**
     * 获取所有父类
     * @return
     */
    List<QuestionType> getParentList();

    /**
     * 根据父类获取所有子类
     * @param id
     * @return
     */
    List<QuestionType> getListByParentId(String id);

    /**
     * 获取所有级联
     * @return
     */
    List<QuestionType> getTreeList();



}
