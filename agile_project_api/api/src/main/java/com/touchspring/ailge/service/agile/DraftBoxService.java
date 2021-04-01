package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.DraftBox;
import com.touchspring.ailge.entity.agile.Project;

/**
 * 草稿箱
 */
public interface DraftBoxService extends IService<DraftBox> {

    boolean delete(String id, String userId);

    Project findByDraftBoxId(String draftBoxId);
}
