package com.kafka.custom.encryption.demo.producer;

import com.kafka.custom.encryption.demo.PersonEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonEventProducer {
    private final KafkaTemplate<String, PersonEvent> kafkaTemplate;
    private final String topic;

    public PersonEventProducer(KafkaTemplate<String, PersonEvent> kafkaTemplate,
                               @Value("${kafka.topic.name}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendPersonEvent(PersonEvent event) {
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), event);
    }
}
