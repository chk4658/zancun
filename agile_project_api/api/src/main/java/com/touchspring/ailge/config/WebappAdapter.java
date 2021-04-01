package com.touchspring.ailge.config;

import com.touchspring.ailge.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 拦截
 */
@Configuration
@Transactional
public class WebappAdapter implements WebMvcConfigurer     {

    private static List<String> EXCLUDE_PATHS = new ArrayList<>();

    {
        EXCLUDE_PATHS.add("/login");
        EXCLUDE_PATHS.add("/test/*");
        EXCLUDE_PATHS.add("/data/**");
        EXCLUDE_PATHS.add("/sys-dict");
        EXCLUDE_PATHS.add("/upload");
        EXCLUDE_PATHS.add("/project-milestone/*");
        EXCLUDE_PATHS.add("/redis-syn-test/*");
        EXCLUDE_PATHS.add("/testPreview");
    }

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;


    /**
     * 注册拦截器s
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 添加拦截url，     excludePathPatterns 排除拦截url
//        registry.addInterceptor(new AuthenticationInterceptor()).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATHS);
//        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String realPath = new File("").getAbsolutePath();
        registry.addResourceHandler("/data/**").addResourceLocations("file:" + realPath + "/data/");
    }

    @PostConstruct
    public void initEditableAvlidation() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)handlerAdapter.getWebBindingInitializer();
        if(initializer.getConversionService()!=null) {
            GenericConversionService genericConversionService = (GenericConversionService)initializer.getConversionService();
            genericConversionService.addConverter(new StringToDateConverter());
        }
    }
}


