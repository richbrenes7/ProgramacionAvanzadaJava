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
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.security.Usuario;
import com.banco.core.security.UsuarioService;
import com.banco.core.service.CuentaService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CuentaService cuentaService;

    public AdminController(UsuarioService usuarioService, CuentaService cuentaService) {
        this.usuarioService = usuarioService;
        this.cuentaService = cuentaService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listar().stream().map(UsuarioResponse::from).toList());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody CrearUsuarioRequest request) {
        Usuario usuario = usuarioService.crear(request.username(), request.password(), request.role(), request.nombre(), request.clienteId());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PostMapping("/usuarios/{id}/cliente/{clienteId}")
    public ResponseEntity<UsuarioResponse> asignarCliente(@PathVariable Long id, @PathVariable Long clienteId) {
        Usuario usuario = usuarioService.asignarCliente(id, clienteId);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PostMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<UsuarioResponse> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        Usuario usuario = usuarioService.resetPassword(id, request.newPassword());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PostMapping("/productos")
    public ResponseEntity<Cuenta> crearProducto(@RequestBody CrearProductoRequest request) {
        Cuenta cuenta = cuentaService.crearProductoParaCliente(
                request.clienteId(),
                request.tipoCuenta(),
                request.moneda(),
                request.saldoInicial(),
                request.estado());
        return ResponseEntity.ok(cuenta);
    }

    public record CrearUsuarioRequest(String username, String password, String role, String nombre, Long clienteId) {}
    public record ResetPasswordRequest(String newPassword) {}
    public record CrearProductoRequest(Long clienteId, String tipoCuenta, String moneda, BigDecimal saldoInicial, String estado) {}

    public record UsuarioResponse(Long id, String username, String role, String nombre, Long clienteId, String estado) {
        static UsuarioResponse from(Usuario usuario) {
            return new UsuarioResponse(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getRole(),
                    usuario.getNombre(),
                    usuario.getClienteId(),
                    usuario.getEstado());
        }
    }
}
