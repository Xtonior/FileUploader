package kz.lab.dbapp.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import kz.lab.dbapp.exception.KafkaException;
import kz.lab.dbapp.model.minio.MinioEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioNotificationListener {
    @KafkaListener(topics = "${kafka.topics.s3-load-events}", containerFactory = "kafkaListenerContainerFactory", groupId = "load-minio-listeners")
    public void handleUploads(ConsumerRecord<String, MinioEvent> record) throws KafkaException {
        MinioEvent event = record.value();
        if (event.getRecords() != null && !event.getRecords().isEmpty()) {
            String fileName = event.getRecords().get(0).getS3().getObject().getKey();
            log.info("Loaded file: {}", fileName);
        } else {
            throw new KafkaException("Failed to parse kafka message");
        }
    }

    @KafkaListener(topics = "${kafka.topics.s3-delete-events}", containerFactory = "kafkaListenerContainerFactory", groupId = "delete-minio-listeners")
    public void handleLifecycle(ConsumerRecord<String, MinioEvent> record) throws KafkaException {
        MinioEvent event = record.value();
        if (event.getRecords() != null && !event.getRecords().isEmpty()) {
            String fileName = event.getRecords().get(0).getS3().getObject().getKey();
            log.info("Deleted file: {}", fileName);
        } else {
            throw new KafkaException("Failed to parse kafka message");
        }
    }
}
