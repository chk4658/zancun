package com.touchspring.ailge.controller.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.CircleLogDao;
import com.touchspring.ailge.entity.agile.CircleLog;
import com.touchspring.ailge.service.agile.CircleLogService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 圈子记录表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@RestController
@RequestMapping("//circle-log")
public class CircleLogController {

    private final CircleLogDao circleLogDao;
    private final CircleLogService circleLogService;

    public CircleLogController(CircleLogDao circleLogDao, CircleLogService circleLogService) {
        this.circleLogDao = circleLogDao;
        this.circleLogService = circleLogService;
    }

    /**
     * 查看圈子所有Log
     */
    @GetMapping("list")
    public ResultData findAllLogByCircleId(@RequestParam(defaultValue = "1", required = false) Integer page,
                                           @RequestParam(defaultValue = "20", required = false) Integer size,
                                           String circleId,
                                           @RequestParam(required = false) String searchName){
        Page<CircleLog> circleLogPage = new Page<>(page, size);
        circleLogPage.setRecords(circleLogDao.findByCircleId(circleLogPage, circleId, searchName));
        return ResultData.ok().putDataValue("circleLogs", circleLogPage.getRecords()).putDataValue("totalAmount", circleLogPage.getTotal());
    }

}
