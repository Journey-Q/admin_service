package com.example.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePromotionStatusDTO {
    private String status; // APPROVED, REJECTED, ADVERTISED
    private String reviewComment;
}
