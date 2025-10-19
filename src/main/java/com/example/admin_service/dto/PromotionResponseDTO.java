package com.example.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String image;
    private Integer discount;
    private String validFrom;
    private String validTo;
    private Boolean isActive;
    private String status; // PENDING, APPROVED, ADVERTISED, REJECTED
    private String serviceType;
    private String location;
    private String category;
    private Boolean featured;
    private Long serviceProviderId;
    private String submittedBy;
    private String submittedDate;
    private String reviewedBy;
    private String reviewedDate;
    private Integer bookings;
    private Double revenue;
    private Integer views;
    private Integer clicks;
    private Double rating;
    private String reviewComment;
}
