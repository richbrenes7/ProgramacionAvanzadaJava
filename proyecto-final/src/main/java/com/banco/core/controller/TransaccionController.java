package com.banco.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.core.service.CuentaService;
import com.banco.core.service.ProcesadorTransaccionesService;
import com.banco.core.transaccion.Transaccion;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final CuentaService cuentaService;
    private final ProcesadorTransaccionesService procesador;

    public TransaccionController(CuentaService cuentaService, ProcesadorTransaccionesService procesador) {
        this.cuentaService = cuentaService;
        this.procesador = procesador;
    }

    @PostMapping
    public ResponseEntity<Void> registrar(@RequestBody Transaccion transaccion) {
        cuentaService.transferir(transaccion.getOrigen(), transaccion.getDestino(), transaccion.getMonto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lote")
    public ResponseEntity<Integer> procesarLote(@RequestBody List<Transaccion> lote) {
        procesador.procesarLote(lote);
        return ResponseEntity.ok(lote.size());
    }
}
