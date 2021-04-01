package com.touchspring.ailge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentDTO {

    private String milestoneName;

    private String taskName;

    private String reviewUser;

    private String confirmUser;

    private LocalDateTime estEndTime;

    private String parentTaskName;

}
