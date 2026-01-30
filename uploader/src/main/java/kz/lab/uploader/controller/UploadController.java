package kz.lab.uploader.controller;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import kz.lab.uploader.exception.StorageException;
import kz.lab.uploader.initerface.S3Service;
import kz.lab.uploader.initerface.StorageService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@RestController
@RequiredArgsConstructor
public class UploadController {
    @Autowired
    StorageService storageService;

    @Autowired
    private final S3AsyncClient s3AsyncClient;

    @Autowired
    private final S3Service s3Service;

    @Value("${minio.bucket}")
    private String bucket;

    @GetMapping(value = "/files")
    public Flux<Stream<Path>> getFiles() throws StorageException {
        return Flux.just(storageService.loadAll());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@RequestPart("file") FilePart file) throws StorageException {
        return storageService.store(file)
                .doOnSuccess(path -> {
                    s3Service.store(path, s3AsyncClient, bucket)
                            .subscribe(
                                    unused -> System.out.println("Uploaded to S3: " + path.getFileName()),
                                    err -> System.err.println("S3 upload failed: " + err.getMessage()));
                })
                .thenReturn("File stored locally, S3 upload started in background");
    }

}
