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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cliente.Cliente;
import com.banco.core.cliente.ClienteRepository;
import com.banco.core.cuenta.Cuenta;
import com.banco.core.movimiento.Movimiento;
import com.banco.core.security.Usuario;
import com.banco.core.security.UsuarioService;
import com.banco.core.service.CuentaService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;
    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;

    public CuentaController(CuentaService cuentaService, UsuarioService usuarioService, ClienteRepository clienteRepository) {
        this.cuentaService = cuentaService;
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    public ResponseEntity<Cuenta> crear(@RequestBody Cuenta c, Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        if (usuario != null && !"ADMIN".equalsIgnoreCase(usuario.getRole()) && usuario.getClienteId() != null) {
            c.setClienteId(usuario.getClienteId());
        }
        return ResponseEntity.ok(cuentaService.crearCuenta(c));
    }

    @GetMapping("/mis-productos")
    public ResponseEntity<List<Cuenta>> misProductos(Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        if (usuario == null || "ADMIN".equalsIgnoreCase(usuario.getRole()) || usuario.getClienteId() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(cuentaService.obtenerPorCliente(usuario.getClienteId()));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<Cuenta> porNumero(@PathVariable String numero) {
        Cuenta c = cuentaService.obtenerPorNumero(numero);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @GetMapping("/numero/{numero}/destinatario")
    public ResponseEntity<DestinatarioCuentaResponse> destinatario(@PathVariable String numero) {
        Cuenta cuenta = cuentaService.obtenerPorNumero(numero);
        if (cuenta == null) return ResponseEntity.notFound().build();
        String nombre = cuenta.getClienteId() == null ? "Cliente no asignado" : clienteRepository.findById(cuenta.getClienteId())
                .map(Cliente::getNombre)
                .orElse("Cliente no asignado");
        return ResponseEntity.ok(new DestinatarioCuentaResponse(
                cuenta.getNumeroCuenta(),
                nombre,
                cuenta.getTipoCuenta(),
                cuenta.getEstado()));
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

    private Usuario usuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        return usuarioService.buscarPorUsername(authentication.getName());
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferir(@RequestParam String origen, @RequestParam String destino, @RequestParam BigDecimal monto) {
        cuentaService.transferir(origen, destino, monto);
        return ResponseEntity.ok().build();
    }

    public record DestinatarioCuentaResponse(String numeroCuenta, String nombreCliente, String tipoCuenta, String estado) {}
}
