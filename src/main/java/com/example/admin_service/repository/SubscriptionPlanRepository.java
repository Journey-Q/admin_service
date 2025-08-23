// Repository
package com.example.admin_service.repository;

import com.example.admin_service.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<SubscriptionPlan> findByIsActiveTrue();

    List<SubscriptionPlan> findByIsActiveFalse();

    List<SubscriptionPlan> findByType(String type);

    List<SubscriptionPlan> findByInterval(String interval);

    List<SubscriptionPlan> findByHasDiscountTrue();
}
