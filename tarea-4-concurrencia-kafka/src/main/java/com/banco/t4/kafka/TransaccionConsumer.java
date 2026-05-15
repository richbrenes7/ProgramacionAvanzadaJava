package com.banco.t4.kafka;

import com.banco.t4.domain.ResultadoProcesamiento;
import com.banco.t4.domain.Transaccion;
import com.banco.t4.service.ProcesadorTransaccionesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransaccionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransaccionConsumer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProcesadorTransaccionesService procesador;

    @Value("${app.kafka.topic-resultados:transacciones-resultados}")
    private String topicResultados;

    @KafkaListener(
            topics = "${app.kafka.topic-transacciones:transacciones-entrada}",
            groupId = "${spring.kafka.consumer.group-id:t4-group}")
    public void listen(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String id = node.path("id").asText("-unknown-");
            log.info("TRANSACCION_RECIBIDA transaccionId={}", id);

            Transaccion transaccion = objectMapper.treeToValue(node, Transaccion.class);
            ResultadoProcesamiento res = procesador.procesar(transaccion);
            kafkaTemplate.send(topicResultados, id, objectMapper.writeValueAsString(res));
            log.info("TRANSACCION_PROCESADA transaccionId={} success={}", id, res.isSuccess());
        } catch (Exception e) {
            log.error("Error procesando mensaje Kafka", e);
        }
    }
}
