package com.kafka.custom.encryption.demo.controller;

import com.kafka.custom.encryption.demo.PersonEvent;
import com.kafka.custom.encryption.demo.producer.PersonEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ProduceMessageController {

    private final PersonEventProducer producer;

    @PostMapping("/produce")
    public ResponseEntity<Void> produce(@RequestBody PersonEvent event) {
        event.setDate(Instant.now());
        producer.sendPersonEvent(event);
        return ResponseEntity.noContent().build();
    }

}
