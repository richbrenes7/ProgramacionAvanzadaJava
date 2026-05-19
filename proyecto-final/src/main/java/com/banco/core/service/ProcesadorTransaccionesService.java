package com.banco.core.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.banco.core.transaccion.Transaccion;

@Service
public class ProcesadorTransaccionesService {

/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
    private final CuentaService cuentaService;
    private final Executor processingExecutor;

    public ProcesadorTransaccionesService(CuentaService cuentaService, Executor processingExecutor) {
        this.cuentaService = cuentaService;
        this.processingExecutor = processingExecutor;
    }

    public List<Void> procesarLote(List<Transaccion> lote) {
        List<CompletableFuture<Void>> futures = lote.stream().map(tx ->
            CompletableFuture.runAsync(() -> cuentaService.transferir(tx.getOrigen(), tx.getDestino(), tx.getMonto()), processingExecutor)
        ).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream().map(f -> (Void) null).collect(Collectors.toList());
    }
}
