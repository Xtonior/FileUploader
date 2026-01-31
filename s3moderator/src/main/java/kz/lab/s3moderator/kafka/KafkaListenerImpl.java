package kz.lab.s3moderator.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kz.lab.s3moderator.model.FileLoadEntity;

@Component
public class KafkaListenerImpl {
    @KafkaListener(id = "listen1", topics = "${kafka.topics.db-update-events}")
    public void listen1(FileLoadEntity in) {
        System.out.println(in.getId());
    }
}
