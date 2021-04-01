package com.touchspring.ailge.controller.agile;

import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.QuestionTypeDao;
import com.touchspring.ailge.dto.CascaderResultDTO;
import com.touchspring.ailge.entity.agile.QuestionType;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.QuestionTypeService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("//question-type")
public class QuestionTypeController {


    private final QuestionTypeService questionTypeService;
    private final QuestionTypeDao questionTypeDao;


    public QuestionTypeController(QuestionTypeService questionTypeService, QuestionTypeDao questionTypeDao) {
        this.questionTypeService = questionTypeService;
        this.questionTypeDao = questionTypeDao;
    }

    /**
     * 查询菜单及子菜单信息 默认正序排列
     */
    @GetMapping("")
    public ResultData findAllMenus(){
        return ResultData.ok().putDataValue("questionTypeList", questionTypeDao.findTreeList());
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(QuestionType questionType,@RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        boolean save = questionTypeService.saveOrUpdate(questionType,user.getId());
        if (save) return ResultData.ok();
        return ResultData.errorRequest();
    }

    /**
     * 删除
     */
    @DeleteMapping("{id}")
    public ResultData delete(@PathVariable("id") String id){
        if (StringUtils.isBlank(id)) return ResultData.ok();
        int i = questionTypeDao.deleteById(id);
        if (i > 0) return ResultData.ok();
        return ResultData.errorRequest();
    }


    @GetMapping("cascader")
    public ResultData getCascaderResult() {
        return ResultData.ok().putDataValue("cascaderResult",questionTypeService.getCascaderQuestionTypes());
    }

    @GetMapping("{id}")
    public ResultData getListById(@PathVariable String id) {
        return ResultData.ok().putDataValue("selectOne",questionTypeDao.selectById(id));
    }


    @GetMapping("getParentList")
    public ResultData getParentList() {
        return ResultData.ok().putDataValue("questionTypeList",questionTypeService.getParentList());
    }


    @GetMapping("getListByParentId")
    public ResultData getListByParentId(String parentId) {
        return ResultData.ok().putDataValue("questionTypeList",questionTypeService.getListByParentId(parentId));
    }

}
