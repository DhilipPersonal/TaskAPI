package com.example.taskapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public List<HeaderWriter> securityHeaderWriters() {
        return Arrays.asList(
            // X-Content-Type-Options: nosniff
            new StaticHeadersWriter("X-Content-Type-Options", "nosniff"),
            
            // X-Frame-Options: DENY
            new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY),
            
            // X-XSS-Protection: 1; mode=block
            new XXssProtectionHeaderWriter(),
            
            // Referrer-Policy: strict-origin-when-cross-origin
            new ReferrerPolicyHeaderWriter(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN),
            
            // Content-Security-Policy
            new StaticHeadersWriter("Content-Security-Policy", 
                "default-src 'self'; script-src 'self'; object-src 'none'; img-src 'self' data:; " +
                "style-src 'self' 'unsafe-inline'; font-src 'self'; frame-ancestors 'none'; " +
                "connect-src 'self'; base-uri 'self'"),
            
            // Strict-Transport-Security
            new StaticHeadersWriter("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains")
        );
    }
}
