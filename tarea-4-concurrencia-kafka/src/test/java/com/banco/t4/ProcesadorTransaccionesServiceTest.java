package com.banco.t4;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banco.t4.domain.Transaccion;
import com.banco.t4.service.ProcesadorTransaccionesService;

@SpringBootTest
public class ProcesadorTransaccionesServiceTest {

    @Autowired
    private ProcesadorTransaccionesService procesador;

    @Test
    void procesaLoteSimple() {
        Transaccion t1 = new Transaccion("cuenta-1", new BigDecimal("100.00"));
        Transaccion t2 = new Transaccion("cuenta-2", new BigDecimal("-10.00"));
        var resultados = procesador.procesarLote(List.of(t1,t2));
        assertEquals(2, resultados.size());
    }
}
