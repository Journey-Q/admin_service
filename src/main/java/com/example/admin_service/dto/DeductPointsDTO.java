package com.example.admin_service.dto;

import lombok.Data;

@Data
public class DeductPointsDTO {
    private Long userId;
    private Integer points;
    private String reason; // Optional: reason for deducting points
}