package com.touchspring.ailge.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.dto.ExcelData;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.exception.ExcelAnalysisException;
import com.touchspring.ailge.exception.ExcelHeadException;
import com.touchspring.ailge.service.agile.ExcelDataService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 项目Excel导入解析
 **/
public class ExcelListener extends AnalysisEventListener<ExcelData>{

    private static final Logger logger = LoggerFactory.getLogger(ExcelListener.class);


    private ExcelDataService excelDataService;

    private SysUser sysUser;

    private String projectId;

    private Boolean checkBoolean;

    public ExcelListener(ExcelDataService excelDataService,SysUser sysUser,String projectId) {
        this.excelDataService = excelDataService;
        this.sysUser = sysUser;
        this.projectId = projectId;
    }

    public ExcelListener(ExcelDataService excelDataService,Boolean checkBoolean ) {
        this.excelDataService = excelDataService;
        this.checkBoolean = checkBoolean;
    }

    //每次读取100条数据就进行保存操作
    private static final int BATCH_COUNT = 100;

    List<ExcelData> list = new ArrayList<>();

    //角色类型
    List<String> roleList = new ArrayList<>();

    Map<String,List<ExcelData>> map = new HashMap<>();

    String sheetName = "";



    public ExcelListener() {
    }

    @Override
    public void invoke(ExcelData data, AnalysisContext analysisContext) {
        logger.info("解析到一条数据:{}", JSON.toJSONString(data));
        sheetName = analysisContext.readSheetHolder().getSheetName();
        list.add(data);
        //存入项目角色
        roleList.add(data.getReviewProjectRoleId());
        roleList.add(data.getConfirmProjectRoleId());
        //判断是否达到存储状态
        if(checkBoolean != null && checkBoolean)  checkData();
        if((checkBoolean == null || !checkBoolean)&& list.size()>=BATCH_COUNT){
            saveData();
            //保存完毕清除list
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        if (checkBoolean == null || !checkBoolean) {
            saveProjectRole();
            saveData();
            logger.info("所有数据解析完成！");
        }
    }

    //读取表头内容
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if(headMap.keySet().size()!=9){
            throw new ExcelHeadException("文件表头数量有误！");
        }
        System.out.println("表头："+headMap);
    }

    //保存数据库操作
    public void saveData(){
        logger.info("开始存储数据库");
        boolean istrue = true;
        istrue = excelDataService.save(list,sheetName,sysUser,projectId);
        roleList.addAll(list.stream().map(ExcelData::getReviewProjectRoleId).collect(Collectors.toList()));
        roleList.addAll(list.stream().map(ExcelData::getConfirmProjectRoleId).collect(Collectors.toList()));
        list.clear();
        if(!istrue){
            throw new ExcelAnalysisException("文件解析有误！");
        }
    }

    public void checkData() {
        boolean istrue = true;
        istrue = excelDataService.check(list,sheetName);
        if(!istrue){
            throw new ExcelAnalysisException("文件解析有误！");
        }
    }

    //保存project role
    public void saveProjectRole(){
        logger.info("开始存储项目角色关系");
        List<String> collect = roleList.stream().distinct().collect(Collectors.toList());
        excelDataService.saveProjectRole(sysUser,projectId,collect);
        //清除数据
        roleList.clear();
    }

    public static void main(String[] args) throws FileNotFoundException {
//        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\excel导入模板.xlsx");
////        ExcelListener excelListener = new ExcelListener();
//        ExcelReader excelReader = EasyExcel.read(inputStream,ExcelData.class,new ExcelListener()).build();
//        ReadSheet readSheet1 = EasyExcel.readSheet(0).build();
//        ReadSheet readSheet2 = EasyExcel.readSheet(1).build();
//        excelReader.read(readSheet2);
//        //关闭资源
//        excelReader.finish();
    }



}
