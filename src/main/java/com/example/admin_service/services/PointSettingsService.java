package com.example.admin_service.services;

import com.example.admin_service.dto.PointSettingsDTO;
import com.example.admin_service.dto.UpdatePointSettingsDTO;
import com.example.admin_service.entity.PointSettings;
import com.example.admin_service.repository.PointSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointSettingsService {

    private final PointSettingsRepository pointSettingsRepository;

    /**
     * Initialize default point settings if not exists
     */
    @Transactional
    public void initializeDefaultSettings() {
        if (pointSettingsRepository.count() == 0) {
            log.info("Initializing default point settings");

            pointSettingsRepository.save(new PointSettings("tier1", 0, 1000, 10));
            pointSettingsRepository.save(new PointSettings("tier2", 1001, 10000, 20));
            pointSettingsRepository.save(new PointSettings("tier3", 10001, 100000, 30));
            pointSettingsRepository.save(new PointSettings("tier4", 100001, 500000, 40));
            pointSettingsRepository.save(new PointSettings("tier5", 500001, 1000000, 50));

            log.info("Default point settings initialized successfully");
        }
    }

    /**
     * Get all point settings
     */
    public List<PointSettingsDTO> getAllSettings() {
        return pointSettingsRepository.findAllByOrderByMinLikesAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get point settings by tier name
     */
    public PointSettingsDTO getSettingsByTierName(String tierName) {
        return pointSettingsRepository.findByTierName(tierName)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Update point settings for a specific tier
     */
    @Transactional
    public PointSettingsDTO updateSettings(String tierName, UpdatePointSettingsDTO dto) {
        PointSettings settings = pointSettingsRepository.findByTierName(tierName)
                .orElseThrow(() -> new RuntimeException("Point settings not found for tier: " + tierName));

        settings.setPointsPerMilestone(dto.getPointsPerMilestone());
        PointSettings updated = pointSettingsRepository.save(settings);

        log.info("Updated point settings for tier: {}, new points: {}",
                tierName, dto.getPointsPerMilestone());

        return convertToDTO(updated);
    }

    /**
     * Bulk update point settings
     */
    @Transactional
    public List<PointSettingsDTO> bulkUpdateSettings(List<UpdatePointSettingsDTO> dtos) {
        for (UpdatePointSettingsDTO dto : dtos) {
            updateSettings(dto.getTierName(), dto);
        }
        return getAllSettings();
    }

    /**
     * Calculate points based on likes
     */
    public Integer calculatePointsFromLikes(Integer likes) {
        List<PointSettings> settings = pointSettingsRepository.findAllByOrderByMinLikesAsc();

        for (PointSettings setting : settings) {
            if (likes >= setting.getMinLikes() && likes <= setting.getMaxLikes()) {
                // For tier1, calculate based on hundreds
                if (setting.getTierName().equals("tier1")) {
                    return (likes / 100) * setting.getPointsPerMilestone();
                }
                // For other tiers, return fixed points
                return setting.getPointsPerMilestone();
            }
        }

        // If likes exceed all tiers, return the highest tier points
        return settings.isEmpty() ? 0 : settings.get(settings.size() - 1).getPointsPerMilestone();
    }

    /**
     * Convert entity to DTO
     */
    private PointSettingsDTO convertToDTO(PointSettings entity) {
        PointSettingsDTO dto = new PointSettingsDTO();
        dto.setId(entity.getId());
        dto.setTierName(entity.getTierName());
        dto.setMinLikes(entity.getMinLikes());
        dto.setMaxLikes(entity.getMaxLikes());
        dto.setPointsPerMilestone(entity.getPointsPerMilestone());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}