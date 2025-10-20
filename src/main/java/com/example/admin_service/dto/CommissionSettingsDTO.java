package com.example.admin_service.dto;

import com.example.admin_service.entity.Commission;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionSettingsDTO {
    private Long id;
    private Commission.ProviderType providerType;
    private BigDecimal commissionRate;
    private Boolean isActive;
}
