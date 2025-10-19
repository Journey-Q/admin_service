package com.example.admin_service.repository;

import com.example.admin_service.entity.PointRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRedemptionRepository extends JpaRepository<PointRedemption, Long> {

    List<PointRedemption> findByUserId(Long userId);

    List<PointRedemption> findByStatus(String status);

    List<PointRedemption> findAllByOrderByRedeemedAtDesc();

    @Query("SELECT r FROM PointRedemption r WHERE r.userId = ?1 ORDER BY r.redeemedAt DESC")
    List<PointRedemption> findByUserIdOrderByRedeemedAtDesc(Long userId);

    @Query("SELECT COUNT(r) FROM PointRedemption r WHERE r.status = 'ACTIVE'")
    Long countActiveRedemptions();

    @Query("SELECT SUM(r.pointsUsed) FROM PointRedemption r")
    Long sumPointsUsed();
}