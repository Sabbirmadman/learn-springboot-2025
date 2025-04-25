package com.lelarn.dreamshops.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        // Add request ID to response header
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);


        // Log request details before processing
        log.info("Request START [ID:{}]: {} {} from {}",
                requestId,
                httpRequest.getMethod(),
                httpRequest.getRequestURI() + (httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : ""),
                httpRequest.getRemoteAddr());

        try {
            // Proceed with the filter chain
            chain.doFilter(request, response);
        } finally {
            // Log response details after processing
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request END   [ID:{}]: Status {} in {}ms",
                    requestId,
                    httpResponse.getStatus(),
                    duration);
        }
    }


}
