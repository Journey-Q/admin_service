// services/AdminService.java
package com.example.admin_service.services;

import com.example.admin_service.dto.AdminAuthResponse;
import com.example.admin_service.dto.AdminCreateRequest;
import com.example.admin_service.dto.AdminLoginRequest;
import com.example.admin_service.entity.Admin;
import com.example.admin_service.entity.AdminPrincipal;
import com.example.admin_service.exception.BadRequestException;
import com.example.admin_service.repository.AdminRepo;
import com.example.admin_service.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    @Lazy
    private final AuthenticationManager authenticationManager;

    public AdminAuthResponse verify(AdminLoginRequest req) {
        try {
            // Check if email exists first
            boolean emailExists = adminRepo.existsByEmail(req.getEmail());
            if (!emailExists) {
                throw new BadRequestException("Email does not exist");
            }

            // Email exists, now authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            AdminPrincipal userPrincipal = (AdminPrincipal) authentication.getPrincipal();
            Admin admin = userPrincipal.getAdmin();
            String token = jwtService.generateToken(userPrincipal);

            AdminAuthResponse authResponse = new AdminAuthResponse();
            authResponse.setAdmin(admin);
            authResponse.setAccessToken(token);
            authResponse.setExpiresIn(jwtService.getExpirationTime());

            return authResponse;

        } catch (BadRequestException e) {
            throw e; // Re-throw email not found error
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password");
        } catch (UsernameNotFoundException e) {
            // This shouldn't happen since we check email existence first
            throw new BadRequestException("Email does not exist");
        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable", e);
        }
    }

    @Transactional
    public Admin createAdmin(AdminCreateRequest request) {
        log.info("Creating new admin with email: {}", request.getEmail());

        // Validate password confirmation
        if (!request.isPasswordConfirmed()) {
            throw new BadRequestException("Passwords do not match");
        }

        // Check if admin with same email exists
        if (adminRepo.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Admin with this email already exists");
        }

        // Check if admin with same username exists
        if (adminRepo.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Admin with this username already exists");
        }

        try {
            // Create new admin entity
            Admin admin = new Admin();
            admin.setUsername(request.getUsername());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

            // Save admin to database
            Admin savedAdmin = adminRepo.save(admin);
            log.info("Successfully created admin with ID: {}", savedAdmin.getId());

            return savedAdmin;

        } catch (Exception e) {
            log.error("Error creating admin: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create admin", e);
        }
    }

    public Admin findByEmail(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin findByUsername(String username) {
        return adminRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    // Method to create default admin (for testing)
    public Admin createDefaultAdmin() {
        if (!adminRepo.existsByEmail("admin@serviceproviders.com")) {
            Admin admin = new Admin(
                    "admin",
                    "admin@serviceproviders.com",
                    passwordEncoder.encode("admin123")
            );
            return adminRepo.save(admin);
        }
        return adminRepo.findByEmail("admin@serviceproviders.com").orElse(null);
    }

    // Get all admins (optional - for admin management)
    public java.util.List<Admin> getAllAdmins() {
        return adminRepo.findAll();
    }

    // Check if admin exists by ID
    public boolean existsById(Long id) {
        return adminRepo.existsById(id);
    }

    // Update admin status (activate/deactivate)
    @Transactional
    public Admin updateAdminStatus(Long id, Boolean isActive) {
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Admin not found"));

        admin.setIsActive(isActive);
        return adminRepo.save(admin);
    }
}