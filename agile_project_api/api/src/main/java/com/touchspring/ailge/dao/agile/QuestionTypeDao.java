package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.dto.CascaderResultDTO;
import com.touchspring.ailge.entity.agile.QuestionType;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionTypeDao extends BaseMapper<QuestionType> {


    @Select("select * from QUESTION_TYPE where is_delete = 0 and PARENT_ID is null Or PARENT_ID = ''")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.QuestionTypeDao.findListByParentId"))
    })
    List<QuestionType> findTreeList();

    @Select("select * from QUESTION_TYPE where is_delete = 0 and PARENT_ID=#{parentId}")
    List<QuestionType> findListByParentId(String parentId);



    @Select("select * from QUESTION_TYPE where is_delete = 0 and PARENT_ID is null Or PARENT_ID = ''")
    List<QuestionType> findParentList();

}
