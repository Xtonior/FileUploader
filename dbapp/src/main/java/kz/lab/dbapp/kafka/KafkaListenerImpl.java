package kz.lab.dbapp.kafka;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaListenerImpl {
    @KafkaListener(id = "listen1", topics = "${new-load}")
    public void listen1(String in) {
        System.out.println(in);
    }
}
