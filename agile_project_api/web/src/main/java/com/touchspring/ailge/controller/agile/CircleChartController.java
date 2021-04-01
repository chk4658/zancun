package com.touchspring.ailge.controller.agile;

import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.dao.agile.CircleDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.dto.CircleChartDTO;
import com.touchspring.ailge.entity.agile.Circle;
import com.touchspring.core.mvc.ui.ResultData;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 圈子图表
 */
@RestController
@RequestMapping("//circle-chart")
public class CircleChartController {

    private final CircleDao circleDao;
    private final TaskDao taskDao;

    public CircleChartController(CircleDao circleDao, TaskDao taskDao) {
        this.circleDao = circleDao;
        this.taskDao = taskDao;
    }

    /**
     * 圈子统计 每月新增圈子数
     */
    @GetMapping("newCircleNumPerMonth")
    public ResultData getNewCircleNumPerMonth() {
        List<CircleChartDTO> month = circleDao.getNewCircleNumPerMonth();
        return ResultData.ok().putDataValue("month", month);
    }

    /**
     * 圈子任务数Top10
     */
    @GetMapping("topTenTaskNumInCircle")
    public ResultData getTopTenTaskNumInCircle() {
        List<CircleChartDTO> topTenTaskNumInCircle = circleDao.getTopTenTaskNumInCircle();

        List<Circle> circles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topTenTaskNumInCircle)) {
            topTenTaskNumInCircle.forEach(taskNumInCircle -> {
                Circle circle = circleDao.selectById(taskNumInCircle.getCircleId());
                if (circle != null) {
                    circle.setTaskNum(taskNumInCircle.getNum());
                    circles.add(circle);
                }
            });
        }
        return ResultData.ok().putDataValue("topTenTaskNumInCircle", circles);
    }

    /**
     * 圈子参与人数Top10
     */
    @GetMapping("topTenUserNumInCircle")
    public ResultData getTopTenUserNumInCircle() {
        List<CircleChartDTO> topTenUserNumInCircle = circleDao.getTopTenUserNumInCircle();

        List<Circle> circles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topTenUserNumInCircle)) {
            topTenUserNumInCircle.forEach(userNumInCircle -> {
                Circle circle = circleDao.selectById(userNumInCircle.getCircleId());
                if (circle != null) {
                    circle.setUserNum(userNumInCircle.getNum().longValue());
                    circles.add(circle);
                }
            });
        }
        return ResultData.ok().putDataValue("topTenUserNumInCircle", circles);
    }

    /**
     * 多选圈子比较任务新增情况
     */
    @PostMapping("newTaskNumPerDayInCircleIds")
    public ResultData getNewTaskNumPerDayInCircleIds(String[] circleIds, String startDate, String endDate) {

        if (ArrayUtils.isEmpty(circleIds) || startDate == null || endDate == null) return ResultData.notFound();
        List<CircleChartDTO> perDayInCircleIds = circleDao.getNewTaskNumPerDayInCircleIds(Arrays.asList(circleIds), startDate, endDate);

        perDayInCircleIds.forEach(circleChartDTO -> {
            Circle circle = circleDao.selectById(circleChartDTO.getCircleId());
            circleChartDTO.setCircle(circle);
            circleChartDTO.setCircleName(circle.getName());
        });

//        Map<String, List<CircleChartDTO>> listMap = perDayInCircleIds.stream().collect(Collectors.groupingBy(CircleChartDTO::getCircleName));
        return ResultData.ok().putDataValue("listMap", perDayInCircleIds);
    }

}
