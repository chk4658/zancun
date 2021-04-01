package com.touchspring.ailge.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createAt", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "updateAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "isDelete", Integer.class, BaseEnums.NO.getCode());
//        this.strictInsertFill(metaObject, "createUserId", String.class, SecurityUtils.getUser());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateAt", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
        this.strictUpdateFill(metaObject, "updateUserId", Date.class, new Date());
    }
}