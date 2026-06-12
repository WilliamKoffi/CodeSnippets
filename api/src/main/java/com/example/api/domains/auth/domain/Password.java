package com.example.api.domains.auth.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class Password {

    private Password() {
    }

    public static String secure(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("Failed to hash password", error);
        }
    }

    public static boolean matches(String raw, String hash) {
        return secure(raw).equals(hash);
    }
}
