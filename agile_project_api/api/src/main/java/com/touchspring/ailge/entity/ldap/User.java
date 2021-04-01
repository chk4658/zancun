package com.touchspring.ailge.entity.ldap;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Data
@Entry(base = "DC=SSDTC,DC=com", objectClasses = {"organizationalPerson", "person", "top"})
public class User {

    @Id
    private Name id;

    @Attribute(name = "sAMAccountName")
    private String account;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "name")
    private String name;

    @Attribute(name = "company")
    private String company;

    @Attribute(name = "department")
    private String department;

    @Attribute(name = "telephoneNumber")
    private String telephone;

    @Attribute(name = "mobile")
    private String mobile;

    @Attribute(name = "logonCount")
    private Long loginCount;

}