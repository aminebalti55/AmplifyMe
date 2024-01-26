package com.example.stagefinal.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class PasswordEncryptionService  {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public String encrypt(String password) {
        return encoder.encode(password);
    }


    public String decrypt(String encryptedPassword, String password) {
        if (encoder.matches(password, encryptedPassword)) {
            return password;
        } else {
            return null;
        }
    }
}
