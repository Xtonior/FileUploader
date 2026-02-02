package kz.lab.dbapp.repository;


import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import kz.lab.dbapp.entity.FileLoadEntity;
import reactor.core.publisher.Mono;

@Repository
public interface FileRepository extends ReactiveCrudRepository<FileLoadEntity, Long> {
    Mono<Void> deleteByUserGuidAndName(UUID userGuid, String name);
}