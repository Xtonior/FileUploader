package kz.lab.uploader.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import kz.lab.uploader.exception.StorageException;
import kz.lab.uploader.initerface.S3Service;
import kz.lab.uploader.initerface.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    @Autowired
    StorageService storageService;

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Autowired
    private S3Service s3Service;

    @Value("${minio.bucket}")
    private String bucket;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@RequestPart("file") FilePart file,
            @RequestParam("userUuid") UUID userUuid) throws StorageException {
        String userId = userUuid.toString();
        String fileName = file.filename();

        return storageService.store(file, userId)
                .flatMap(path -> s3Service.store(path, s3AsyncClient, userUuid, bucket)
                        .then(storageService.deleteAsync(fileName, userId)))
                .thenReturn("File uploaded");
    }
}
