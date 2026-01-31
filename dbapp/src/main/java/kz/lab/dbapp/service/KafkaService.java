package kz.lab.dbapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kz.lab.dbapp.exception.KafkaException;
import kz.lab.dbapp.kafka.KafkaSenderImpl;
import kz.lab.dbapp.model.DbUpdateEvent;
import kz.lab.dbapp.model.SampleEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaService {
    @Autowired
    private final KafkaSenderImpl kafkaSender;

    @Value("${kafka.topics.sample-events}")
    private String sampleEventsTopic;

    @Value("${kafka.topics.db-update-events}")
    private String dbUpdateEventsTopic;

    public void sendSampleEvent(SampleEvent event) throws KafkaException {
        try {
            kafkaSender.send(sampleEventsTopic, event.getId(), event.getMessage());
        } catch (KafkaException e) {
            throw new KafkaException("KafkaService: Failed to send event", e);
        }
    }

    public void sendDbUpdateEvent(DbUpdateEvent event) throws KafkaException {
        try {
            kafkaSender.send(dbUpdateEventsTopic, event.getId(), event.getData());
        } catch (KafkaException e) {
            throw new KafkaException("KafkaService: Failed to send event", e);
        }
    }
}
