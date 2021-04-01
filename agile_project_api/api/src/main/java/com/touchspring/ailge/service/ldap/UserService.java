package com.touchspring.ailge.service.ldap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.ldap.User;

public interface UserService {

    /**
     * 查询条目
     * @param sAMAccountName
     * @return
     */
    User findOneBySAMAccountName(String sAMAccountName);

    /**
     * 账号密码验证
     * @param sAMAccountName
     * @param ldapPwd
     * @return
     */
    boolean verifyLDAP(String sAMAccountName,String ldapPwd);

}
