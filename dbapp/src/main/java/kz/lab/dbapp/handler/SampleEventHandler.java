package kz.lab.dbapp.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import kz.lab.dbapp.model.SampleEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = "sample-events-topic")
public class SampleEventHandler {
    @KafkaHandler
    public void handle(SampleEvent event) {
        log.info("Sample Event: {}", event);
    }

    @KafkaHandler
    public void handle(SampleEvent event, Acknowledgment ack) {
        log.info("Sample Event w/ ack: {}", event);

        if (event != null && !event.getMessage().isEmpty()) {
            ack.acknowledge();
        }
    }
}
