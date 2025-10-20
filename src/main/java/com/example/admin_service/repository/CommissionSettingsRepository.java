package com.example.admin_service.repository;

import com.example.admin_service.entity.Commission;
import com.example.admin_service.entity.CommissionSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionSettingsRepository extends JpaRepository<CommissionSettings, Long> {

    Optional<CommissionSettings> findByProviderType(Commission.ProviderType providerType);

    Optional<CommissionSettings> findByProviderTypeAndIsActiveTrue(Commission.ProviderType providerType);
}
