package com.banco.core.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiUsageLoggingFilter extends OncePerRequestFilter {

    private final ApiUsageLogService logService;

    public ApiUsageLoggingFilter(ApiUsageLogService logService) {
        this.logService = logService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (debeRegistrar(request)) {
                logService.registrar(new ApiUsageLog(
                        LocalDateTime.now(),
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        System.currentTimeMillis() - inicio,
                        usuarioActual(),
                        origen(request)));
            }
        }
    }

    private boolean debeRegistrar(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return ruta.startsWith("/api/")
                || ruta.startsWith("/actuator/")
                || ruta.startsWith("/swagger-ui/")
                || ruta.startsWith("/v3/api-docs");
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "anonimo";
        }
        return auth.getName();
    }

    private String origen(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
