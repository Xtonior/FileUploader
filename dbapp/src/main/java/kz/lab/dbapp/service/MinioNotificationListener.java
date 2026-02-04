package kz.lab.dbapp.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import kz.lab.dbapp.entity.FileLoadEntity;
import kz.lab.dbapp.exception.KafkaException;
import kz.lab.dbapp.model.minio.MinioEvent;
import kz.lab.dbapp.model.minio.MinioRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioNotificationListener {
    @Autowired
    FileService fileService;

    @KafkaListener(topics = "${kafka.topics.s3-load-events}", containerFactory = "kafkaListenerContainerFactory", groupId = "load-minio-listeners")
    public void handleUploads(ConsumerRecord<String, MinioEvent> record) throws KafkaException {
        MinioEvent event = record.value();

        if (event.getRecords() == null || event.getRecords().isEmpty()) {
            throw new KafkaException("Failed to parse kafka message");
        }

        Flux.fromIterable(event.getRecords())
                .map(this::ParseMinioEvent)
                .flatMap(entity -> checkDuplicate(entity)
                        .flatMap(isDuplicate -> {
                            if (isDuplicate) {
                                log.info("Duplicate found for: {}", entity.getName());
                                return Mono.empty();
                            } else {
                                return fileService.create(entity)
                                        .doOnSuccess(saved -> log.info("Successfully saved: {}", saved.getName()));
                            }
                        }))
                .collectList()
                .block();
    }

    @KafkaListener(topics = "${kafka.topics.s3-delete-events}", containerFactory = "kafkaListenerContainerFactory", groupId = "delete-minio-listeners")
    public void handleLifecycle(ConsumerRecord<String, MinioEvent> record) {
        MinioEvent event = record.value();
        for (MinioRecord rec : event.getRecords()) {
            FileLoadEntity entity = ParseMinioEvent(rec);

            if (entity.getUserGuid() != null && entity.getName() != null) {
                log.info("Starting DB deletion for: {} with UUID: {}", entity.getName(), entity.getUserGuid());

                fileService.deleteByNameAndGuid(entity.getName(), entity.getUserGuid())
                        .doOnSuccess(v -> log.info("Successfully deleted from DB: {}", entity.getName()))
                        .doOnError(err -> log.error("DB delete Error: {}", err.getMessage()))
                        .subscribe();
            } else {
                log.warn("Skipping DB deletion. Reason: UserGuid is {}, Name is {}",
                        entity.getUserGuid(), entity.getName());
            }
        }
    }

    private FileLoadEntity ParseMinioEvent(MinioRecord record) {
        var obj = record.getS3().getObject();
        String rawKey = obj.getKey();

        String fullKey = URLDecoder.decode(rawKey, StandardCharsets.UTF_8);
        log.info("Decoded key: {}", fullKey);

        FileLoadEntity entity = new FileLoadEntity();

        entity.setLink(fullKey);

        String[] parts = fullKey.split("/");

        for (String part : parts) {
            try {
                entity.setUserGuid(UUID.fromString(part.trim()));
            } catch (IllegalArgumentException e) {

            }
        }

        if (parts.length > 0) {
            entity.setName(parts[parts.length - 1]);
        }

        var metadata = obj.getUserMetadata();
        if (metadata != null) {
            String metaUuid = metadata.getOrDefault("x-amz-meta-user-guid", metadata.get("user-guid"));
            if (metaUuid != null && entity.getUserGuid() == null) {
                entity.setUserGuid(UUID.fromString(metaUuid));
            }

            String dateStr = metadata.get("x-amz-meta-date");
            if (dateStr != null) {
                try {
                    entity.setUploadDate(LocalDateTime.parse(dateStr));
                } catch (Exception e) {
                    entity.setUploadDate(LocalDateTime.now());
                }
            }
        }

        if (entity.getUploadDate() == null) {
            entity.setUploadDate(LocalDateTime.now());
        }

        return entity;
    }

    public Mono<Boolean> checkDuplicate(FileLoadEntity entity) {
        return fileService.find(entity.getUserGuid(), entity.getUploadDate())
                .map(ent -> true)
                .defaultIfEmpty(false);
    }
}
