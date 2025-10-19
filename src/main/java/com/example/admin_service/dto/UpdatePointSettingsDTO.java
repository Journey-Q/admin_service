package com.example.admin_service.dto;

import lombok.Data;

@Data
public class UpdatePointSettingsDTO {
    private String tierName;
    private Integer pointsPerMilestone;
}