package kz.lab.uploader.initerface;

import java.nio.file.Path;
import java.util.UUID;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface S3Service {
    public Mono<PutObjectResponse> store(Path path, S3AsyncClient s3AsyncClient, UUID userUuid, String bucket);
}
