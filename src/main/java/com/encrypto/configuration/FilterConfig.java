package com.encrypto.configuration;

import com.encrypto.filters.ApiOriginFilter;
import com.encrypto.models.ApiConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfig {

    @Autowired
    private ApiConfig config;

    @Profile("production")
    @Bean
    public FilterRegistrationBean<ApiOriginFilter> logFilter() {
        FilterRegistrationBean<ApiOriginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiOriginFilter(config));
        registrationBean.addUrlPatterns("/" + config.getVersion() + "/*");
        return registrationBean;
    }

    @Profile("development")
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