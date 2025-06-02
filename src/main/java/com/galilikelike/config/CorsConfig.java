package com.galilikelike.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .maxAge(1)
                .allowCredentials(true)
                .allowedMethods("GET","POST","DELETE","PUT","OPTIONS");
        System.out.println("跨域成功");
    }
}
