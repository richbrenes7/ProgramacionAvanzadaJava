package com.banco.core.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.security.permit-all=false")
class AdminSecurityReportIntegrationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void usuarioVeProductosDeSuClienteYNoAccedeAAdmin() {
        String adminToken = login("admin", "password");

        Map<String, Object> cliente = post("/api/clientes", adminToken, Map.of(
                "nombre", "Cliente Seguridad",
                "documento", "DPI-SEC-1",
                "email", "seguridad@example.com",
                "telefono", "5555-9999",
                "estado", "ACTIVO"));
        Number clienteId = (Number) cliente.get("id");

        String username = "cliente" + System.nanoTime();
        post("/api/admin/usuarios", adminToken, Map.of(
                "username", username,
                "password", "cliente123",
                "role", "USER",
                "nombre", "Usuario Cliente",
                "clienteId", clienteId.longValue()));

        Map<String, Object> producto = post("/api/admin/productos", adminToken, Map.of(
                "clienteId", clienteId.longValue(),
                "tipoCuenta", "AHORROS",
                "moneda", "GTQ",
                "saldoInicial", new BigDecimal("250.00"),
                "estado", "ACTIVO"));

        String userToken = login(username, "cliente123");
        ResponseEntity<List<Map<String, Object>>> productos = rest.exchange(
                url("/api/cuentas/mis-productos"),
                HttpMethod.GET,
                new HttpEntity<>(headers(userToken)),
                new ParameterizedTypeReference<>() {});

        assertThat(productos.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(productos.getBody()).isNotNull();
        assertThat(productos.getBody()).hasSize(1);
        assertThat(productos.getBody().get(0).get("numeroCuenta")).isEqualTo(producto.get("numeroCuenta"));
        assertThat(((Number) productos.getBody().get(0).get("clienteId")).longValue()).isEqualTo(clienteId.longValue());

        ResponseEntity<String> adminDenied = rest.exchange(
                url("/api/admin/usuarios"),
                HttpMethod.GET,
                new HttpEntity<>(headers(userToken)),
                String.class);
        assertThat(adminDenied.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<Map<String, Object>> reporte = rest.exchange(
                url("/api/reportes/operativo"),
                HttpMethod.GET,
                new HttpEntity<>(headers(adminToken)),
                new ParameterizedTypeReference<>() {});
        assertThat(reporte.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reporte.getBody()).containsKeys("resumen", "cartera", "apis");
    }

    private String login(String username, String password) {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                url("/api/auth/login"),
                HttpMethod.POST,
                json(Map.of("username", username, "password", password), null),
                new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return String.valueOf(response.getBody().get("token"));
    }

    private Map<String, Object> post(String path, String token, Map<String, Object> body) {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                url(path),
                HttpMethod.POST,
                json(body, token),
                new ParameterizedTypeReference<>() {});
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private HttpEntity<Map<String, Object>> json(Map<String, Object> body, String token) {
        return new HttpEntity<>(body, headers(token));
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) headers.setBearerAuth(token);
        return headers;
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
