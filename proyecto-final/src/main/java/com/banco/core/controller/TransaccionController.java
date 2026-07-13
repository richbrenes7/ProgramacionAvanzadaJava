package com.banco.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.cuenta.Cuenta;
import com.banco.core.security.Usuario;
import com.banco.core.security.UsuarioService;
import com.banco.core.service.CuentaService;
import com.banco.core.service.ProcesadorTransaccionesService;
import com.banco.core.transaccion.Transaccion;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final CuentaService cuentaService;
    private final ProcesadorTransaccionesService procesador;
    private final UsuarioService usuarioService;

    public TransaccionController(CuentaService cuentaService, ProcesadorTransaccionesService procesador, UsuarioService usuarioService) {
        this.cuentaService = cuentaService;
        this.procesador = procesador;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Void> registrar(@RequestBody Transaccion transaccion, Authentication authentication) {
        validarOrigen(transaccion, authentication);
        cuentaService.transferir(transaccion.getOrigen(), transaccion.getDestino(), transaccion.getMonto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lote")
    public ResponseEntity<Integer> procesarLote(@RequestBody List<Transaccion> lote, Authentication authentication) {
        lote.forEach(transaccion -> validarOrigen(transaccion, authentication));
        procesador.procesarLote(lote);
        return ResponseEntity.ok(lote.size());
    }

    private void validarOrigen(Transaccion transaccion, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return;
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        if ("ADMIN".equalsIgnoreCase(usuario.getRole())) return;
        if (usuario.getClienteId() == null) throw new IllegalArgumentException("El usuario no tiene cliente asociado");
        Cuenta origen = cuentaService.obtenerPorNumero(transaccion.getOrigen());
        if (origen == null || !usuario.getClienteId().equals(origen.getClienteId())) {
            throw new IllegalArgumentException("La cuenta origen no pertenece al usuario autenticado");
        }
    }
}
