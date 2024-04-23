package com.igse.config;

import com.igse.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class EncoderDecoder {
    public String encrypt(String value) {
        byte[] hash = null;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new UserException(HttpStatus.BAD_REQUEST.value(), "Invalid Password");
        }
        return bytesToHex2(hash);
    }
    private String bytesToHex2(byte[] hash) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
