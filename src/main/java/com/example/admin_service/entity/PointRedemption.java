package com.example.admin_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to track point redemption history
 */
@Entity
@Table(name = "point_redemptions")
@Getter
@Setter
public class PointRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed;

    @Column(name = "discount_percentage", nullable = false)
    private Integer discountPercentage;

    @Column(name = "subscription_type")
    private String subscriptionType = "MONTHLY_PREMIUM"; // Currently only monthly

    @Column(name = "original_price", nullable = false)
    private Double originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private Double discountedPrice;

    @Column(name = "subscription_id")
    private Long subscriptionId; // Reference to subscription in subscription service

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, EXPIRED, CANCELLED

    @Column(name = "redeemed_at")
    @CreationTimestamp
    private LocalDateTime redeemedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public PointRedemption() {
        this.status = "ACTIVE";
        this.subscriptionType = "MONTHLY_PREMIUM";
    }

    public PointRedemption(Long userId, Integer pointsUsed, Double originalPrice) {
        this();
        this.userId = userId;
        this.pointsUsed = pointsUsed;
        this.discountPercentage = pointsUsed; // 1 point = 1% discount
        this.originalPrice = originalPrice;
        this.discountedPrice = calculateDiscountedPrice(originalPrice, pointsUsed);
        this.expiresAt = LocalDateTime.now().plusMonths(1); // Monthly subscription
    }

    /**
     * Calculate discounted price based on points
     */
    private Double calculateDiscountedPrice(Double originalPrice, Integer points) {
        int discount = Math.min(points, 100); // Max 100% discount
        double discountAmount = originalPrice * (discount / 100.0);
        return Math.max(0, originalPrice - discountAmount);
    }

    @Override
    public String toString() {
        return "PointRedemption{" +
                "id=" + id +
                ", userId=" + userId +
                ", pointsUsed=" + pointsUsed +
                ", discountPercentage=" + discountPercentage +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", originalPrice=" + originalPrice +
                ", discountedPrice=" + discountedPrice +
                ", status='" + status + '\'' +
                ", redeemedAt=" + redeemedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}