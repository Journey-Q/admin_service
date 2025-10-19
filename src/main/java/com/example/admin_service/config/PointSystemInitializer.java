package com.example.admin_service.config;

import com.example.admin_service.services.PointSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes the point system with default settings on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PointSystemInitializer implements CommandLineRunner {

    private final PointSettingsService pointSettingsService;

    @Override
    public void run(String... args) {
        try {
            log.info("Initializing TripFluencer Points System...");
            pointSettingsService.initializeDefaultSettings();
            log.info("TripFluencer Points System initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize TripFluencer Points System: {}", e.getMessage(), e);
        }
    }
}