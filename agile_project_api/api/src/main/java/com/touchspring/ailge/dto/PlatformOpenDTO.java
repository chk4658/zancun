package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import lombok.Data;

import java.util.List;

@Data
public class PlatformOpenDTO {

    List<Project> projects;

    List<ProjectTemplateTask> projectTemplateTasks;
}
