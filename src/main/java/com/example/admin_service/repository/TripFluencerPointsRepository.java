package com.example.admin_service.repository;

import com.example.admin_service.entity.TripFluencerPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripFluencerPointsRepository extends JpaRepository<TripFluencerPoints, Long> {

    Optional<TripFluencerPoints> findByUserId(Long userId);

    List<TripFluencerPoints> findAllByIsActiveTrue();

    List<TripFluencerPoints> findAllByTier(String tier);

    @Query("SELECT t FROM TripFluencerPoints t WHERE t.isActive = true ORDER BY t.totalPointsEarned DESC")
    List<TripFluencerPoints> findTopEarners();

    @Query("SELECT t FROM TripFluencerPoints t WHERE t.isActive = true ORDER BY t.currentPoints DESC")
    List<TripFluencerPoints> findByCurrentPointsDesc();

    @Query("SELECT COUNT(t) FROM TripFluencerPoints t WHERE t.isActive = true")
    Long countActiveTripFluencers();

    @Query("SELECT SUM(t.totalPointsEarned) FROM TripFluencerPoints t WHERE t.isActive = true")
    Long sumTotalPointsEarned();
}