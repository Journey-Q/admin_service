package com.example.admin_service.controller;

import com.example.admin_service.dto.ApiError;
import com.example.admin_service.dto.PointRedemptionDTO;
import com.example.admin_service.dto.RedeemPointsDTO;
import com.example.admin_service.entity.AdminPrincipal;
import com.example.admin_service.services.PointRedemptionService;
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
 * Controller for managing point redemptions
 */
@RestController
@RequestMapping("/admin/auth/redemptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PointRedemptionController {

    private final PointRedemptionService redemptionService;

    /**
     * Get all redemptions
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRedemptions(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Admin {} is fetching all redemptions", authentication.getName());

            String token = authHeader.replace("Bearer ", "");
            List<PointRedemptionDTO> redemptions = redemptionService.getAllRedemptions(token);

            Map<String, Object> response = new HashMap<>();
            response.put("redemptions", redemptions);
            response.put("total", redemptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching redemptions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch redemptions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get redemptions by user (Public - accessible by all users)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRedemptionsByUser(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("Fetching redemptions for user: {}", userId);

            String token = authHeader != null ? authHeader.replace("Bearer ", "") : null;
            List<PointRedemptionDTO> redemptions = redemptionService.getRedemptionsByUser(userId, token);

            Map<String, Object> response = new HashMap<>();
            response.put("redemptions", redemptions);
            response.put("userId", userId);
            response.put("total", redemptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching redemptions for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch user redemptions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get redemptions by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRedemptionsByStatus(
            @PathVariable String status,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Admin {} is fetching redemptions with status: {}", authentication.getName(), status);

            String token = authHeader.replace("Bearer ", "");
            List<PointRedemptionDTO> redemptions = redemptionService.getRedemptionsByStatus(status, token);

            Map<String, Object> response = new HashMap<>();
            response.put("redemptions", redemptions);
            response.put("status", status.toUpperCase());
            response.put("total", redemptions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching redemptions by status {}: {}", status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch redemptions by status: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Redeem points (create new redemption) - Public - accessible by all users
     */
    @PostMapping
    public ResponseEntity<?> redeemPoints(@RequestBody RedeemPointsDTO dto) {
        try {
            log.info("Redeeming {} points for user {}", dto.getPointsToRedeem(), dto.getUserId());

            PointRedemptionDTO redemption = redemptionService.redeemPoints(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Points redeemed successfully");
            response.put("redemption", redemption);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error redeeming points: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error redeeming points: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to redeem points: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Cancel redemption
     */
    @PatchMapping("/{redemptionId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelRedemption(
            @PathVariable Long redemptionId,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is cancelling redemption: {}", admin.getEmail(), redemptionId);

            String token = authHeader.replace("Bearer ", "");
            PointRedemptionDTO cancelled = redemptionService.cancelRedemption(redemptionId, token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Redemption cancelled and points refunded successfully");
            response.put("redemption", cancelled);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error cancelling redemption: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error cancelling redemption: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to cancel redemption: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get redemption statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRedemptionStatistics(Authentication authentication) {
        try {
            log.info("Admin {} is fetching redemption statistics", authentication.getName());

            PointRedemptionService.RedemptionStatsDTO stats = redemptionService.getRedemptionStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching redemption statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch redemption statistics: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Expire old redemptions (manual trigger)
     */
    @PostMapping("/expire-old")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> expireOldRedemptions(Authentication authentication) {
        try {
            AdminPrincipal admin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is triggering expiration of old redemptions", admin.getEmail());

            redemptionService.expireOldRedemptions();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Old redemptions expired successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error expiring old redemptions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to expire old redemptions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}