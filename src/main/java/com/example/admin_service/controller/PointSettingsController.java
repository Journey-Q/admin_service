package com.example.admin_service.controller;

import com.example.admin_service.dto.ApiError;
import com.example.admin_service.dto.PointSettingsDTO;
import com.example.admin_service.dto.UpdatePointSettingsDTO;
import com.example.admin_service.services.PointSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing point settings
 */
@RestController
@RequestMapping("/admin/auth/points/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PointSettingsController {

    private final PointSettingsService pointSettingsService;

    /**
     * Get all point settings (Public - accessible by all users)
     */
    @GetMapping
    public ResponseEntity<?> getAllSettings() {
        try {
            log.info("Fetching all point settings");

            List<PointSettingsDTO> settings = pointSettingsService.getAllSettings();

            Map<String, Object> response = new HashMap<>();
            response.put("settings", settings);
            response.put("total", settings.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching point settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch point settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get point settings by tier name (Public - accessible by all users)
     */
    @GetMapping("/{tierName}")
    public ResponseEntity<?> getSettingsByTier(@PathVariable String tierName) {
        try {
            log.info("Fetching point settings for tier: {}", tierName);

            PointSettingsDTO settings = pointSettingsService.getSettingsByTierName(tierName);

            if (settings != null) {
                return ResponseEntity.ok(settings);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError("Point settings not found for tier: " + tierName,
                                HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            log.error("Error fetching point settings for tier {}: {}", tierName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch point settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Update point settings for a tier
     */
    @PutMapping("/{tierName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSettings(
            @PathVariable String tierName,
            @RequestBody UpdatePointSettingsDTO dto,
            Authentication authentication) {
        try {
            log.info("Admin {} is updating point settings for tier: {}", authentication.getName(), tierName);

            // Ensure tierName in path matches DTO
            dto.setTierName(tierName);

            PointSettingsDTO updated = pointSettingsService.updateSettings(tierName, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Point settings updated successfully");
            response.put("settings", updated);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating point settings for tier {}: {}", tierName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to update point settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Bulk update point settings
     */
    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkUpdateSettings(
            @RequestBody List<UpdatePointSettingsDTO> dtos,
            Authentication authentication) {
        try {
            log.info("Admin {} is bulk updating point settings for {} tiers",
                    authentication.getName(), dtos.size());

            List<PointSettingsDTO> updated = pointSettingsService.bulkUpdateSettings(dtos);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Point settings updated successfully");
            response.put("settings", updated);
            response.put("total", updated.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error bulk updating point settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to bulk update point settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Initialize default settings (only if no settings exist)
     */
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> initializeDefaultSettings(Authentication authentication) {
        try {
            log.info("Admin {} is initializing default point settings", authentication.getName());

            pointSettingsService.initializeDefaultSettings();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Default point settings initialized successfully");
            response.put("settings", pointSettingsService.getAllSettings());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initializing default point settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to initialize default point settings: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}