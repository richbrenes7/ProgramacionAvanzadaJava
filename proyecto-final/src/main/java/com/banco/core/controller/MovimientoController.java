/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.service.MovimientoService;
import com.banco.core.movimiento.Movimiento;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<Movimiento> crear(@RequestBody Movimiento m) {
        return ResponseEntity.ok(movimientoService.registrarMovimiento(m));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> porCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
    }
}
