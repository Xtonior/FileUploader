package kz.lab.dbapp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileLoadEntity {
    @Id
    private Long id;

    @Column
    private String s3Url;

    @Column
    private String ownerGuid;

    @Column
    private LocalDateTime ttl_date;
}
