package com.example.admin_service.services;

import com.example.admin_service.dto.CommissionDTO;
import com.example.admin_service.dto.CreateCommissionDTO;
import com.example.admin_service.entity.Commission;
import com.example.admin_service.repository.CommissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionService {

    private final CommissionRepository commissionRepository;

    /**
     * Get all commissions
     */
    public List<CommissionDTO> getAllCommissions() {
        return commissionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commission by ID
     */
    public CommissionDTO getCommissionById(Long id) {
        return commissionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Commission not found with id: " + id));
    }

    /**
     * Get commission by transaction ID
     */
    public CommissionDTO getCommissionByTransactionId(String transactionId) {
        return commissionRepository.findByTransactionId(transactionId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Commission not found with transaction ID: " + transactionId));
    }

    /**
     * Get commissions by provider type
     */
    public List<CommissionDTO> getCommissionsByProviderType(Commission.ProviderType providerType) {
        return commissionRepository.findByProviderType(providerType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commissions by service provider
     */
    public List<CommissionDTO> getCommissionsByServiceProvider(String serviceProvider) {
        return commissionRepository.findByServiceProvider(serviceProvider)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commissions by month
     */
    public List<CommissionDTO> getCommissionsByMonth(String month) {
        return commissionRepository.findByMonth(month)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search commissions
     */
    public List<CommissionDTO> searchCommissions(String searchTerm) {
        return commissionRepository.searchCommissions(searchTerm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get commissions by date range
     */
    public List<CommissionDTO> getCommissionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return commissionRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get distinct service providers
     */
    public List<String> getDistinctServiceProviders() {
        return commissionRepository.findDistinctServiceProviders();
    }

    /**
     * Get distinct months
     */
    public List<String> getDistinctMonths() {
        return commissionRepository.findDistinctMonths();
    }

    /**
     * Create new commission
     */
    @Transactional
    public CommissionDTO createCommission(CreateCommissionDTO dto) {
        // Check if transaction ID already exists
        if (commissionRepository.findByTransactionId(dto.getTransactionId()).isPresent()) {
            throw new RuntimeException("Commission with transaction ID " + dto.getTransactionId() + " already exists");
        }

        Commission commission = new Commission();
        commission.setServiceProvider(dto.getServiceProvider());
        commission.setProviderType(dto.getProviderType());
        commission.setAmount(dto.getAmount());
        commission.setPercentage(dto.getPercentage());
        commission.setDate(dto.getDate());
        commission.setTransactionId(dto.getTransactionId());
        commission.setRevenue(dto.getRevenue());
        commission.setBusinessRegNo(dto.getBusinessRegNo());
        commission.setPaymentMethod(dto.getPaymentMethod());
        commission.setCustomerCount(dto.getCustomerCount());
        commission.setMonth(dto.getMonth());
        commission.setLocation(dto.getLocation());
        commission.setPhone(dto.getPhone());
        commission.setWebsite(dto.getWebsite());
        commission.setAddress(dto.getAddress());

        Commission saved = commissionRepository.save(commission);
        log.info("Created new commission: {} for provider: {}", saved.getTransactionId(), saved.getServiceProvider());

        return convertToDTO(saved);
    }

    /**
     * Delete commission
     */
    @Transactional
    public void deleteCommission(Long id) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission not found with id: " + id));

        commissionRepository.delete(commission);
        log.info("Deleted commission: {}", commission.getTransactionId());
    }

    /**
     * Calculate statistics
     */
    public Map<String, Object> calculateStatistics() {
        List<Commission> allCommissions = commissionRepository.findAll();

        Map<String, Object> stats = new HashMap<>();

        // Total commissions and revenue
        BigDecimal totalCommissions = allCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = allCommissions.stream()
                .map(Commission::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalCommissions", totalCommissions);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalTransactions", allCommissions.size());

        // By provider type
        Map<String, BigDecimal> commissionsByType = new HashMap<>();
        Map<String, Long> countByType = new HashMap<>();

        for (Commission.ProviderType type : Commission.ProviderType.values()) {
            List<Commission> typeCommissions = allCommissions.stream()
                    .filter(c -> c.getProviderType() == type)
                    .collect(Collectors.toList());

            BigDecimal typeTotal = typeCommissions.stream()
                    .map(Commission::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            commissionsByType.put(type.name(), typeTotal);
            countByType.put(type.name(), (long) typeCommissions.size());
        }

        stats.put("commissionsByType", commissionsByType);
        stats.put("countByType", countByType);

        return stats;
    }

    /**
     * Convert entity to DTO
     */
    private CommissionDTO convertToDTO(Commission entity) {
        CommissionDTO dto = new CommissionDTO();
        dto.setId(entity.getId());
        dto.setServiceProvider(entity.getServiceProvider());
        dto.setProviderType(entity.getProviderType());
        dto.setAmount(entity.getAmount());
        dto.setPercentage(entity.getPercentage());
        dto.setDate(entity.getDate());
        dto.setTransactionId(entity.getTransactionId());
        dto.setRevenue(entity.getRevenue());
        dto.setBusinessRegNo(entity.getBusinessRegNo());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setCustomerCount(entity.getCustomerCount());
        dto.setMonth(entity.getMonth());
        dto.setLocation(entity.getLocation());
        dto.setPhone(entity.getPhone());
        dto.setWebsite(entity.getWebsite());
        dto.setAddress(entity.getAddress());
        return dto;
    }
}
