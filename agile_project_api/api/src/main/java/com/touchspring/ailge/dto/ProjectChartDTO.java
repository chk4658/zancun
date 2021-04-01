package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Project;
import lombok.Data;

@Data
public class ProjectChartDTO {

    private String dateTime;

    private Integer num;

    private String projectId;

    private String status;

    private Project project;
}
