package com.example.admin_service.services;

import com.example.admin_service.dto.CreatePromotionDTO;
import com.example.admin_service.dto.PromotionResponseDTO;
import com.example.admin_service.dto.UpdatePromotionStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final RestTemplate restTemplate;

    @Value("${serviceprovider.api.url}")
    private String serviceProviderApiUrl;

    /**
     * Get all promotions
     */
    public List<PromotionResponseDTO> getAllPromotions(String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/all";
            log.info("Fetching all promotions from: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<PromotionResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<PromotionResponseDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched {} promotions", response.getBody().size());
                return response.getBody();
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching promotions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch promotions from external API", e);
        }
    }

    /**
     * Get promotion by ID
     */
    public PromotionResponseDTO getPromotionById(Long id, String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/" + id;
            log.info("Fetching promotion with ID: {} from: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<PromotionResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PromotionResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched promotion with ID: {}", id);
                return response.getBody();
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching promotion with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch promotion", e);
        }
    }

    /**
     * Get promotions by status
     */
    public List<PromotionResponseDTO> getPromotionsByStatus(String status, String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/status/" + status;
            log.info("Fetching promotions with status: {} from: {}", status, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<PromotionResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<PromotionResponseDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched {} promotions with status: {}", response.getBody().size(), status);
                return response.getBody();
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching promotions by status {}: {}", status, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch promotions by status", e);
        }
    }

    /**
     * Get active promotions
     */
    public List<PromotionResponseDTO> getActivePromotions(String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/active";
            log.info("Fetching active promotions from: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<PromotionResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<PromotionResponseDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched {} active promotions", response.getBody().size());
                return response.getBody();
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching active promotions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch active promotions", e);
        }
    }

    /**
     * Update promotion status (Approve/Reject/Advertise)
     */
    public PromotionResponseDTO updatePromotionStatus(Long id, String status, String token) {
        try {
            // Clean the token - remove any newlines or whitespace
            String cleanToken = token.trim().replaceAll("\\s+", "");

            // Build URL properly with encoding
            String url = UriComponentsBuilder
                    .fromHttpUrl(serviceProviderApiUrl + "/service/promotions/" + id + "/status")
                    .queryParam("status", status.trim())
                    .build()
                    .toUriString();

            log.info("Updating promotion {} status to: {} at URL: {}", id, status, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cleanToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<PromotionResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                entity,
                PromotionResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully updated promotion {} status to: {}", id, status);
                return response.getBody();
            } else {
                log.warn("Failed to update promotion status. Response: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating promotion {} status: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update promotion status", e);
        }
    }

    /**
     * Toggle promotion active status
     */
    public PromotionResponseDTO togglePromotionActive(Long id, String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/" + id + "/toggle-active";
            log.info("Toggling promotion {} active status at URL: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<PromotionResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                entity,
                PromotionResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully toggled promotion {} active status", id);
                return response.getBody();
            } else {
                log.warn("Failed to toggle promotion active status. Response: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error toggling promotion {} active status: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to toggle promotion active status", e);
        }
    }

    /**
     * Update promotion
     */
    public PromotionResponseDTO updatePromotion(Long id, CreatePromotionDTO dto, String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/" + id;
            log.info("Updating promotion with ID: {} at URL: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreatePromotionDTO> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<PromotionResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                PromotionResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully updated promotion with ID: {}", id);
                return response.getBody();
            } else {
                log.warn("Failed to update promotion. Response: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating promotion with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update promotion", e);
        }
    }

    /**
     * Delete promotion
     */
    public boolean deletePromotion(Long id, String token) {
        try {
            String url = serviceProviderApiUrl + "/service/promotions/" + id;
            log.info("Deleting promotion with ID: {} at URL: {}", id, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully deleted promotion with ID: {}", id);
                return true;
            } else {
                log.warn("Failed to delete promotion. Response: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting promotion with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete promotion", e);
        }
    }
}
