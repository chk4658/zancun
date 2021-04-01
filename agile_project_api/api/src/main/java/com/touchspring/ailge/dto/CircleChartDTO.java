package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Circle;
import lombok.Data;

@Data
public class CircleChartDTO {

    private String dateTime;

    private Integer num;

    private String circleId;

    private Circle circle;

    private String circleName;

}
