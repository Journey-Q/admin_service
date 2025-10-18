package com.example.admin_service.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Providerdto {
    private Long id;
    private String username;
    private String email;
    private String serviceType;
    private String businessRegistrationNumber;
    private String address;
    private String contactNo;
    private Boolean isApproved;
    private Boolean isActive;
    private String createdAt;
}
