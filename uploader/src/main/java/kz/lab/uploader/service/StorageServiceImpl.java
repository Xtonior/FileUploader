package kz.lab.uploader.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import kz.lab.uploader.config.StorageProperties;
import kz.lab.uploader.exception.StorageException;
import kz.lab.uploader.initerface.StorageService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

    private final Scheduler storageScheduler = Schedulers.newBoundedElastic(10, 100, "storage-pool");

    @Autowired
    public StorageServiceImpl(StorageProperties properties) throws StorageException {
        if (properties.getDefaultLocation().trim().length() == 0) {
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getDefaultLocation());

        init();
    }

    @Override
    public void init() throws StorageException {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public Mono<Path> store(FilePart filePart, String userUuid) throws StorageException {
        return Mono.just(filePart)
                .publishOn(storageScheduler)
                .flatMap(file -> {
                    String fileName = file.filename();

                    if (fileName == null || fileName.isEmpty()) {
                        return Mono.error(new StorageException("Empty file!"));
                    }

                    Path userDir = rootLocation.resolve(userUuid).normalize().toAbsolutePath();

                    try {
                        Files.createDirectories(userDir);
                    } catch (IOException e) {
                        return Mono.error(new StorageException("Failed to create user directory", e));
                    }

                    Path dest = userDir.resolve(fileName).normalize().toAbsolutePath();

                    if (!dest.startsWith(rootLocation.toAbsolutePath())) {
                        return Mono.error(new StorageException("Cannot store file outside of root directory!"));
                    }

                    return file.transferTo(dest)
                            .thenReturn(dest);
                })
                .onErrorMap(e -> {
                    if (e instanceof StorageException)
                        return e;
                    return new StorageException("Failed to store file", e);
                });
    }

    @Override
    public Mono<Void> deleteAsync(String filename, String userUuid) {
        return Mono.fromRunnable(() -> {
            try {
                Path fileToDelete = rootLocation.resolve(userUuid).resolve(filename);
                Files.deleteIfExists(fileToDelete);

                Files.deleteIfExists(rootLocation.resolve(userUuid));
            } catch (IOException e) {
                log.error("Failed to delete file", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public void delete(String filename, String userUuid) {
        FileSystemUtils.deleteRecursively(rootLocation.resolve(userUuid).toFile());
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

}
