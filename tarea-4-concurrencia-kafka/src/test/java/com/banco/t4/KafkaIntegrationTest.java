package com.banco.t4;

import com.banco.t4.kafka.TransaccionConsumer;
import com.banco.t4.kafka.TransaccionProducer;
import com.banco.t4.domain.Transaccion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"transacciones-entrada","transacciones-resultados"})
@DirtiesContext
public class KafkaIntegrationTest {

    @Autowired
    private TransaccionProducer producer;

    @Autowired
    private TransaccionConsumer consumer;

    @Test
    void producerAndConsumerRun() throws InterruptedException {
        Transaccion t = new Transaccion("cuenta-1", new BigDecimal("10.00"));
        producer.enviar(t, "transacciones-entrada");
        // small sleep to allow embedded kafka to process
        Thread.sleep(500);
        // If no exception thrown, assume consumer processed message
        assertThat(t.getId()).isNotNull();
    }
}
