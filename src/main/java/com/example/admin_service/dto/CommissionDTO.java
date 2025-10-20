package com.example.admin_service.dto;

import com.example.admin_service.entity.Commission;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CommissionDTO {
    private Long id;
    private String serviceProvider;
    private Commission.ProviderType providerType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private LocalDate date;
    private String transactionId;
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
