package kz.lab.uploader.config;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class StorageProperties {
    private String defaultLocation = "uploads";
}
