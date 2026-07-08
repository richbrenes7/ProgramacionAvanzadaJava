/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.movimiento.Movimiento;
import com.banco.core.service.CuentaService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<Cuenta> crear(@RequestBody Cuenta c) {
        return ResponseEntity.ok(cuentaService.crearCuenta(c));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<Cuenta> porNumero(@PathVariable String numero) {
        Cuenta c = cuentaService.obtenerPorNumero(numero);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cuenta>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaService.obtenerPorCliente(clienteId));
    }

    @PostMapping("/numero/{numero}/cliente/{clienteId}")
    public ResponseEntity<Cuenta> asignarCliente(@PathVariable String numero, @PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaService.asignarCuentaACliente(numero, clienteId));
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<BigDecimal> saldo(@PathVariable String numero) {
        Cuenta c = cuentaService.obtenerPorNumero(numero);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c.getSaldo());
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<Movimiento>> movimientos(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.movimientos(id));
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferir(@RequestParam String origen, @RequestParam String destino, @RequestParam BigDecimal monto) {
        cuentaService.transferir(origen, destino, monto);
        return ResponseEntity.ok().build();
    }
}
