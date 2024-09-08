package com.kafka.custom.encryption.demo.deserializer;

import com.kafka.custom.encryption.demo.service.EncryptionServiceInterface;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class EncryptedKafkaAvroDeserializer extends KafkaAvroDeserializer {

    private final EncryptionServiceInterface encryptionService;

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            // Decrypt the data
            byte[] decryptedData = encryptionService.decrypt(data);

            // Deserialize using Avro
            return super.deserialize(topic, decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }
}
