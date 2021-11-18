package com.encrypto.filters;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.encrypto.models.ApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiOriginFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(ApiOriginFilter.class);

    private final ApiConfig apiConfig;
    private final ObjectMapper mapper;

    public ApiOriginFilter(ApiConfig apiConfig) {
        this.mapper = new ObjectMapper();
        this.apiConfig = apiConfig;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String reqOrigin = request.getHeader("Origin");
        if (!Strings.isNullOrEmpty(reqOrigin) && reqOrigin.startsWith(apiConfig.getAllowedOrigin())) {
            filterChain.doFilter(request, response);
        } else {
            logger.error("Origin:: " + reqOrigin + " Request URL::" + request.getRequestURL().toString()
                    + " Start Time=" + Instant.now());

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Invalid origin");

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(response.getWriter(), errorDetails);
        }
    }
}
