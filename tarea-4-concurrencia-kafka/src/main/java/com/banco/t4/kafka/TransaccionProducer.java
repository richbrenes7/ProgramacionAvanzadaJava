package com.banco.t4.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.banco.t4.domain.Transaccion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TransaccionProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void enviar(Transaccion t, String topic) {
        try {
            String payload = objectMapper.writeValueAsString(t);
            kafkaTemplate.send(topic, t.getId(), payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
