package com.kafka.custom.encryption.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@ConditionalOnProperty(prefix = "kafka.encryption", name = "enabled", havingValue = "true")
public class EncryptionService implements EncryptionServiceInterface {

    private final SecretKey secretKey;

    public EncryptionService(@Value("${kafka.encryption.base64Key:b7ZiyEwFwp7JkqIBUuJjPA==}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key); // Decode the Base64-encoded key
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}
