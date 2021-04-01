package com.touchspring.ailge.service.impl.ldap;

import com.touchspring.ailge.dao.ldap.UserDao;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.service.ldap.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public User findOneBySAMAccountName(String sAMAccountName) {

        log.info("验证域账号",sAMAccountName);
        ldapTemplate.setIgnorePartialResultException (true);
        List<User> users = ldapTemplate.search(
                query().where("objectclass")
                        .is("person").and("sAMAccountName").is(sAMAccountName),
                (AttributesMapper<User>) attrs -> {
                    User ldapUser = new User();
                    ldapUser.setAccount(attrs.get("sAMAccountName") != null ? attrs.get("sAMAccountName").get().toString() : null);
                    ldapUser.setEmail(attrs.get("mail") != null ? attrs.get("mail").get().toString() : null);
                    ldapUser.setName(attrs.get("name") != null ? attrs.get("name").get().toString() : null);
                    ldapUser.setCompany(attrs.get("company") != null ? attrs.get("company").get().toString() : null);
                    ldapUser.setDepartment(attrs.get("department") != null ? attrs.get("department").get().toString() : null);
                    ldapUser.setTelephone(attrs.get("telephoneNumber") != null ? attrs.get("telephoneNumber").get().toString() : null);
                    ldapUser.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get().toString() : null);
                    ldapUser.setLoginCount(attrs.get("logonCount") != null ? Long.valueOf(attrs.get("logonCount").get().toString())  : null);
                    return ldapUser;
                });
        if (CollectionUtils.isEmpty(users)) return null;
        return users.get(0);
    }

    @Override
    public boolean verifyLDAP(String sAMAccountName, String ldapPwd) {
        DirContext dirContext = null;
        try {
            dirContext = ldapTemplate.getContextSource().getContext("SSDTC\\"+sAMAccountName, ldapPwd);
        } catch (org.springframework.ldap.NamingException e) {
            log.info(e.getMessage(),sAMAccountName,ldapPwd);
            return false;
        } finally {
            LdapUtils.closeContext(dirContext);
        }
        return true;
    }
}
