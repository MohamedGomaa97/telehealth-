package com.telehealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TeleHealth Platform - Modular Monolith Entry Point
 *
 * Architecture: Modular Monolith with Clean Architecture per Module
 * Modules:
 *   - patient    : Patient registration, profiles, medical history
 *   - doctor     : Doctor profiles, availability, specializations
 *   - consultation: Home visits, remote consultations, scheduling
 *   - emergency  : Emergency services, dispatch, SOS handling
 *
 * Communication: RabbitMQ for async cross-module events
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class TeleHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeleHealthApplication.class, args);
    }
}
