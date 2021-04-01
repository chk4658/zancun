package com.touchspring.ailge.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.agile.Project;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDTO extends Project {
    /**
     * 选定的部门ID
     */
    @TableField(exist = false)
    private List<String> departmentIds;

    /**
     * 文件路径
     */
    private String filePath;
}
