package com.example.admin_service.dto;

import lombok.Data;

@Data
public class PointsStatisticsDTO {
    private Long activeTripFluencers;
    private Long totalPointsEarned;
    private Long totalPointsUsed;
    private Long activeSubscriptions;
    private Integer averagePointsPerUser;
}