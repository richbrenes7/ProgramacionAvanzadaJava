package com.banco.core.service;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.transaccion.Transaccion;

@SpringBootTest
public class ProcesadorTransaccionesServiceIT {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private ProcesadorTransaccionesService processor;

    @Test
    void procesaVariosEnParalelo() {
        Cuenta a = new Cuenta();
        a.setClienteId(1L);
        a.setNumeroCuenta("BATCH-1");
        a.setMoneda("GTQ");
        a.setSaldo(BigDecimal.valueOf(1000));
        a.setEstado("ACTIVO");
        cuentaService.crearCuenta(a);

        Cuenta b = new Cuenta();
        b.setClienteId(2L);
        b.setNumeroCuenta("BATCH-2");
        b.setMoneda("GTQ");
        b.setSaldo(BigDecimal.valueOf(100));
        b.setEstado("ACTIVO");
        cuentaService.crearCuenta(b);

        Transaccion t1 = new Transaccion();
        t1.setOrigen("BATCH-1");
        t1.setDestino("BATCH-2");
        t1.setMonto(BigDecimal.valueOf(200));

        Transaccion t2 = new Transaccion();
        t2.setOrigen("BATCH-1");
        t2.setDestino("BATCH-2");
        t2.setMonto(BigDecimal.valueOf(300));

        processor.procesarLote(List.of(t1, t2));

        Cuenta updatedA = cuentaService.obtenerPorNumero("BATCH-1");
        Cuenta updatedB = cuentaService.obtenerPorNumero("BATCH-2");

        assertThat(updatedA.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(updatedB.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(600));
    }
}
