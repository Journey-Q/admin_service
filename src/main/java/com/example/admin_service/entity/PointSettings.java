package com.example.admin_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to store point tier settings for TripFluencer rewards system
 */
@Entity
@Table(name = "point_settings")
@Getter
@Setter
public class PointSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tier_name", nullable = false, unique = true)
    private String tierName; // tier1, tier2, tier3, tier4, tier5

    @Column(name = "min_likes", nullable = false)
    private Integer minLikes;

    @Column(name = "max_likes", nullable = false)
    private Integer maxLikes;

    @Column(name = "points_per_milestone", nullable = false)
    private Integer pointsPerMilestone;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public PointSettings() {
        this.isActive = true;
    }

    public PointSettings(String tierName, Integer minLikes, Integer maxLikes, Integer pointsPerMilestone) {
        this();
        this.tierName = tierName;
        this.minLikes = minLikes;
        this.maxLikes = maxLikes;
        this.pointsPerMilestone = pointsPerMilestone;
    }

    @Override
    public String toString() {
        return "PointSettings{" +
                "id=" + id +
                ", tierName='" + tierName + '\'' +
                ", minLikes=" + minLikes +
                ", maxLikes=" + maxLikes +
                ", pointsPerMilestone=" + pointsPerMilestone +
                ", isActive=" + isActive +
                '}';
    }
}