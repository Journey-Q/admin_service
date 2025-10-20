package com.example.admin_service.controller;

import com.example.admin_service.dto.ApiError;
import com.example.admin_service.dto.CommissionSettingsDTO;
import com.example.admin_service.dto.UpdateCommissionSettingsDTO;
import com.example.admin_service.entity.Commission;
import com.example.admin_service.services.CommissionSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing commission settings
 */
@RestController
@RequestMapping("/admin/auth/commissions/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CommissionSettingsController {

    private final CommissionSettingsService commissionSettingsService;

    /**
     * Get all commission settings
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllSettings(Authentication authentication) {
        try {
            log.info("Admin {} is fetching commission settings", authentication.getName());

            Map<String, BigDecimal> settings = commissionSettingsService.getAllSettings();

            Map<String, Object> response = new HashMap<>();
            response.put("settings", settings);
            response.put("message", "Commission settings retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commission settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get all commission settings as detailed DTOs
     */
    @GetMapping("/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllSettingsDetailed(Authentication authentication) {
        try {
            log.info("Admin {} is fetching detailed commission settings", authentication.getName());

            List<CommissionSettingsDTO> settings = commissionSettingsService.getAllSettingsDTO();

            Map<String, Object> response = new HashMap<>();
            response.put("settings", settings);
            response.put("total", settings.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching detailed commission settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commission settings by provider type
     */
    @GetMapping("/{providerType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSettingsByProviderType(@PathVariable Commission.ProviderType providerType,
                                                       Authentication authentication) {
        try {
            log.info("Admin {} is fetching commission settings for provider type: {}",
                    authentication.getName(), providerType);

            CommissionSettingsDTO settings = commissionSettingsService.getSettingsByProviderType(providerType);

            if (settings != null) {
                return ResponseEntity.ok(settings);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError("Commission settings not found for provider type: " + providerType,
                                HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            log.error("Error fetching commission settings for provider type {}: {}",
                    providerType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Update commission settings for a provider type
     */
    @PutMapping("/{providerType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSettings(
            @PathVariable Commission.ProviderType providerType,
            @Valid @RequestBody UpdateCommissionSettingsDTO dto,
            Authentication authentication) {
        try {
            log.info("Admin {} is updating commission settings for provider type: {}",
                    authentication.getName(), providerType);

            // Ensure provider type in path matches DTO
            dto.setProviderType(providerType);

            CommissionSettingsDTO updated = commissionSettingsService.updateSettings(providerType, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commission settings updated successfully");
            response.put("settings", updated);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating commission settings for provider type {}: {}",
                    providerType, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Error updating commission settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to update commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Bulk update commission settings
     */
    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkUpdateSettings(
            @Valid @RequestBody List<UpdateCommissionSettingsDTO> dtos,
            Authentication authentication) {
        try {
            log.info("Admin {} is bulk updating commission settings for {} provider types",
                    authentication.getName(), dtos.size());

            Map<String, BigDecimal> updated = commissionSettingsService.bulkUpdateSettings(dtos);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commission settings updated successfully");
            response.put("settings", updated);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error bulk updating commission settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to bulk update commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Initialize default commission settings
     */
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> initializeDefaultSettings(Authentication authentication) {
        try {
            log.info("Admin {} is initializing default commission settings", authentication.getName());

            commissionSettingsService.initializeDefaultSettings();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Default commission settings initialized successfully");
            response.put("settings", commissionSettingsService.getAllSettings());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initializing default commission settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to initialize default commission settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
