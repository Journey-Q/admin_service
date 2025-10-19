package com.example.admin_service.dto;

import lombok.Data;

@Data
public class RedeemPointsDTO {
    private Long userId;
    private Integer pointsToRedeem;
    private String subscriptionType; // MONTHLY_PREMIUM
}