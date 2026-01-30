package kz.lab.dbapp.kafka;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import kz.lab.dbapp.exception.KafkaException;

public class KafkaSenderImpl {
    private final KafkaTemplate<String, String> template;

    public KafkaSenderImpl(KafkaTemplate<String, String> template) {
        this.template = template;
    }

    public void send(final String topic, final String data) throws KafkaException {
        final ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, data);

        CompletableFuture<SendResult<String, String>> future = template.send(record);

        try {
            future.whenComplete((result, ex) -> {
                System.out.println("Sended kafka message");
            });
        } catch (Exception e) {
            throw new KafkaException("Kafka failure", e);
        }
    }
}
