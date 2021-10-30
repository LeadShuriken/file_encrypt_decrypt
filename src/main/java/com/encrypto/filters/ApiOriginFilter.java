package com.encrypto.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.encrypto.models.ApiConfig;
import com.google.common.base.Strings;

import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiOriginFilter extends OncePerRequestFilter {

    private final ApiConfig apiConfig;

    public ApiOriginFilter(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String localOrigin = request.getHeader("Origin");
        if (!Strings.isNullOrEmpty(localOrigin) && localOrigin.startsWith(apiConfig.getAllowedOrigin())) {
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException("Origin not allowed");
        }
    }
}
