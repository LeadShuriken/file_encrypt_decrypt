package com.encrypto.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.encrypto.config.ApiConfig;
import com.google.common.base.Strings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component("allowedOriginFilter")
public class ApiOriginFilter extends OncePerRequestFilter {

    @Autowired
    private ApiConfig apiConfig;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String localOrigin = request.getHeader("Origin");
        if (!Strings.isNullOrEmpty(localOrigin) && localOrigin.startsWith(apiConfig.getAllowedOrigin())) {
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException("Origin not allowed");
        }
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().matches(apiConfig.getOriginFilter());
    }
}
