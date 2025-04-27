package com.cinema.booking_app.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloudinary {
    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap();
        config.put("cloud_name","dt8idd99e");
        config.put("api_key","833898432841594");
        config.put("api_secret","5kXbMC-C_xLW4HEnRYcqJn3Tm5A");
        return new Cloudinary(config);
    }
}
