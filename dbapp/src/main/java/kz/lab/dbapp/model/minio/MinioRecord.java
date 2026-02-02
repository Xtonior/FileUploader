package kz.lab.dbapp.model.minio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class MinioRecord {
    private String eventName; // например s3:ObjectCreated:Put
    private String eventTime;
    private S3Dto s3;
}