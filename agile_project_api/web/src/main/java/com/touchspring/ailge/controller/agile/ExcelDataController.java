package com.touchspring.ailge.controller.agile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.ProjectMilestoneDao;
import com.touchspring.ailge.dao.agile.ProjectRoleDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dto.ExcelData;
import com.touchspring.ailge.entity.agile.Message;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.LogTypeEnum;
import com.touchspring.ailge.exception.ExcelAnalysisException;
import com.touchspring.ailge.listener.ExcelListener;
import com.touchspring.ailge.service.agile.ExcelDataService;
import com.touchspring.ailge.service.agile.ProjectLogService;
import com.touchspring.ailge.service.impl.agile.ProjectMilestoneServiceImpl;
import com.touchspring.ailge.service.impl.agile.TaskServiceImpl;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-28 10:15
 **/
@RestController
@RequestMapping("/excel-data")
@Slf4j
public class ExcelDataController {

    @Autowired
    private ExcelDataService excelDataService;

    /**
     * 项目Excel导入
     * 解析excel文件存库
     */
    @GetMapping(value = "analysisExcel")
    public ResultData AnalysisExcel(@RequestHeader(SystemConfig.TOKEN) String token, String path, String projectId){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        return excelDataService.analysisExcel(user, path, projectId);
    }

    /**
     * 项目Excel导入
     * 解析excel文件存库
     */
    @GetMapping(value = "checkExcel")
    public ResultData checkExcel(String path){
        String realPath = new File("").getAbsolutePath();
        try {
            InputStream inputStream = new FileInputStream(realPath + File.separator + path);
            ExcelReader excelReader = EasyExcel.read(inputStream, ExcelData.class, new ExcelListener(excelDataService,true)).doReadAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultData("100", e.getMessage());
        }
        return ResultData.ok();
    }



}
