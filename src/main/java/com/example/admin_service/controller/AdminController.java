// controller/AdminController.java
package com.example.admin_service.controller;

import com.example.admin_service.dto.*;
import com.example.admin_service.dto.ApiError;
import com.example.admin_service.entity.Admin;
import com.example.admin_service.entity.AdminPrincipal;
import com.example.admin_service.exception.BadRequestException;
import com.example.admin_service.services.AdminService;
import com.example.admin_service.services.ServiceProvidersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    private final ServiceProvidersService serviceProvidersService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            AdminAuthResponse response = adminService.verify(request);
            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminCreateRequest request,
                                         BindingResult bindingResult,
                                         Authentication authentication) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            // Log who is creating the admin
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is creating a new admin with email: {}",
                    currentAdmin.getEmail(), request.getEmail());

            // Create the admin
            Admin createdAdmin = adminService.createAdmin(request);

            // Prepare response
            AdminCreateResponse response = new AdminCreateResponse(createdAdmin, "Admin created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BadRequestException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Error creating admin: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to create admin: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            AdminPrincipal principal = (AdminPrincipal) authentication.getPrincipal();
            Admin admin = principal.getAdmin();

            // Create a response object without sensitive information
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", admin.getId());
            profileData.put("username", admin.getUsername());
            profileData.put("email", admin.getEmail());
            profileData.put("role", admin.getRole());
            profileData.put("isActive", admin.getIsActive());
            profileData.put("createdAt", admin.getCreatedAt());

            return ResponseEntity.ok(profileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("Unauthorized", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAdmins(Authentication authentication) {
        try {
            List<Admin> admins = adminService.getAllAdmins();

            // Convert to response format without sensitive data
            List<Map<String, Object>> adminList = admins.stream()
                    .map(admin -> {
                        Map<String, Object> adminData = new HashMap<>();
                        adminData.put("id", admin.getId());
                        adminData.put("username", admin.getUsername());
                        adminData.put("email", admin.getEmail());
                        adminData.put("role", admin.getRole());
                        adminData.put("isActive", admin.getIsActive());
                        adminData.put("createdAt", admin.getCreatedAt());
                        return adminData;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("admins", adminList);
            response.put("total", adminList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to retrieve admins: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminStatus(@PathVariable Long id,
                                               @RequestBody Map<String, Boolean> statusRequest,
                                               Authentication authentication) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();

            // Prevent admin from deactivating themselves
            if (currentAdmin.getId().equals(id) && !statusRequest.get("isActive")) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Cannot deactivate your own account", HttpStatus.BAD_REQUEST.value()));
            }

            Boolean isActive = statusRequest.get("isActive");
            if (isActive == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("isActive field is required", HttpStatus.BAD_REQUEST.value()));
            }

            Admin updatedAdmin = adminService.updateAdminStatus(id, isActive);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin status updated successfully");
            response.put("admin", Map.of(
                    "id", updatedAdmin.getId(),
                    "username", updatedAdmin.getUsername(),
                    "email", updatedAdmin.getEmail(),
                    "isActive", updatedAdmin.getIsActive()
            ));

            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to update admin status: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin API is working!");
    }

    // Endpoint to create default admin (for testing only)
    @PostMapping("/setup")
    public ResponseEntity<?> setupDefaultAdmin() {
        try {
            Admin admin = adminService.createDefaultAdmin();
            if (admin != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Default admin created/exists");
                response.put("email", admin.getEmail());
                response.put("username", admin.getUsername());
                response.put("defaultPassword", "admin123");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to create admin", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Setup failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/all_service")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllServiceProviders(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            String token = authHeader.replace("Bearer ", "");

            AllServiceproviderResponse response = serviceProvidersService.getAllServiceProviders(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching service providers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to fetch service providers: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/providers/{providerId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveService(
            @PathVariable Long providerId,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is approving service provider with ID: {}", currentAdmin.getEmail(), providerId);

            // Extract token from "Bearer <token>"
            String token = authHeader.replace("Bearer ", "");

            boolean response = serviceProvidersService.approveServiceProvider(providerId, token);
            if (response) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Service provider approved successfully");
                responseMap.put("providerId", providerId);
                return ResponseEntity.ok(responseMap);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to approve service provider", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error approving service provider: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to approve service provider: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/providers/{providerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectService(
            @PathVariable Long providerId,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        try {
            AdminPrincipal currentAdmin = (AdminPrincipal) authentication.getPrincipal();
            log.info("Admin {} is rejecting service provider with ID: {}", currentAdmin.getEmail(), providerId);

            // Extract token from "Bearer <token>"
            String token = authHeader.replace("Bearer ", "");

            boolean response = serviceProvidersService.disapproveServiceProvider(providerId, token);
            if (response) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Service provider rejected successfully");
                responseMap.put("providerId", providerId);
                return ResponseEntity.ok(responseMap);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to reject service provider", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            log.error("Error rejecting service provider: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Failed to reject service provider: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


}