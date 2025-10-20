package com.example.admin_service.controller;

import com.example.admin_service.dto.*;
import com.example.admin_service.entity.Commission;
import com.example.admin_service.services.CommissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing commissions
 */
@RestController
@RequestMapping("/admin/auth/commissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CommissionController {

    private final CommissionService commissionService;

    /**
     * Get all commissions
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCommissions(Authentication authentication) {
        try {
            log.info("Admin {} is fetching all commissions", authentication.getName());

            List<CommissionDTO> commissions = commissionService.getAllCommissions();

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commissions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commission by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionById(@PathVariable Long id, Authentication authentication) {
        try {
            log.info("Admin {} is fetching commission with ID: {}", authentication.getName(), id);

            CommissionDTO commission = commissionService.getCommissionById(id);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            log.error("Error fetching commission {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Error fetching commission {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commission: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commission by transaction ID
     */
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionByTransactionId(@PathVariable String transactionId,
                                                          Authentication authentication) {
        try {
            log.info("Admin {} is fetching commission with transaction ID: {}",
                    authentication.getName(), transactionId);

            CommissionDTO commission = commissionService.getCommissionByTransactionId(transactionId);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            log.error("Error fetching commission with transaction ID {}: {}", transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Error fetching commission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commission: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commissions by provider type
     */
    @GetMapping("/type/{providerType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionsByProviderType(@PathVariable Commission.ProviderType providerType,
                                                          Authentication authentication) {
        try {
            log.info("Admin {} is fetching commissions for provider type: {}",
                    authentication.getName(), providerType);

            List<CommissionDTO> commissions = commissionService.getCommissionsByProviderType(providerType);

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("providerType", providerType);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commissions for provider type {}: {}", providerType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commissions by service provider
     */
    @GetMapping("/provider/{serviceProvider}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionsByServiceProvider(@PathVariable String serviceProvider,
                                                            Authentication authentication) {
        try {
            log.info("Admin {} is fetching commissions for service provider: {}",
                    authentication.getName(), serviceProvider);

            List<CommissionDTO> commissions = commissionService.getCommissionsByServiceProvider(serviceProvider);

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("serviceProvider", serviceProvider);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commissions for service provider {}: {}", serviceProvider, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commissions by month
     */
    @GetMapping("/month/{month}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionsByMonth(@PathVariable String month, Authentication authentication) {
        try {
            log.info("Admin {} is fetching commissions for month: {}", authentication.getName(), month);

            List<CommissionDTO> commissions = commissionService.getCommissionsByMonth(month);

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("month", month);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commissions for month {}: {}", month, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Search commissions
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchCommissions(@RequestParam String query, Authentication authentication) {
        try {
            log.info("Admin {} is searching commissions with query: {}", authentication.getName(), query);

            List<CommissionDTO> commissions = commissionService.searchCommissions(query);

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("query", query);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching commissions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to search commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commissions by date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCommissionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            log.info("Admin {} is fetching commissions from {} to {}",
                    authentication.getName(), startDate, endDate);

            List<CommissionDTO> commissions = commissionService.getCommissionsByDateRange(startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("commissions", commissions);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("total", commissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching commissions by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch commissions: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get distinct service providers
     */
    @GetMapping("/providers/distinct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDistinctServiceProviders(Authentication authentication) {
        try {
            log.info("Admin {} is fetching distinct service providers", authentication.getName());

            List<String> providers = commissionService.getDistinctServiceProviders();

            Map<String, Object> response = new HashMap<>();
            response.put("providers", providers);
            response.put("total", providers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching distinct service providers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch service providers: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get distinct months
     */
    @GetMapping("/months/distinct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDistinctMonths(Authentication authentication) {
        try {
            log.info("Admin {} is fetching distinct months", authentication.getName());

            List<String> months = commissionService.getDistinctMonths();

            Map<String, Object> response = new HashMap<>();
            response.put("months", months);
            response.put("total", months.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching distinct months: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch months: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Get commission statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStatistics(Authentication authentication) {
        try {
            log.info("Admin {} is fetching commission statistics", authentication.getName());

            Map<String, Object> stats = commissionService.calculateStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching commission statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch statistics: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Create new commission
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCommission(@Valid @RequestBody CreateCommissionDTO dto,
                                             Authentication authentication) {
        try {
            log.info("Admin {} is creating a new commission for provider: {}",
                    authentication.getName(), dto.getServiceProvider());

            CommissionDTO commission = commissionService.createCommission(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commission created successfully");
            response.put("commission", commission);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating commission: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error creating commission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to create commission: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Delete commission
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCommission(@PathVariable Long id, Authentication authentication) {
        try {
            log.info("Admin {} is deleting commission with ID: {}", authentication.getName(), id);

            commissionService.deleteCommission(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commission deleted successfully");
            response.put("id", id);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting commission {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Error deleting commission {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to delete commission: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
