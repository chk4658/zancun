package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.QuestionTypeDao;
import com.touchspring.ailge.dto.CascaderResultDTO;
import com.touchspring.ailge.entity.agile.QuestionType;
import com.touchspring.ailge.service.agile.QuestionTypeService;
import com.touchspring.ailge.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionTypeServiceImpl extends ServiceImpl<QuestionTypeDao,QuestionType> implements QuestionTypeService {

    @Autowired
    private QuestionTypeDao questionTypeDao;

    @Override
    public boolean saveOrUpdate(QuestionType questionType, String userId) {
        questionType.setUpdateAt(new Date());
        questionType.setUpdateUserId(userId);
        questionType.setIsDelete(0);
        if (StringUtils.isEmpty(questionType.getId())) {
            questionType.setCreateAt(new Date());
            questionType.setCreateUserId(userId);
            questionType.setId(IdWorker.nextId());
            questionTypeDao.insert(questionType);
        } else  questionTypeDao.updateById(questionType);
        return true;
    }

    @Override
    public List<CascaderResultDTO> getCascaderQuestionTypes() {
        List<QuestionType> questionTypes = questionTypeDao.findTreeList();
        return questionTypes.stream().map(questionType -> {
            CascaderResultDTO cascaderResultDTO = new CascaderResultDTO();
            cascaderResultDTO.setLabel(questionType.getName());
            cascaderResultDTO.setValue(questionType.getId());
            if (!CollectionUtils.isEmpty(questionType.getChildren())) {
                cascaderResultDTO.setChildren( questionType.getChildren().stream().map(qt -> {
                    CascaderResultDTO cascaderResultDTO1 = new CascaderResultDTO();
                    cascaderResultDTO1.setValue(qt.getId());
                    cascaderResultDTO1.setLabel(qt.getName());
                    return cascaderResultDTO1;
                }).collect(Collectors.toList()));
            }
            return cascaderResultDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionType> getParentList() {
        return questionTypeDao.findParentList();
    }

    @Override
    public List<QuestionType> getListByParentId(String id) {
        return questionTypeDao.findListByParentId(id);
    }

    @Override
    public List<QuestionType> getTreeList() {
        return questionTypeDao.findTreeList();
    }


}
