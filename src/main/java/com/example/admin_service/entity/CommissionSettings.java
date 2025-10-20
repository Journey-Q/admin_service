package com.example.admin_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity to store commission rate settings for different provider types
 */
@Entity
@Table(name = "commission_settings")
@Getter
@Setter
public class CommissionSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, unique = true)
    private Commission.ProviderType providerType;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public CommissionSettings() {
        this.isActive = true;
    }

    public CommissionSettings(Commission.ProviderType providerType, BigDecimal commissionRate) {
        this();
        this.providerType = providerType;
        this.commissionRate = commissionRate;
    }
}
