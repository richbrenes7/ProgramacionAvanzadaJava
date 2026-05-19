package com.banco.core.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void loginGeneraToken() {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("password");

        ResponseEntity<LoginResponse> res = rest.postForEntity("http://localhost:" + port + "/api/auth/login", req, LoginResponse.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody().getToken()).isNotEmpty();
    }
}
