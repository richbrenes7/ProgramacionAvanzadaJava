package com.banco.t4.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.t4.domain.ResultadoProcesamiento;
import com.banco.t4.domain.Transaccion;
import com.banco.t4.kafka.TransaccionProducer;
import com.banco.t4.service.ProcesadorTransaccionesService;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private ProcesadorTransaccionesService procesador;

    @Autowired
    private TransaccionProducer producer;

    @Value("${app.kafka.topic-transacciones:transacciones-entrada}")
    private String topicTransacciones;

    @PostMapping("/procesar-lote")
    public ResponseEntity<List<ResultadoProcesamiento>> procesar(@RequestBody List<Transaccion> lote) {
        List<ResultadoProcesamiento> resultados = procesador.procesarLote(lote);
        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/publicar")
    public ResponseEntity<Void> publicar(@RequestBody Transaccion transaccion) {
        producer.enviar(transaccion, topicTransacciones);
        return ResponseEntity.accepted().build();
    }
}
