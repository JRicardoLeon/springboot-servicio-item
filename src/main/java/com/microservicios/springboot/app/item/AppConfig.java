package com.microservicios.springboot.app.item;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate registarRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
        return factory -> factory.configureDefault(id ->{
            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.custom()
                            .slidingWindowSize(10) // Tamaño de ventana deslizante, la que nos permite entrar al Circiut-Breaker, por defecto es de 100 peticiones
                            .failureRateThreshold(50) // Tasa de fallas del lumbral. Un 50% de fallos se activa el Circuit-Breaker
                            .waitDurationInOpenState(Duration.ofSeconds(10L)) // Tiempo de espera cuando se activa el Circuit-Breaker, por defecto es 60sg
                            .permittedNumberOfCallsInHalfOpenState(5) //Número de llmadas permitidas en el estado semi-abierto, por defecto son 10 llamadas
                            .build())
                    .timeLimiterConfig(TimeLimiterConfig.ofDefaults()) // Tiempo limite
                    .build();
        });
    }

}
