package kz.lab.s3moderator.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;

@Data
public class FileLoadEntity {
    @Getter
    private Long id;
    @Getter
    private String s3Url;
    @Getter
    private String ownerGuid;
    @Getter
    private LocalDateTime ttl_date;
}
