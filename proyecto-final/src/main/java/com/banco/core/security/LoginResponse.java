/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.security;

public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private Long clienteId;

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public LoginResponse(String token, String username, String role) {
        this(token, username, role, null);
    }

    public LoginResponse(String token, String username, String role, Long clienteId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.clienteId = clienteId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
