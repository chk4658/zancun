package com.touchspring.ailge.dto.login;

import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;

@Data
public class UserAuthDTO {
    private String id;
    private String realName;
    private String account;

    public UserAuthDTO() {

    }

    public UserAuthDTO(SysUser sysUser) {
        this.id = sysUser.getId();
        this.realName = sysUser.getRealName();
        this.account = sysUser.getAccount();
    }
}
