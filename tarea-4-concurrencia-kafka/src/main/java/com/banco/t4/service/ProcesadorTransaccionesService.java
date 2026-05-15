package com.banco.t4.service;

import com.banco.t4.domain.ResultadoProcesamiento;
import com.banco.t4.domain.Transaccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class ProcesadorTransaccionesService {

    @Autowired
    private Executor processingExecutor;

    public List<ResultadoProcesamiento> procesarLote(List<Transaccion> transacciones) {
        List<CompletableFuture<ResultadoProcesamiento>> futures = transacciones.stream()
            .map(tx -> CompletableFuture.supplyAsync(() -> procesar(tx), processingExecutor)
                .exceptionally(ex -> ResultadoProcesamiento.error(tx.getId(), ex.getMessage())))
            .collect(Collectors.toList());

        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public ResultadoProcesamiento procesar(Transaccion t) {
        // Simular validación y procesamiento
        if (t.getMonto().doubleValue() < 0) {
            return ResultadoProcesamiento.error(t.getId(), "Monto negativo");
        }
        return ResultadoProcesamiento.success(t.getId());
    }
}
