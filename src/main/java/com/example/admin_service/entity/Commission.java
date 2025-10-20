package com.example.admin_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity to store commission transactions from service providers
 */
@Entity
@Table(name = "commissions")
@Getter
@Setter
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_provider", nullable = false)
    private String serviceProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "revenue", nullable = false, precision = 10, scale = 2)
    private BigDecimal revenue;

    @Column(name = "business_reg_no")
    private String businessRegNo;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "customer_count")
    private Integer customerCount;

    @Column(name = "month")
    private String month;

    @Column(name = "location")
    private String location;

    @Column(name = "phone")
    private String phone;

    @Column(name = "website")
    private String website;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ProviderType {
        HOTEL,
        TRAVEL_SERVICE,
        TOUR_SERVICE
    }
}
