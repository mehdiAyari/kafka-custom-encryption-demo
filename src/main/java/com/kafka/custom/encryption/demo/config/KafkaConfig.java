package com.kafka.custom.encryption.demo.config;

import com.kafka.custom.encryption.demo.PersonEvent;
import com.kafka.custom.encryption.demo.deserializer.EncryptedKafkaAvroDeserializer;
import com.kafka.custom.encryption.demo.serializer.EncryptedKafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory producerFactory(EncryptedKafkaAvroSerializer encryptedKafkaAvroSerializer) {
        final Map<String, Object> configProps = new HashMap<>(this.kafkaProperties.buildProducerProperties(null));
        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), encryptedKafkaAvroSerializer);
    }

    @Bean
    public ConsumerFactory consumerFactory(EncryptedKafkaAvroDeserializer encryptedKafkaAvroDeserializer) {
        final Map<String, Object> configProps = new HashMap<>(this.kafkaProperties.buildConsumerProperties(null));
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), encryptedKafkaAvroDeserializer);
    }

    @Bean
    public KafkaTemplate<String, PersonEvent> kafkaTemplate(ProducerFactory<String, PersonEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PersonEvent> kafkaListenerContainerFactory(ConsumerFactory<String, PersonEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PersonEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3); // Adjust concurrency as needed
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
}
