package com.banco.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.banco.api.dto.AuthRequest;
import com.banco.api.security.JwtUtil;

@Service
public class AuthService {

    private final String username;
    private final String password;
    private final JwtUtil jwtUtil;

    public AuthService(
            @Value("${app.auth.username}") String username,
            @Value("${app.auth.password}") String password,
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.username = username;
        this.password = password;
        this.jwtUtil = new JwtUtil(jwtSecret, expirationMinutes);
    }

    public String login(AuthRequest request) {
        if (!username.equals(request.username()) || !password.equals(request.password())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return jwtUtil.generateToken(request.username());
    }
}