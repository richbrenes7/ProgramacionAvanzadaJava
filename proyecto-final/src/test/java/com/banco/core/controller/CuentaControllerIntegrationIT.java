package com.banco.core.controller;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.movimiento.Movimiento;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CuentaControllerIntegrationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void crearYBuscarPorNumero() {
        Cuenta c = new Cuenta();
        c.setClienteId(1L);
        c.setNumeroCuenta("ACC-1001");
        c.setTipoCuenta("AHORROS");
        c.setMoneda("GTQ");
        c.setSaldo(BigDecimal.valueOf(1000));
        c.setEstado("ACTIVO");

        ResponseEntity<Cuenta> res = rest.postForEntity("http://localhost:" + port + "/api/cuentas", c, Cuenta.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        Cuenta saved = res.getBody();
        assertThat(saved).isNotNull();

        ResponseEntity<Cuenta> byNum = rest.getForEntity("http://localhost:" + port + "/api/cuentas/numero/ACC-1001", Cuenta.class);
        assertThat(byNum.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(byNum.getBody().getNumeroCuenta()).isEqualTo("ACC-1001");

        ResponseEntity<Movimiento[]> movs = rest.getForEntity("http://localhost:" + port + "/api/cuentas/" + saved.getId() + "/movimientos", Movimiento[].class);
        assertThat(movs.getStatusCode().is2xxSuccessful()).isTrue();
        List<Movimiento> movimientos = List.of(movs.getBody());
        assertThat(movimientos).isNotNull();
    }
}
