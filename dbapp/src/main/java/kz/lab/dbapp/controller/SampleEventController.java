package kz.lab.dbapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kz.lab.dbapp.exception.KafkaException;
import kz.lab.dbapp.model.DbUpdateEvent;
import kz.lab.dbapp.model.SampleEvent;
import kz.lab.dbapp.service.KafkaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SampleEventController {
    @Autowired
    private KafkaService kafkaService;

    @PostMapping(path = "/kafka-test")
    public void sendSampleEventToKafka(@RequestBody SampleEvent event) throws KafkaException {
        kafkaService.sendSampleEvent(new SampleEvent(event.getId(), event.getMessage()));
    }

    @PostMapping(path = "/dbUpdate")
    public void sendDbUpdateEvent(@RequestBody DbUpdateEvent event) throws KafkaException {
        kafkaService.sendDbUpdateEvent(new DbUpdateEvent(event.getId(), event.getData()));
    }
}
