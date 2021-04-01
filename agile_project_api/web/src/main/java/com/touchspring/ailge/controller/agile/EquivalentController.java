package com.touchspring.ailge.controller.agile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.dao.agile.EquivalentDao;
import com.touchspring.ailge.dto.EquivalentExcelDTO;
import com.touchspring.ailge.entity.agile.Equivalent;
import com.touchspring.ailge.entity.agile.ProjectRoleUser;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.listener.EquivalentListener;
import com.touchspring.ailge.service.agile.EquivalentService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("//equivalent")
@Slf4j
public class EquivalentController {

    @Autowired
    private EquivalentDao equivalentDao;


    @Autowired
    private EquivalentService equivalentService;

    /**
     * 查找 根据名称 / 时间跨度
     */
    @GetMapping("list")
    public ResultData list(@RequestHeader(SystemConfig.TOKEN) String token,
                           @RequestParam(defaultValue = "1", required = false) Integer page,
                           @RequestParam(defaultValue = "20", required = false) Integer size,
                           @RequestParam(required = false) Equivalent equivalent,
                           @RequestParam(required = false) String searchName,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        Page<Equivalent> equivalentPage = new Page<>(page,size);
        equivalentPage.setRecords(equivalentDao.findList(equivalentPage, equivalent, searchName, startDate, endDate));
        return ResultData.ok().putDataValue("equivalents",equivalentPage.getRecords()).putDataValue("totalAmount",equivalentPage.getTotal());
    }

    /**
     * 根据ID获取
     */
    @GetMapping("{id}")
    public ResultData getById(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("equivalent",equivalentDao.findById(id));
    }

    /**
     * 保存/更新
     */
    @PostMapping("")
    public ResultData saveOrUpdate(Equivalent equivalent, @RequestHeader(SystemConfig.TOKEN) String token){
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        equivalent.setCreateUserId(user.getId());
        equivalent.setUpdateUserId(user.getId());
        equivalentService.save(equivalent);
        return ResultData.ok();
    }

    /**
     * Excel导入
     * @param token token
     * @param path 文件路径
     * @param months 月份
     * @return .
     */
    @PostMapping(value = "excelImport")
    public ResultData AnalysisExcel(@RequestHeader(SystemConfig.TOKEN) String token, String path, String months){
        if (StringUtils.isBlank(months)) return ResultData.errorRequest();
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        String realPath = new File("").getAbsolutePath();
        try {
            InputStream inputStream = new FileInputStream(realPath + File.separator + path);
            ExcelReader excelReader = EasyExcel.read(inputStream, EquivalentExcelDTO.class, new EquivalentListener(equivalentService, user, months)).doReadAll();
            //关闭资源
            excelReader.finish();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultData.errorRequest();
        }
        return ResultData.ok();
    }




}
