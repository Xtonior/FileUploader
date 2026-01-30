package kz.lab.uploader.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import kz.lab.uploader.config.StorageProperties;
import kz.lab.uploader.exception.StorageException;
import kz.lab.uploader.exception.StorageFileNotFoundException;
import kz.lab.uploader.initerface.StorageService;
import reactor.core.publisher.Mono;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

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

    // add parted flag
    @Override
    public Mono<Path> store(FilePart filePart) throws StorageException {
        return Mono.just(filePart)
                .flatMap(file -> {
                    String fileName = file.filename();

                    if (fileName.isEmpty()) {
                        return Mono.error(new StorageException("Empty file!"));
                    }

                    Path dest = rootLocation.resolve(Paths.get(fileName))
                            .normalize().toAbsolutePath();

                    if (!dest.getParent().equals(rootLocation.toAbsolutePath())) {
                        return Mono.error(new StorageException("Cannot store file outside of current directory!"));
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
    public Stream<Path> loadAll() throws StorageException {
        try {
            return Files.walk(rootLocation, 1);
        } catch (Exception e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) throws StorageFileNotFoundException {
        try {
            Path file = load(filename);
            Resource res = new UrlResource(file.toUri());

            if (res.exists() || res.isReadable())
                return res;
            else
                throw new StorageFileNotFoundException("Could not read file: " + filename);
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void delete(String filename) {
        FileSystemUtils.deleteRecursively(rootLocation.resolve(filename).toFile());
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

}
