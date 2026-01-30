package kz.lab.uploader.service;

import java.nio.file.Path;
import org.springframework.stereotype.Service;

import kz.lab.uploader.initerface.S3Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3ServiceImpl implements S3Service {

    @Override
    public Mono<PutObjectResponse> store(Path path, S3AsyncClient s3AsyncClient, String bucket) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path.getFileName().toString())
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.putObject(request, AsyncRequestBody.fromFile(path)));
    }
}
