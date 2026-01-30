package kz.lab.dbapp;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import kz.lab.dbapp.config.KafkaConfig;
import kz.lab.dbapp.exception.KafkaException;
import kz.lab.dbapp.kafka.KafkaSenderImpl;

@SpringBootApplication
public class DbappApplication {
	public static void main(String[] args) {
		SpringApplication.run(DbappApplication.class, args);

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(KafkaConfig.class)) {
			try {
				context.getBean(KafkaSenderImpl.class).send("new-load", "test");
			} catch (BeansException e) {
				e.printStackTrace();
			} catch (KafkaException e) {
				e.printStackTrace();
			}
		}
	}
}
