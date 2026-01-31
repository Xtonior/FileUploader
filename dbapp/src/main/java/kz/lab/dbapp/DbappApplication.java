package kz.lab.dbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.r2dbc.autoconfigure.DataR2dbcAutoConfiguration;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcAutoConfiguration;

@SpringBootApplication(
    exclude = {
        R2dbcAutoConfiguration.class,
        DataR2dbcAutoConfiguration.class
    }
)
public class DbappApplication {
	public static void main(String[] args) {
		SpringApplication.run(DbappApplication.class, args);
	}
}
