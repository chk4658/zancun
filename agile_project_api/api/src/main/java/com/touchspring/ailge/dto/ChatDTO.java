package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Chat;
import lombok.Data;

import java.util.List;

@Data
public class ChatDTO extends Chat {

    /**
     * @ 的用户ID
     */
    private List<String> atUserIds;
}
