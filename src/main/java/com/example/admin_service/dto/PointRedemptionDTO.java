package com.example.admin_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointRedemptionDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer pointsUsed;
    private Integer discountPercentage;
    private String subscriptionType;
    private Double originalPrice;
    private Double discountedPrice;
    private Long subscriptionId;
    private String status;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiresAt;
}