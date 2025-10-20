package com.example.admin_service.repository;

import com.example.admin_service.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    Optional<Commission> findByTransactionId(String transactionId);

    List<Commission> findByProviderType(Commission.ProviderType providerType);

    List<Commission> findByServiceProvider(String serviceProvider);

    List<Commission> findByMonth(String month);

    List<Commission> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM Commission c WHERE " +
           "LOWER(c.serviceProvider) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.transactionId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.businessRegNo) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Commission> searchCommissions(@Param("search") String search);

    @Query("SELECT c FROM Commission c WHERE c.providerType = :type AND c.month = :month")
    List<Commission> findByProviderTypeAndMonth(
        @Param("type") Commission.ProviderType type,
        @Param("month") String month
    );

    @Query("SELECT DISTINCT c.serviceProvider FROM Commission c ORDER BY c.serviceProvider")
    List<String> findDistinctServiceProviders();

    @Query("SELECT DISTINCT c.month FROM Commission c ORDER BY c.month DESC")
    List<String> findDistinctMonths();
}
