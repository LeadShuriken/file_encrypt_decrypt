package com.encrypto.configuration;

import com.encrypto.filters.ApiOriginFilter;
import com.encrypto.models.ApiConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private ApiConfig config;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
    }

    @Bean
    @ConditionalOnProperty(prefix = "api", name = "filterOrigin", havingValue = "true")
    public FilterRegistrationBean<ApiOriginFilter> logFilter() {
        FilterRegistrationBean<ApiOriginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiOriginFilter(config));
        registrationBean.addUrlPatterns("/" + config.getVersion() + "/*");
        return registrationBean;
    }
}