package com.example.admin_service.dto;

import lombok.Data;

@Data
public class AddPointsDTO {
    private Long userId;
    private Integer points;
    private String reason; // Optional: reason for adding points
}