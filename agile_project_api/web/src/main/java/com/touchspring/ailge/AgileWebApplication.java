package com.touchspring.ailge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.touchspring.ailge.dao")
@EnableLdapRepositories
@EnableAsync
public class AgileWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgileWebApplication.class, args);
    }
}
