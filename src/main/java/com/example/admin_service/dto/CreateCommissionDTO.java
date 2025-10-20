package com.example.admin_service.dto;

import com.example.admin_service.entity.Commission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCommissionDTO {
    @NotBlank(message = "Service provider is required")
    private String serviceProvider;

    @NotNull(message = "Provider type is required")
    private Commission.ProviderType providerType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Percentage is required")
    @Positive(message = "Percentage must be positive")
    private BigDecimal percentage;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Revenue is required")
    @Positive(message = "Revenue must be positive")
    private BigDecimal revenue;

    private String businessRegNo;
    private String paymentMethod;
    private Integer customerCount;
    private String month;
    private String location;
    private String phone;
    private String website;
    private String address;
}
