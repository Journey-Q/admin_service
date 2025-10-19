package com.example.admin_service.services;

import com.example.admin_service.dto.PointRedemptionDTO;
import com.example.admin_service.dto.RedeemPointsDTO;
import com.example.admin_service.entity.PointRedemption;
import com.example.admin_service.entity.TripFluencerPoints;
import com.example.admin_service.repository.PointRedemptionRepository;
import com.example.admin_service.repository.TripFluencerPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointRedemptionService {

    private final PointRedemptionRepository redemptionRepository;
    private final TripFluencerPointsRepository pointsRepository;
    private final RestTemplate restTemplate;

    // Monthly premium price - should be configured in application.properties
    private static final Double MONTHLY_PREMIUM_PRICE = 2500.0;

    // User service URL
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/users";

    /**
     * Redeem points for monthly premium subscription
     */
    @Transactional
    public PointRedemptionDTO redeemPoints(RedeemPointsDTO dto) {
        // Validate user exists and has enough points
        TripFluencerPoints userPoints = pointsRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("TripFluencer not found for user: " + dto.getUserId()));

        // Validate points to redeem (max 100 points = 100% discount)
        Integer pointsToRedeem = Math.min(dto.getPointsToRedeem(), 100);

        if (userPoints.getCurrentPoints() < pointsToRedeem) {
            throw new RuntimeException("Insufficient points. User has: " + userPoints.getCurrentPoints() +
                    ", trying to redeem: " + pointsToRedeem);
        }

        // Create redemption record
        PointRedemption redemption = new PointRedemption(
                dto.getUserId(),
                pointsToRedeem,
                MONTHLY_PREMIUM_PRICE
        );

        redemption.setSubscriptionType(dto.getSubscriptionType());

        // Deduct points from user
        userPoints.deductPoints(pointsToRedeem);
        pointsRepository.save(userPoints);

        // Save redemption
        PointRedemption saved = redemptionRepository.save(redemption);

        log.info("User {} redeemed {} points for {}% discount on monthly premium",
                dto.getUserId(), pointsToRedeem, pointsToRedeem);

        return convertToDTO(saved, null);
    }

    /**
     * Get all redemptions
     */
    public List<PointRedemptionDTO> getAllRedemptions(String token) {
        return redemptionRepository.findAllByOrderByRedeemedAtDesc()
                .stream()
                .map(redemption -> convertToDTO(redemption, token))
                .collect(Collectors.toList());
    }

    /**
     * Get redemptions by user
     */
    public List<PointRedemptionDTO> getRedemptionsByUser(Long userId, String token) {
        return redemptionRepository.findByUserIdOrderByRedeemedAtDesc(userId)
                .stream()
                .map(redemption -> convertToDTO(redemption, token))
                .collect(Collectors.toList());
    }

    /**
     * Get redemptions by status
     */
    public List<PointRedemptionDTO> getRedemptionsByStatus(String status, String token) {
        return redemptionRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(redemption -> convertToDTO(redemption, token))
                .collect(Collectors.toList());
    }

    /**
     * Cancel redemption
     */
    @Transactional
    public PointRedemptionDTO cancelRedemption(Long redemptionId, String token) {
        PointRedemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RuntimeException("Redemption not found: " + redemptionId));

        if (!"ACTIVE".equals(redemption.getStatus())) {
            throw new RuntimeException("Can only cancel active redemptions");
        }

        // Refund points to user
        TripFluencerPoints userPoints = pointsRepository.findByUserId(redemption.getUserId())
                .orElseThrow(() -> new RuntimeException("TripFluencer not found"));

        userPoints.addPoints(redemption.getPointsUsed());
        userPoints.setPointsUsed(userPoints.getPointsUsed() - redemption.getPointsUsed());
        pointsRepository.save(userPoints);

        // Update redemption status
        redemption.setStatus("CANCELLED");
        PointRedemption updated = redemptionRepository.save(redemption);

        log.info("Redemption {} cancelled, refunded {} points to user {}",
                redemptionId, redemption.getPointsUsed(), redemption.getUserId());

        return convertToDTO(updated, token);
    }

    /**
     * Expire old redemptions (should be called by a scheduled job)
     */
    @Transactional
    public void expireOldRedemptions() {
        List<PointRedemption> activeRedemptions = redemptionRepository.findByStatus("ACTIVE");

        int expiredCount = 0;
        for (PointRedemption redemption : activeRedemptions) {
            if (redemption.getExpiresAt() != null &&
                    redemption.getExpiresAt().isBefore(LocalDateTime.now())) {
                redemption.setStatus("EXPIRED");
                redemptionRepository.save(redemption);
                expiredCount++;
            }
        }

        if (expiredCount > 0) {
            log.info("Expired {} redemptions", expiredCount);
        }
    }

    /**
     * Get redemption statistics
     */
    public RedemptionStatsDTO getRedemptionStatistics() {
        Long activeCount = redemptionRepository.countActiveRedemptions();
        Long totalPointsUsed = redemptionRepository.sumPointsUsed();

        RedemptionStatsDTO stats = new RedemptionStatsDTO();
        stats.setActiveRedemptions(activeCount != null ? activeCount : 0L);
        stats.setTotalPointsRedeemed(totalPointsUsed != null ? totalPointsUsed : 0L);

        return stats;
    }

    /**
     * Convert entity to DTO
     */
    private PointRedemptionDTO convertToDTO(PointRedemption entity, String token) {
        PointRedemptionDTO dto = new PointRedemptionDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setPointsUsed(entity.getPointsUsed());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setSubscriptionType(entity.getSubscriptionType());
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setDiscountedPrice(entity.getDiscountedPrice());
        dto.setSubscriptionId(entity.getSubscriptionId());
        dto.setStatus(entity.getStatus());
        dto.setRedeemedAt(entity.getRedeemedAt());
        dto.setExpiresAt(entity.getExpiresAt());

        // Fetch user details if token is provided
        if (token != null) {
            try {
                fetchUserDetails(dto, entity.getUserId(), token);
            } catch (Exception e) {
                log.warn("Failed to fetch user details for redemption {}: {}", entity.getId(), e.getMessage());
                dto.setUserName("User " + entity.getUserId());
                dto.setUserEmail("user" + entity.getUserId() + "@example.com");
            }
        }

        return dto;
    }

    /**
     * Fetch user details from user service
     */
    private void fetchUserDetails(PointRedemptionDTO dto, Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDetailsResponse> response = restTemplate.exchange(
                    USER_SERVICE_URL + "/" + userId,
                    HttpMethod.GET,
                    entity,
                    UserDetailsResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                UserDetailsResponse user = response.getBody();
                dto.setUserName(user.getName());
                dto.setUserEmail(user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Could not fetch user details: {}", e.getMessage());
        }
    }

    /**
     * Inner class for user details response
     */
    private static class UserDetailsResponse {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * Inner class for redemption statistics
     */
    public static class RedemptionStatsDTO {
        private Long activeRedemptions;
        private Long totalPointsRedeemed;

        public Long getActiveRedemptions() {
            return activeRedemptions;
        }

        public void setActiveRedemptions(Long activeRedemptions) {
            this.activeRedemptions = activeRedemptions;
        }

        public Long getTotalPointsRedeemed() {
            return totalPointsRedeemed;
        }

        public void setTotalPointsRedeemed(Long totalPointsRedeemed) {
            this.totalPointsRedeemed = totalPointsRedeemed;
        }
    }
}