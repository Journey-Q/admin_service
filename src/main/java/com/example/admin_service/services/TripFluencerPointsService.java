package com.example.admin_service.services;

import com.example.admin_service.dto.AddPointsDTO;
import com.example.admin_service.dto.DeductPointsDTO;
import com.example.admin_service.dto.PointsStatisticsDTO;
import com.example.admin_service.dto.TripFluencerPointsDTO;
import com.example.admin_service.entity.TripFluencerPoints;
import com.example.admin_service.repository.TripFluencerPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripFluencerPointsService {

    private final TripFluencerPointsRepository pointsRepository;
    private final PointSettingsService pointSettingsService;
    private final RestTemplate restTemplate;

    // User service URL - should be configured in application.properties
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/users";

    /**
     * Get all TripFluencer points
     */
    public List<TripFluencerPointsDTO> getAllTripFluencers(String token) {
        List<TripFluencerPoints> allPoints = pointsRepository.findAllByIsActiveTrue();

        return allPoints.stream()
                .map(points -> convertToDTO(points, token))
                .collect(Collectors.toList());
    }

    /**
     * Get points for a specific user
     */
    public TripFluencerPointsDTO getUserPoints(Long userId, String token) {
        TripFluencerPoints points = pointsRepository.findByUserId(userId)
                .orElse(null);

        if (points == null) {
            return null;
        }

        return convertToDTO(points, token);
    }

    /**
     * Create or update TripFluencer points record
     */
    @Transactional
    public TripFluencerPointsDTO createOrUpdateTripFluencer(Long userId, Integer followers, Integer totalLikes) {
        TripFluencerPoints points = pointsRepository.findByUserId(userId)
                .orElse(new TripFluencerPoints(userId, followers, totalLikes));

        points.setFollowers(followers);
        points.setTotalLikes(totalLikes);
        points.updateTier();

        TripFluencerPoints saved = pointsRepository.save(points);
        log.info("Created/Updated TripFluencer points for user: {}", userId);

        return convertToDTO(saved, null);
    }

    /**
     * Add points to a user (admin action)
     */
    @Transactional
    public TripFluencerPointsDTO addPoints(AddPointsDTO dto, String token) {
        TripFluencerPoints points = pointsRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("TripFluencer not found for user: " + dto.getUserId()));

        points.addPoints(dto.getPoints());
        TripFluencerPoints updated = pointsRepository.save(points);

        log.info("Admin added {} points to user {}. Reason: {}",
                dto.getPoints(), dto.getUserId(), dto.getReason());

        return convertToDTO(updated, token);
    }

    /**
     * Deduct points from a user (admin action)
     */
    @Transactional
    public TripFluencerPointsDTO deductPoints(DeductPointsDTO dto, String token) {
        TripFluencerPoints points = pointsRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("TripFluencer not found for user: " + dto.getUserId()));

        boolean success = points.deductPoints(dto.getPoints());
        if (!success) {
            throw new RuntimeException("Insufficient points. User has: " + points.getCurrentPoints() +
                    ", trying to deduct: " + dto.getPoints());
        }

        TripFluencerPoints updated = pointsRepository.save(points);

        log.info("Admin deducted {} points from user {}. Reason: {}",
                dto.getPoints(), dto.getUserId(), dto.getReason());

        return convertToDTO(updated, token);
    }

    /**
     * Award points based on likes received (called when user gets likes)
     */
    @Transactional
    public TripFluencerPointsDTO awardPointsForLikes(Long userId, Integer newLikes, String token) {
        TripFluencerPoints points = pointsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("TripFluencer not found for user: " + userId));

        // Calculate points based on the new likes
        Integer pointsToAdd = pointSettingsService.calculatePointsFromLikes(newLikes);

        if (pointsToAdd > 0) {
            points.addPoints(pointsToAdd);
            points.setTotalLikes(points.getTotalLikes() + newLikes);
            TripFluencerPoints updated = pointsRepository.save(points);

            log.info("Awarded {} points to user {} for {} likes", pointsToAdd, userId, newLikes);
            return convertToDTO(updated, token);
        }

        return convertToDTO(points, token);
    }

    /**
     * Get top earners
     */
    public List<TripFluencerPointsDTO> getTopEarners(String token) {
        return pointsRepository.findTopEarners()
                .stream()
                .map(points -> convertToDTO(points, token))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Get statistics
     */
    public PointsStatisticsDTO getStatistics() {
        PointsStatisticsDTO stats = new PointsStatisticsDTO();

        Long activeCount = pointsRepository.countActiveTripFluencers();
        Long totalEarned = pointsRepository.sumTotalPointsEarned();

        stats.setActiveTripFluencers(activeCount != null ? activeCount : 0L);
        stats.setTotalPointsEarned(totalEarned != null ? totalEarned : 0L);

        if (activeCount != null && activeCount > 0 && totalEarned != null) {
            stats.setAveragePointsPerUser((int) (totalEarned / activeCount));
        } else {
            stats.setAveragePointsPerUser(0);
        }

        return stats;
    }

    /**
     * Toggle TripFluencer active status
     */
    @Transactional
    public TripFluencerPointsDTO toggleActive(Long userId, String token) {
        TripFluencerPoints points = pointsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("TripFluencer not found for user: " + userId));

        points.setIsActive(!points.getIsActive());
        TripFluencerPoints updated = pointsRepository.save(points);

        log.info("Toggled active status for user {}: {}", userId, updated.getIsActive());
        return convertToDTO(updated, token);
    }

    /**
     * Convert entity to DTO and fetch user details
     */
    private TripFluencerPointsDTO convertToDTO(TripFluencerPoints entity, String token) {
        TripFluencerPointsDTO dto = new TripFluencerPointsDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setCurrentPoints(entity.getCurrentPoints());
        dto.setTotalPointsEarned(entity.getTotalPointsEarned());
        dto.setPointsUsed(entity.getPointsUsed());
        dto.setTotalLikes(entity.getTotalLikes());
        dto.setFollowers(entity.getFollowers());
        dto.setTier(entity.getTier());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Fetch user details from user service if token is provided
        if (token != null) {
            try {
                fetchUserDetails(dto, entity.getUserId(), token);
            } catch (Exception e) {
                log.warn("Failed to fetch user details for user {}: {}", entity.getUserId(), e.getMessage());
                // Set default values
                dto.setUserName("User " + entity.getUserId());
                dto.setUserEmail("user" + entity.getUserId() + "@example.com");
            }
        }

        return dto;
    }

    /**
     * Fetch user details from user service
     */
    private void fetchUserDetails(TripFluencerPointsDTO dto, Long userId, String token) {
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
                dto.setProfileImage(user.getProfileImage());
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
        private String profileImage;

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

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }
    }
}