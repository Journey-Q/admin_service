package com.example.admin_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to track points for individual TripFluencers
 */
@Entity
@Table(name = "tripfluencer_points")
@Getter
@Setter
public class TripFluencerPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; // Reference to user in user service

    @Column(name = "current_points", nullable = false)
    private Integer currentPoints = 0;

    @Column(name = "total_points_earned", nullable = false)
    private Integer totalPointsEarned = 0;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed = 0;

    @Column(name = "total_likes", nullable = false)
    private Integer totalLikes = 0;

    @Column(name = "followers", nullable = false)
    private Integer followers = 0;

    @Column(name = "tier")
    private String tier = "SILVER"; // SILVER, GOLD, PLATINUM

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public TripFluencerPoints() {
        this.currentPoints = 0;
        this.totalPointsEarned = 0;
        this.pointsUsed = 0;
        this.totalLikes = 0;
        this.followers = 0;
        this.tier = "SILVER";
        this.isActive = true;
    }

    public TripFluencerPoints(Long userId, Integer followers, Integer totalLikes) {
        this();
        this.userId = userId;
        this.followers = followers;
        this.totalLikes = totalLikes;
    }

    /**
     * Add points to the user's account
     */
    public void addPoints(Integer points) {
        if (points > 0) {
            this.currentPoints += points;
            this.totalPointsEarned += points;
        }
    }

    /**
     * Deduct points when redeemed
     */
    public boolean deductPoints(Integer points) {
        if (points > 0 && this.currentPoints >= points) {
            this.currentPoints -= points;
            this.pointsUsed += points;
            return true;
        }
        return false;
    }

    /**
     * Update tier based on followers
     */
    public void updateTier() {
        if (this.followers >= 25000) {
            this.tier = "PLATINUM";
        } else if (this.followers >= 15000) {
            this.tier = "GOLD";
        } else {
            this.tier = "SILVER";
        }
    }

    @Override
    public String toString() {
        return "TripFluencerPoints{" +
                "id=" + id +
                ", userId=" + userId +
                ", currentPoints=" + currentPoints +
                ", totalPointsEarned=" + totalPointsEarned +
                ", pointsUsed=" + pointsUsed +
                ", totalLikes=" + totalLikes +
                ", followers=" + followers +
                ", tier='" + tier + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}