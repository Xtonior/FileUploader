package kz.lab.dbapp.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("file_records")
public class FileLoadEntity {
    @Id
    private Long id;
    private String name;
    private String link;
    private UUID userGuid;
    private LocalDateTime uploadDate;
}
