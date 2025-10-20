package com.example.admin_service.services;

import com.example.admin_service.dto.CommissionSettingsDTO;
import com.example.admin_service.dto.UpdateCommissionSettingsDTO;
import com.example.admin_service.entity.Commission;
import com.example.admin_service.entity.CommissionSettings;
import com.example.admin_service.repository.CommissionSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionSettingsService {

    private final CommissionSettingsRepository commissionSettingsRepository;

    /**
     * Initialize default commission settings if not exists
     */
    @Transactional
    public void initializeDefaultSettings() {
        if (commissionSettingsRepository.count() == 0) {
            log.info("Initializing default commission settings");

            commissionSettingsRepository.save(new CommissionSettings(
                Commission.ProviderType.HOTEL,
                new BigDecimal("12.5")
            ));
            commissionSettingsRepository.save(new CommissionSettings(
                Commission.ProviderType.TRAVEL_SERVICE,
                new BigDecimal("10.0")
            ));
            commissionSettingsRepository.save(new CommissionSettings(
                Commission.ProviderType.TOUR_SERVICE,
                new BigDecimal("15.0")
            ));

            log.info("Default commission settings initialized successfully");
        }
    }

    /**
     * Get all commission settings
     */
    public Map<String, BigDecimal> getAllSettings() {
        List<CommissionSettings> settings = commissionSettingsRepository.findAll();
        Map<String, BigDecimal> settingsMap = new HashMap<>();

        for (CommissionSettings setting : settings) {
            String key = setting.getProviderType().name();
            settingsMap.put(key, setting.getCommissionRate());
        }

        return settingsMap;
    }

    /**
     * Get all commission settings as DTOs
     */
    public List<CommissionSettingsDTO> getAllSettingsDTO() {
        return commissionSettingsRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commission settings by provider type
     */
    public CommissionSettingsDTO getSettingsByProviderType(Commission.ProviderType providerType) {
        return commissionSettingsRepository.findByProviderType(providerType)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get commission rate by provider type
     */
    public BigDecimal getCommissionRate(Commission.ProviderType providerType) {
        return commissionSettingsRepository.findByProviderTypeAndIsActiveTrue(providerType)
                .map(CommissionSettings::getCommissionRate)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Update commission settings for a specific provider type
     */
    @Transactional
    public CommissionSettingsDTO updateSettings(Commission.ProviderType providerType,
                                                UpdateCommissionSettingsDTO dto) {
        CommissionSettings settings = commissionSettingsRepository.findByProviderType(providerType)
                .orElseThrow(() -> new RuntimeException(
                    "Commission settings not found for provider type: " + providerType));

        settings.setCommissionRate(dto.getCommissionRate());
        CommissionSettings updated = commissionSettingsRepository.save(settings);

        log.info("Updated commission settings for provider type: {}, new rate: {}%",
                providerType, dto.getCommissionRate());

        return convertToDTO(updated);
    }

    /**
     * Bulk update commission settings
     */
    @Transactional
    public Map<String, BigDecimal> bulkUpdateSettings(List<UpdateCommissionSettingsDTO> dtos) {
        for (UpdateCommissionSettingsDTO dto : dtos) {
            updateSettings(dto.getProviderType(), dto);
        }
        return getAllSettings();
    }

    /**
     * Convert entity to DTO
     */
    private CommissionSettingsDTO convertToDTO(CommissionSettings entity) {
        CommissionSettingsDTO dto = new CommissionSettingsDTO();
        dto.setId(entity.getId());
        dto.setProviderType(entity.getProviderType());
        dto.setCommissionRate(entity.getCommissionRate());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}
