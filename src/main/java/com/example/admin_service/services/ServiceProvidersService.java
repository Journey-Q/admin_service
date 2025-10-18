package com.example.admin_service.services;

import com.example.admin_service.dto.AllServiceproviderResponse;
import com.example.admin_service.dto.Providerdto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProvidersService {

    private final RestTemplate restTemplate;

    @Value("${serviceprovider.api.url}")
    private String serviceProviderApiUrl;

    public AllServiceproviderResponse getAllServiceProviders(String token) {
        try {
            String url = serviceProviderApiUrl + "/admin/auth/all_service";
            log.info("Fetching service providers from: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AllServiceproviderResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AllServiceproviderResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched {} service providers",
                    response.getBody().getProviders() != null ? response.getBody().getProviders().size() : 0);
                return response.getBody();
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return new AllServiceproviderResponse();
            }
        } catch (Exception e) {
            log.error("Error fetching service providers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch service providers from external API", e);
        }
    }

    public boolean approveServiceProvider(Long id, String token) {
        try {
            String url = serviceProviderApiUrl + "/admin/auth/providers/" + id + "/approve";
            log.info("Approving service provider with ID: {} at URL: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully approved service provider with ID: {}", id);
                return true;
            } else {
                log.warn("Failed to approve service provider. Response: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error approving service provider with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to approve service provider", e);
        }
    }

    public boolean disapproveServiceProvider(Long id, String token) {
        try {
            String url = serviceProviderApiUrl + "/admin/auth/providers/" + id + "/reject";
            log.info("Rejecting service provider with ID: {} at URL: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully rejected service provider with ID: {}", id);
                return true;
            } else {
                log.warn("Failed to reject service provider. Response: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error rejecting service provider with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to reject service provider", e);
        }
    }
}