package com.banco.core.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.banco.core.cuenta.Cuenta;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostgresKafkaIntegrationIT extends TestcontainersIntegrationSetup {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void servicioLevantaYPuedeCrearCuenta() {
        Cuenta c = new Cuenta();
        c.setClienteId(10L);
        c.setNumeroCuenta("TC-1001");
        c.setTipoCuenta("AHORROS");
        c.setMoneda("GTQ");
        c.setSaldo(java.math.BigDecimal.valueOf(100));
        c.setEstado("ACTIVO");

        ResponseEntity<Cuenta> res = rest.postForEntity("http://localhost:" + port + "/api/cuentas", c, Cuenta.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody().getNumeroCuenta()).isEqualTo("TC-1001");
    }
}
