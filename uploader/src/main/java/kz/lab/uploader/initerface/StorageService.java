package kz.lab.uploader.initerface;

import java.nio.file.Path;
import org.springframework.http.codec.multipart.FilePart;

import kz.lab.uploader.exception.StorageException;
import reactor.core.publisher.Mono;

public interface StorageService {
    void init() throws StorageException;
    public Mono<Path> store(FilePart filePart, String userUuid) throws StorageException;
    public Mono<Void> deleteAsync(String filename, String userUuid);
    void delete(String filename, String userUuid);
    void deleteAll();
}
