package com.touchspring.ailge.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Data
public class BaseEntity implements Serializable {

    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "CREATE_USER_ID", fill = FieldFill.INSERT, jdbcType = VARCHAR) // 新增执行
    private String createUserId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8:00")
    @TableField(value = "CREATE_AT", fill = FieldFill.INSERT)
    private Date createAt;

    @TableField(value = "UPDATE_USER_ID", fill = FieldFill.INSERT_UPDATE, jdbcType = VARCHAR) // 新增和更新执行
    private String updateUserId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8:00")
    @TableField(value = "UPDATE_AT", fill = FieldFill.INSERT_UPDATE)
    private Date updateAt;

    @TableField(value = "IS_DELETE", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDelete;
}
