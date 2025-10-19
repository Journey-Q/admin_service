package com.example.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePromotionDTO {
    private String title;
    private String description;
    private String image;
    private Integer discount;
    private String validFrom;
    private String validTo;
    private Boolean isActive;
    private String status;
    private String serviceType;
    private String location;
    private String category;
    private Boolean featured;
    private Long serviceProviderId;
}
