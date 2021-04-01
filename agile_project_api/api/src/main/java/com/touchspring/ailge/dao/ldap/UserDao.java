package com.touchspring.ailge.dao.ldap;

import com.touchspring.ailge.entity.ldap.User;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.naming.Name;

@Component
public interface UserDao extends LdapRepository<User> {
}