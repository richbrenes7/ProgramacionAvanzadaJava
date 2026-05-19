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
public class MovimientoControllerIntegrationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void depositarYVerificarSaldo() {
        Cuenta c = new Cuenta();
        c.setClienteId(1L);
        c.setNumeroCuenta("ACC-2001");
        c.setTipoCuenta("CORRIENTE");
        c.setMoneda("GTQ");
        c.setSaldo(BigDecimal.valueOf(500));
        c.setEstado("ACTIVO");

        ResponseEntity<Cuenta> res = rest.postForEntity("http://localhost:" + port + "/api/cuentas", c, Cuenta.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        Cuenta saved = res.getBody();
        assertThat(saved).isNotNull();

        Movimiento m = new Movimiento();
        m.setCuentaId(saved.getId());
        m.setTipoMovimiento("DEPOSITO");
        m.setMonto(BigDecimal.valueOf(250));

        ResponseEntity<Movimiento> movRes = rest.postForEntity("http://localhost:" + port + "/api/movimientos", m, Movimiento.class);
        assertThat(movRes.getStatusCode().is2xxSuccessful()).isTrue();
        Movimiento savedMov = movRes.getBody();
        assertThat(savedMov).isNotNull();
        assertThat(savedMov.getMonto()).isEqualTo(BigDecimal.valueOf(250));

        ResponseEntity<Cuenta> updated = rest.getForEntity("http://localhost:" + port + "/api/cuentas/numero/ACC-2001", Cuenta.class);
        assertThat(updated.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updated.getBody().getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(750));

        ResponseEntity<Movimiento[]> movs = rest.getForEntity("http://localhost:" + port + "/api/movimientos/cuenta/" + saved.getId(), Movimiento[].class);
        List<Movimiento> movimientos = List.of(movs.getBody());
        assertThat(movimientos).isNotEmpty();
    }
}
