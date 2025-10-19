package com.example.admin_service.controller;

import com.example.admin_service.dto.ApiError;
import com.example.admin_service.dto.CreatePromotionDTO;
import com.example.admin_service.dto.PromotionResponseDTO;
import com.example.admin_service.dto.UpdatePromotionStatusDTO;
import com.example.admin_service.entity.AdminPrincipal;
import com.example.admin_service.services.PromotionService;
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

@RestController
@RequestMapping("/admin/auth/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * Get all promotions
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPromotions(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<PromotionResponseDTO> promotions = promotionService.getAllPromotions(token);

            Map<String, Object> response = new HashMap<>();
            response.put("promotions", promotions);
            response.put("total", promotions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching all promotions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch promotions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get promotion by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPromotionById(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO promotion = promotionService.getPromotionById(id, token);

            if (promotion != null) {
                return ResponseEntity.ok(promotion);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError("Promotion not found", HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            log.error("Error fetching promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get promotions by status (PENDING, APPROVED, ADVERTISED, REJECTED)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPromotionsByStatus(
            @PathVariable String status,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<PromotionResponseDTO> promotions = promotionService.getPromotionsByStatus(status.toUpperCase(), token);

            Map<String, Object> response = new HashMap<>();
            response.put("promotions", promotions);
            response.put("total", promotions.size());
            response.put("status", status);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching promotions by status {}: {}", status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch promotions by status: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get active promotions
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getActivePromotions(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<PromotionResponseDTO> promotions = promotionService.getActivePromotions(token);

            Map<String, Object> response = new HashMap<>();
            response.put("promotions", promotions);
            response.put("total", promotions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching active promotions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch active promotions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Approve promotion
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approvePromotion(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) UpdatePromotionStatusDTO statusDTO) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is approving promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO updatedPromotion = promotionService.updatePromotionStatus(id, "APPROVED", token);

            if (updatedPromotion != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion approved successfully");
                response.put("promotion", updatedPromotion);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to approve promotion", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error approving promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to approve promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Reject promotion
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectPromotion(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) UpdatePromotionStatusDTO statusDTO) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is rejecting promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO updatedPromotion = promotionService.updatePromotionStatus(id, "REJECTED", token);

            if (updatedPromotion != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion rejected successfully");
                response.put("promotion", updatedPromotion);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to reject promotion", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error rejecting promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to reject promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Advertise promotion (change status to ADVERTISED)
     */
    @PutMapping("/{id}/advertise")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> advertisePromotion(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is advertising promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO updatedPromotion = promotionService.updatePromotionStatus(id, "ADVERTISED", token);

            if (updatedPromotion != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion advertised successfully");
                response.put("promotion", updatedPromotion);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to advertise promotion", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error advertising promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to advertise promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Toggle promotion active status (pause/resume)
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> togglePromotionActive(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is toggling active status for promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO updatedPromotion = promotionService.togglePromotionActive(id, token);

            if (updatedPromotion != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion active status toggled successfully");
                response.put("promotion", updatedPromotion);
                response.put("isActive", updatedPromotion.getIsActive());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to toggle promotion active status", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error toggling active status for promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to toggle promotion active status: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Update promotion
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePromotion(
            @PathVariable Long id,
            @RequestBody CreatePromotionDTO dto,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is updating promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            PromotionResponseDTO updatedPromotion = promotionService.updatePromotion(id, dto, token);

            if (updatedPromotion != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion updated successfully");
                response.put("promotion", updatedPromotion);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to update promotion", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error updating promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to update promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Delete promotion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePromotion(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is deleting promotion with ID: {}", currentAdmin.getEmail(), id);

            String token = authHeader.replace("Bearer ", "");
            boolean deleted = promotionService.deletePromotion(id, token);

            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Promotion deleted successfully");
                response.put("promotionId", id);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to delete promotion", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error deleting promotion with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to delete promotion: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Bulk approve promotions
     */
    @PutMapping("/bulk/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkApprovePromotions(
            @RequestBody List<Long> promotionIds,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is bulk approving {} promotions", currentAdmin.getEmail(), promotionIds.size());

            String token = authHeader.replace("Bearer ", "");
            int successCount = 0;
            int failCount = 0;

            for (Long id : promotionIds) {
                try {
                    PromotionResponseDTO result = promotionService.updatePromotionStatus(id, "APPROVED", token);
                    if (result != null) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to approve promotion {}: {}", id, e.getMessage());
                    failCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bulk approval completed");
            response.put("total", promotionIds.size());
            response.put("successful", successCount);
            response.put("failed", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk approve: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to bulk approve promotions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Bulk reject promotions
     */
    @PutMapping("/bulk/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkRejectPromotions(
            @RequestBody List<Long> promotionIds,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is bulk rejecting {} promotions", currentAdmin.getEmail(), promotionIds.size());

            String token = authHeader.replace("Bearer ", "");
            int successCount = 0;
            int failCount = 0;

            for (Long id : promotionIds) {
                try {
                    PromotionResponseDTO result = promotionService.updatePromotionStatus(id, "REJECTED", token);
                    if (result != null) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to reject promotion {}: {}", id, e.getMessage());
                    failCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bulk rejection completed");
            response.put("total", promotionIds.size());
            response.put("successful", successCount);
            response.put("failed", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk reject: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to bulk reject promotions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Bulk delete promotions
     */
    @DeleteMapping("/bulk/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkDeletePromotions(
            @RequestBody List<Long> promotionIds,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is bulk deleting {} promotions", currentAdmin.getEmail(), promotionIds.size());

            String token = authHeader.replace("Bearer ", "");
            int successCount = 0;
            int failCount = 0;

            for (Long id : promotionIds) {
                try {
                    boolean deleted = promotionService.deletePromotion(id, token);
                    if (deleted) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to delete promotion {}: {}", id, e.getMessage());
                    failCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bulk deletion completed");
            response.put("total", promotionIds.size());
            response.put("successful", successCount);
            response.put("failed", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk delete: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to bulk delete promotions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
