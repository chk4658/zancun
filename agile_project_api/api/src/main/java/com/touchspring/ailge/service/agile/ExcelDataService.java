package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.dto.ExcelData;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-27 17:17
 **/
public interface ExcelDataService extends IService<ExcelData> {

    boolean save(List<ExcelData> list,String sheetName,SysUser sysUser, String projectId);

    void saveProjectRole(SysUser sysUser,String projectId,List<String> roleList);

    ResultData analysisExcel(SysUser user, String path, String projectId);


    boolean check(List<ExcelData> list,String sheetName);

}
