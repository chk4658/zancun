package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectCollection;

import java.util.List;

/**
 * <p>
 * 项目与人员收藏关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
public interface ProjectCollectionService extends IService<ProjectCollection> {

    boolean addOrCancelCollection(String sysUserId, String projectId, boolean flag);

    List<Project> userFavorites(String sysUserId, String searchName);

}
