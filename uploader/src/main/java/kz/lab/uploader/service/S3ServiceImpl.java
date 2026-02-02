package kz.lab.uploader.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kz.lab.uploader.initerface.S3Service;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    @Value("${minio.bucket}")
    private String s3path;

    @Override
    public Mono<PutObjectResponse> store(Path path, S3AsyncClient s3AsyncClient, UUID userUuid, String bucket) {
        StringBuilder fullPath = new StringBuilder();
        fullPath.append(s3path);
        fullPath.append('/');
        fullPath.append(userUuid);
        fullPath.append('/');
        fullPath.append(path.getFileName());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fullPath.toString())
                .metadata(Map.of(
                        "user-guid", "550e8400-e29b-41d4-a716-446655440000",
                        "date", LocalDateTime.now().toString()))
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.putObject(request, AsyncRequestBody.fromFile(path)))
                .doOnSuccess(response -> log.info("File uploaded to S3: {}", fullPath.toString()))
                .doOnError(e -> log.error("Failed to upload file to S3", e));
    }
}
