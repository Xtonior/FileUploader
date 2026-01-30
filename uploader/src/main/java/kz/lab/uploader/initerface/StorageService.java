package kz.lab.uploader.initerface;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import kz.lab.uploader.exception.StorageException;
import kz.lab.uploader.exception.StorageFileNotFoundException;
import reactor.core.publisher.Mono;

public interface StorageService {
    void init() throws StorageException;
    public Mono<Path> store(FilePart filePart) throws StorageException;
    Stream<Path> loadAll() throws StorageException;
    Path load(String filename);
    Resource loadAsResource(String filename) throws StorageFileNotFoundException;
    void delete(String filename);
    void deleteAll();
}
