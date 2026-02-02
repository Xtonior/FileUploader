package kz.lab.dbapp.model.minio;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinioEvent {
    @JsonProperty("EventName")
    private String eventName;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Records")
    private List<MinioRecord> records;
}
