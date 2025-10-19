package com.example.admin_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripFluencerPointsDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String profileImage;
    private Integer currentPoints;
    private Integer totalPointsEarned;
    private Integer pointsUsed;
    private Integer totalLikes;
    private Integer followers;
    private String tier;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}