package com.touchspring.ailge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by feng- on 2017/5/24.
 */
@Component
@ConfigurationProperties(prefix = "login")
public class LoginConfig {

    public final static  String SEARCH_SQL = "search_sql";

    public final static  String SEARCH_LDAP = "search_ldap";

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
