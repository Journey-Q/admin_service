package com.example.admin_service.repository;

import com.example.admin_service.entity.PointSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointSettingsRepository extends JpaRepository<PointSettings, Long> {

    Optional<PointSettings> findByTierName(String tierName);

    List<PointSettings> findAllByIsActiveTrue();

    List<PointSettings> findAllByOrderByMinLikesAsc();
}