package com.example.taskapi.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TwoFactorAuthUtil {
    public static String generateSecret() {
        byte[] buffer = new byte[20];
        new SecureRandom().nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }
    // Add methods for TOTP generation/validation as needed
}
