/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.banco.core.transaccion.Transaccion;

@Component
public class TransaccionProducer {

    private final KafkaTemplate<String, Transaccion> kafkaTemplate;

    public TransaccionProducer(KafkaTemplate<String, Transaccion> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviar(Transaccion t) {
        String key = String.valueOf(System.currentTimeMillis());
        kafkaTemplate.send("transacciones", key, t);
    }
}
