// DTO
package com.example.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateSubscriptionPlanDTO {
    private String name;
    private String type;
    private String price;
    private String interval;
    private String description;
    private List<String> features;
    private Boolean isActive;
    private Boolean hasDiscount;
    private String discountPercentage;
}