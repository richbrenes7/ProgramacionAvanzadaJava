package com.banco.core.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cuenta.CuentaRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final CuentaRepository cuentaRepository;

    public ReporteController(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @GetMapping("/cartera")
    public ResponseEntity<Map<String, Object>> cartera() {
        var cuentas = cuentaRepository.findAll();
        BigDecimal saldoTotal = cuentas.stream()
                .map(c -> c.getSaldo() == null ? BigDecimal.ZERO : c.getSaldo())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
                "totalCuentas", cuentas.size(),
                "saldoTotal", saldoTotal,
                "monedaBase", "GTQ"
        ));
    }
}
