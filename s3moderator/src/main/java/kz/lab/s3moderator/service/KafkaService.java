package kz.lab.s3moderator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kz.lab.s3moderator.exception.KafkaException;
import kz.lab.s3moderator.kafka.KafkaSenderImpl;
import kz.lab.s3moderator.model.SampleEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaService {
    @Autowired
    private final KafkaSenderImpl kafkaSender;

    @Value("${kafka.topics.sample-events}")
    private String sampleEventsTopic;

    public void sendSampleEvent(SampleEvent event) throws KafkaException {
        try {
            kafkaSender.send(sampleEventsTopic, event.getId(), event.getMessage());
        } catch (KafkaException e) {
            throw new KafkaException("KafkaService: Failed to send event", e);
        }
    }
}
