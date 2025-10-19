package com.example.admin_service.dto;

import lombok.Data;

@Data
public class PointSettingsDTO {
    private Long id;
    private String tierName;
    private Integer minLikes;
    private Integer maxLikes;
    private Integer pointsPerMilestone;
    private Boolean isActive;
}