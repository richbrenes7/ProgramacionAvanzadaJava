package com.banco.t4.controller;

import com.banco.t4.domain.ResultadoProcesamiento;
import com.banco.t4.domain.Transaccion;
import com.banco.t4.service.ProcesadorTransaccionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private ProcesadorTransaccionesService procesador;

    @PostMapping("/procesar-lote")
    public ResponseEntity<List<ResultadoProcesamiento>> procesar(@RequestBody List<Transaccion> lote) {
        List<ResultadoProcesamiento> resultados = procesador.procesarLote(lote);
        return ResponseEntity.ok(resultados);
    }
}
