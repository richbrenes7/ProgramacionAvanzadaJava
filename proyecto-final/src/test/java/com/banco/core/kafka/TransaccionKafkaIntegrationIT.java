package com.banco.core.kafka;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.banco.core.transaccion.Transaccion;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"transacciones"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class TransaccionKafkaIntegrationIT {

    @Autowired
    private TransaccionProducer producer;

    @Autowired
    private TransaccionConsumer consumer;

    @Test
    void enviarYRecibir() throws Exception {
        Transaccion t = new Transaccion();
        t.setOrigen("ACC-1");
        t.setDestino("ACC-2");
        t.setMonto(BigDecimal.valueOf(50));

        producer.enviar(t);
        boolean ok = consumer.await(5000);
        assertThat(ok).isTrue();
        Transaccion received = consumer.getLast();
        assertThat(received).isNotNull();
        assertThat(received.getOrigen()).isEqualTo("ACC-1");
    }
}
