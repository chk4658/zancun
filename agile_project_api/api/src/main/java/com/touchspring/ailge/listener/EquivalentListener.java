package com.touchspring.ailge.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.dto.EquivalentExcelDTO;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.exception.ExcelAnalysisException;
import com.touchspring.ailge.exception.ExcelHeadException;
import com.touchspring.ailge.service.agile.EquivalentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 当量Excel解析
 **/
public class EquivalentListener extends AnalysisEventListener<EquivalentExcelDTO>{

    private static final Logger logger = LoggerFactory.getLogger(EquivalentListener.class);


    private EquivalentService equivalentService;

    private SysUser sysUser;

    private String month;

    public EquivalentListener(EquivalentService equivalentService, SysUser sysUser, String month) {
        this.equivalentService = equivalentService;
        this.sysUser = sysUser;
        this.month = month;
    }

    //每次读取100条数据就进行保存操作
    private static final int BATCH_COUNT = 100;

    private List<EquivalentExcelDTO> list = new ArrayList<>();

    public EquivalentListener() {
    }

    @Override
    public void invoke(EquivalentExcelDTO equivalentExcelDTO, AnalysisContext analysisContext) {
        logger.info("解析到一条数据:{}", JSON.toJSONString(equivalentExcelDTO));
        list.add(equivalentExcelDTO);
        //判断是否达到存储状态
        if(list.size()>=BATCH_COUNT){
            saveData();
            //保存完毕清除list
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        logger.info("所有数据解析完成！");

    }

    //读取表头内容
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if(headMap.keySet().size()!=4){
            throw new ExcelHeadException("文件表头数量有误！");
        }
        System.out.println("表头："+headMap);
    }

    //保存数据库操作
    private void saveData(){
        logger.info("开始存储数据库");
        boolean isTrue = equivalentService.excelImport(list, sysUser, month);
        list.clear();
        if(!isTrue){
            throw new ExcelAnalysisException("文件解析有误！");
        }
    }



}
