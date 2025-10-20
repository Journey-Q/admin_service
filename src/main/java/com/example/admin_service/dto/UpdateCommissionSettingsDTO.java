package com.example.admin_service.dto;

import com.example.admin_service.entity.Commission;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCommissionSettingsDTO {
    @NotNull(message = "Provider type is required")
    private Commission.ProviderType providerType;

    @NotNull(message = "Commission rate is required")
    @Positive(message = "Commission rate must be positive")
    private BigDecimal commissionRate;
}
