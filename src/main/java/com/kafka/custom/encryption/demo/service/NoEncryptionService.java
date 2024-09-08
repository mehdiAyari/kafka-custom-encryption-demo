package com.kafka.custom.encryption.demo.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "kafka.encryption", name = "enabled", havingValue = "false")
public class NoEncryptionService implements EncryptionServiceInterface {

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        return data;
    }
}
