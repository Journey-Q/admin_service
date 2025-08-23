// entity/AdminPrincipal.java
package com.example.admin_service.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class AdminPrincipal implements UserDetails {

    private Admin admin;

    public AdminPrincipal(Admin admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Admin has ROLE_ADMIN authority
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return admin.getIsActive() != null ? admin.getIsActive() : false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return admin.getIsActive() != null ? admin.getIsActive() : false;
    }

    // Additional methods to access the wrapped Admin entity
    public Admin getAdmin() {
        return admin;
    }

    public Long getId() {
        return admin.getId();
    }

    public String getEmail() {
        return admin.getEmail();
    }

    public String getRole() {
        return admin.getRole();
    }

    public Boolean getIsActive() {
        return admin.getIsActive();
    }

    public LocalDateTime getCreatedAt() {
        return admin.getCreatedAt();
    }

    public boolean isActive() {
        return admin.getIsActive() != null ? admin.getIsActive() : false;
    }
}