package com.microservicios.springboot.app.item;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate registarRestTemplate(){
        return new RestTemplate();
    }
}
