package kz.lab.dbapp.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kz.lab.dbapp.entity.FileLoadEntity;
import kz.lab.dbapp.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public Mono<FileLoadEntity> create(FileLoadEntity entity) {
        if (entity.getUploadDate() == null) {
            entity.setUploadDate(LocalDateTime.now());
        }
        return fileRepository.save(entity);
    }

    public Mono<Void> remove(Long id) {
        return fileRepository.deleteById(id);
    }

    public Flux<FileLoadEntity> findAll() {
        return fileRepository.findAll();
    }

    public Mono<Void> deleteByNameAndGuid(String name, UUID userGuid) {
        return fileRepository.deleteByUserGuidAndName(userGuid, name);
    }
}
