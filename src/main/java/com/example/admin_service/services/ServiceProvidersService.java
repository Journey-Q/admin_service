//package com.example.admin_service.services;
//
//import com.example.admin_service.dto.AllServiceproviderResponse;
//import com.example.admin_service.dto.Providerdto;
//import com.example.admin_service.entity.ServiceProvider;
//import com.example.admin_service.repository.ServiceProviderRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ServiceProvidersService {
//
//    @Autowired
//    private ServiceProviderRepo repo;
//
//    @Transactional
//    public AllServiceproviderResponse getAllServiceProviders() {
//        List<ServiceProvider> providers = repo.findAll();
//
//        // Map to ProviderDTO and format createdAt as string
//        List<Providerdto> providerDTOs = providers.stream().map(provider -> {
//            Providerdto dto = new Providerdto();
//            dto.setId(provider.getId());
//            dto.setUsername(provider.getUsername());
//            dto.setEmail(provider.getEmail());
//            dto.setServiceType(provider.getServiceType());
//            dto.setBusinessRegistrationNumber(provider.getBusinessRegistrationNumber());
//            dto.setAddress(provider.getAddress());
//            dto.setContactNo(provider.getContactNo());
//            dto.setIsApproved(provider.getIsApproved());
//            dto.setIsActive(provider.getIsActive());
//            // Convert LocalDateTime to string (ISO 8601 format for frontend compatibility)
//            dto.setCreatedAt(provider.getCreatedAt() != null
//                    ? provider.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//                    : null);
//            return dto;
//        }).collect(Collectors.toList());
//
//        AllServiceproviderResponse response = new AllServiceproviderResponse();
//        response.setProviders(providerDTOs);
//        return response;
//    }
//
//    public boolean approveServiceProvider(Long id) {
//        ServiceProvider provider = repo.findById(id).orElseThrow(() -> new RuntimeException("Service provider not found"));
//        provider.setIsApproved(true);
//        repo.save(provider);
//        System.out.println("Approved service provider with ID: " + id);
//        return true;
//    }
//
//    public boolean disapproveServiceProvider(Long id) {
//        ServiceProvider provider = repo.findById(id).orElseThrow(() -> new RuntimeException("Service provider not found"));
//        provider.setIsApproved(false);
//        repo.save(provider);
//        System.out.println("Disapproved service provider with ID: " + id);
//        return true;
//    }
//}