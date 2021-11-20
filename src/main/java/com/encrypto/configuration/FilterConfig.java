package com.encrypto.configuration;

import com.encrypto.filters.ApiOriginFilter;
import com.encrypto.models.ApiConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfig {

    @Autowired
    private ApiConfig config;

    @Bean
    public FilterRegistrationBean<ApiOriginFilter> logFilter() {
        FilterRegistrationBean<ApiOriginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiOriginFilter());
        registrationBean.addUrlPatterns("/" + config.getVersion() + "/*");
        return registrationBean;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/" + config.getVersion() + "/*").allowedOrigins(config.getAllowedOrigin())
                        .allowedMethods("POST");
            }
        };
    }
}