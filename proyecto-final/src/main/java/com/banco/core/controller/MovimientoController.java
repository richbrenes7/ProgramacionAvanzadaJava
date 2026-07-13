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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.cuenta.CuentaRepository;
import com.banco.core.movimiento.Movimiento;
import com.banco.core.security.Usuario;
import com.banco.core.security.UsuarioService;
import com.banco.core.service.MovimientoService;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final CuentaRepository cuentaRepository;
    private final UsuarioService usuarioService;

    public MovimientoController(MovimientoService movimientoService, CuentaRepository cuentaRepository, UsuarioService usuarioService) {
        this.movimientoService = movimientoService;
        this.cuentaRepository = cuentaRepository;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Movimiento> crear(@RequestBody Movimiento m, Authentication authentication) {
        validarCuenta(m.getCuentaId(), authentication);
        if ("DEPOSITO".equalsIgnoreCase(m.getTipoMovimiento()) && !esAdmin(authentication)) {
            throw new IllegalArgumentException("Los depositos se generan por transferencias recibidas u operaciones administrativas");
        }
        return ResponseEntity.ok(movimientoService.registrarMovimiento(m));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> porCuenta(@PathVariable Long cuentaId, Authentication authentication) {
        validarCuenta(cuentaId, authentication);
        return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
    }

    private void validarCuenta(Long cuentaId, Authentication authentication) {
        if (esAdmin(authentication)) return;
        Usuario usuario = usuarioActual(authentication);
        if (usuario.getClienteId() == null) throw new IllegalArgumentException("El usuario no tiene cliente asociado");
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
        if (!usuario.getClienteId().equals(cuenta.getClienteId())) {
            throw new IllegalArgumentException("La cuenta no pertenece al usuario autenticado");
        }
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication == null || authentication.getName() == null || usuarioActual(authentication).getRole().equalsIgnoreCase("ADMIN");
    }

    private Usuario usuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Sesion requerida");
        return usuarioService.buscarPorUsername(authentication.getName());
    }
}
