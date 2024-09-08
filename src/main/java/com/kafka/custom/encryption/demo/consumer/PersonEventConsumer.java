package com.kafka.custom.encryption.demo.consumer;

import com.kafka.custom.encryption.demo.PersonEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonEventConsumer {

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, PersonEvent> record) {
        log.info("Consumed event: key = {} , event = {}", record.key(), record.value());
    }
}