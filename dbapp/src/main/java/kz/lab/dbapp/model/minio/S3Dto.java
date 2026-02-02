package kz.lab.dbapp.model.minio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class S3Dto {
    private BucketDto bucket;
    private ObjectDataDto object;
}