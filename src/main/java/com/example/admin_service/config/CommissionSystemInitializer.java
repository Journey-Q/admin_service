package com.example.admin_service.config;

import com.example.admin_service.services.CommissionSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initializes commission system with default settings on application startup
 */
@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class CommissionSystemInitializer implements CommandLineRunner {

    private final CommissionSettingsService commissionSettingsService;

    @Override
    public void run(String... args) {
        try {
            log.info("Initializing Commission System...");
            commissionSettingsService.initializeDefaultSettings();
            log.info("Commission System initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Commission System: {}", e.getMessage(), e);
        }
    }
}
