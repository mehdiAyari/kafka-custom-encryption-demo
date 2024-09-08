package com.kafka.custom.encryption.demo.serializer;

import com.kafka.custom.encryption.demo.service.EncryptionServiceInterface;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EncryptedKafkaAvroSerializer extends KafkaAvroSerializer {

    private final EncryptionServiceInterface encryptionService;

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            // Serialize using Avro
            byte[] avroData = super.serialize(topic, data);

            // Encrypt the Avro serialized data
            return encryptionService.encrypt(avroData);
        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

}
