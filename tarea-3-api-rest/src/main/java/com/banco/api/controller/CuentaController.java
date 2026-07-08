package com.banco.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.api.dto.MovimientoDTO;
import com.banco.api.dto.SaldoDTO;
import com.banco.api.service.CuentaService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<SaldoDTO> obtenerSaldo(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.obtenerSaldo(numeroCuenta));
    }

    @GetMapping("/{numeroCuenta}/movimientos")
    public ResponseEntity<List<MovimientoDTO>> movimientos(
            @PathVariable String numeroCuenta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cuentaService.listarMovimientos(numeroCuenta, desde, hasta, page, size));
    }
}