package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.TaskTemplateData;

import java.util.List;

/**
 * <p>
 * 任务数据表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
public interface TaskTemplateDataService extends IService<TaskTemplateData> {

    boolean save(TaskTemplateData taskTemplateData);

    boolean saveList(List<TaskTemplateData> templateDataList);

}
