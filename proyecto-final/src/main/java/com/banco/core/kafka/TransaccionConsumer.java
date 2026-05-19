/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.kafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banco.core.transaccion.Transaccion;

@Component
public class TransaccionConsumer {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicReference<Transaccion> last = new AtomicReference<>();

    @KafkaListener(topics = "transacciones", groupId = "banco-group", autoStartup = "${app.kafka.listener-auto-startup:true}")
    public void listen(Transaccion t) {
        last.set(t);
        latch.countDown();
    }

    public boolean await(long millis) throws InterruptedException {
        return latch.await(millis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public Transaccion getLast() {
        return last.get();
    }
}
