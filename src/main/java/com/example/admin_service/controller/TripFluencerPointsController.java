package com.example.admin_service.controller;

import com.example.admin_service.dto.*;
import com.example.admin_service.entity.AdminPrincipal;
import com.example.admin_service.services.TripFluencerPointsService;
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
 * Controller for managing TripFluencer points
 */
@RestController
@RequestMapping("/admin/auth/tripfluencer-points")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class TripFluencerPointsController {

    private final TripFluencerPointsService pointsService;

    /**
     * Get all TripFluencers and their points
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTripFluencers(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is fetching all TripFluencers", admin.getEmail());

            String token = authHeader.replace("Bearer ", "");
            List<TripFluencerPointsDTO> tripFluencers = pointsService.getAllTripFluencers(token);

            Map<String, Object> response = new HashMap<>();
            response.put("tripFluencers", tripFluencers);
            response.put("total", tripFluencers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching TripFluencers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch TripFluencers: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get points for a specific user (Public - accessible by all users)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPoints(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("Fetching points for user: {}", userId);

            String token = authHeader != null ? authHeader.replace("Bearer ", "") : null;
            TripFluencerPointsDTO points = pointsService.getUserPoints(userId, token);

            if (points != null) {
                return ResponseEntity.ok(points);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError("TripFluencer not found for user: " + userId,
                                HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            log.error("Error fetching points for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch user points: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Add points to a user (admin action)
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPoints(
            @RequestBody AddPointsDTO dto,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is adding {} points to user {}", admin.getEmail(), dto.getPoints(), dto.getUserId());

            String token = authHeader.replace("Bearer ", "");
            TripFluencerPointsDTO updated = pointsService.addPoints(dto, token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Points added successfully");
            response.put("pointsAdded", dto.getPoints());
            response.put("tripFluencer", updated);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding points: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to add points: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Deduct points from a user (admin action)
     */
    @PostMapping("/deduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deductPoints(
            @RequestBody DeductPointsDTO dto,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is deducting {} points from user {}",
                    admin.getEmail(), dto.getPoints(), dto.getUserId());

            String token = authHeader.replace("Bearer ", "");
            TripFluencerPointsDTO updated = pointsService.deductPoints(dto, token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Points deducted successfully");
            response.put("pointsDeducted", dto.getPoints());
            response.put("tripFluencer", updated);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deducting points: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error deducting points: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to deduct points: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get top earners
     */
    @GetMapping("/top-earners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTopEarners(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Admin {} is fetching top earners", authentication.getName());

            String token = authHeader.replace("Bearer ", "");
            List<TripFluencerPointsDTO> topEarners = pointsService.getTopEarners(token);

            Map<String, Object> response = new HashMap<>();
            response.put("topEarners", topEarners);
            response.put("total", topEarners.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching top earners: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch top earners: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStatistics(Authentication authentication) {
        try {
            log.info("Admin {} is fetching points statistics", authentication.getName());

            PointsStatisticsDTO stats = pointsService.getStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch statistics: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Toggle TripFluencer active status
     */
    @PatchMapping("/user/{userId}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleActive(
            @PathVariable Long userId,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is toggling active status for user {}", admin.getEmail(), userId);

            String token = authHeader.replace("Bearer ", "");
            TripFluencerPointsDTO updated = pointsService.toggleActive(userId, token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Active status toggled successfully");
            response.put("tripFluencer", updated);
            response.put("isActive", updated.getIsActive());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error toggling active status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to toggle active status: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}