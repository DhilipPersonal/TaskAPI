package com.example.taskapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurer.setPatternParser(new PathPatternParser());
    }
}
