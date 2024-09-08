package com.kafka.custom.encryption.demo.service;

public interface EncryptionServiceInterface {
    byte[] encrypt(byte[] data) throws Exception;

    byte[] decrypt(byte[] data) throws Exception;
}
