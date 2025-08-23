// Service
package com.example.admin_service.services;

import com.example.admin_service.dto.CreateSubscriptionPlanDTO;
import com.example.admin_service.entity.SubscriptionPlan;
import com.example.admin_service.exception.BadRequestException;
import com.example.admin_service.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionPlanService {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public SubscriptionPlan createSubscriptionPlan(CreateSubscriptionPlanDTO dto) {
        // Basic validation
        if (dto == null) {
            throw new BadRequestException("Subscription plan data cannot be null");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Plan name is required");
        }
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new BadRequestException("Plan type is required");
        }
        if (dto.getPrice() == null || dto.getPrice().trim().isEmpty()) {
            throw new BadRequestException("Plan price is required");
        }
        if (dto.getInterval() == null || dto.getInterval().trim().isEmpty()) {
            throw new BadRequestException("Plan interval is required");
        }

        // Validate interval values
        if (!isValidInterval(dto.getInterval())) {
            throw new BadRequestException("Invalid interval. Must be: monthly, yearly, weekly, or daily");
        }

        // Validate discount percentage if discount is enabled
        if (dto.getHasDiscount() != null && dto.getHasDiscount()) {
            if (dto.getDiscountPercentage() == null || dto.getDiscountPercentage().trim().isEmpty()) {
                throw new BadRequestException("Discount percentage is required when discount is enabled");
            }
            try {
                double discount = Double.parseDouble(dto.getDiscountPercentage());
                if (discount < 0 || discount > 100) {
                    throw new BadRequestException("Discount percentage must be between 0 and 100");
                }
            } catch (NumberFormatException e) {
                throw new BadRequestException("Invalid discount percentage format");
            }
        }

        // Check for duplicate plan name
        boolean planExists = subscriptionPlanRepository.existsByName(dto.getName());
        if (planExists) {
            throw new BadRequestException("Subscription plan with this name already exists");
        }

        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setName(dto.getName());
        subscriptionPlan.setType(dto.getType());
        subscriptionPlan.setPrice(dto.getPrice());
        subscriptionPlan.setInterval(dto.getInterval());
        subscriptionPlan.setDescription(dto.getDescription());
        subscriptionPlan.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        subscriptionPlan.setHasDiscount(dto.getHasDiscount() != null ? dto.getHasDiscount() : false);
        subscriptionPlan.setDiscountPercentage(dto.getDiscountPercentage());

        if (dto.getFeatures() != null && !dto.getFeatures().isEmpty()) {
            // Remove empty features
            List<String> validFeatures = dto.getFeatures().stream()
                    .filter(feature -> feature != null && !feature.trim().isEmpty())
                    .map(String::trim)
                    .toList();
            subscriptionPlan.setFeatures(validFeatures);
        }

        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    @Transactional(readOnly = true)
    public SubscriptionPlan getSubscriptionPlanById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid subscription plan ID");
        }
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Subscription plan not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getActiveSubscriptionPlans() {
        return subscriptionPlanRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getSubscriptionPlansByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new BadRequestException("Plan type cannot be empty");
        }
        return subscriptionPlanRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getSubscriptionPlansByInterval(String interval) {
        if (interval == null || interval.trim().isEmpty()) {
            throw new BadRequestException("Plan interval cannot be empty");
        }
        return subscriptionPlanRepository.findByInterval(interval);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getDiscountedPlans() {
        return subscriptionPlanRepository.findByHasDiscountTrue();
    }

    @Transactional
    public SubscriptionPlan updateSubscriptionPlan(Long id, CreateSubscriptionPlanDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid subscription plan ID");
        }
        if (dto == null) {
            throw new BadRequestException("Subscription plan data cannot be null");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Plan name is required");
        }
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new BadRequestException("Plan type is required");
        }
        if (dto.getPrice() == null || dto.getPrice().trim().isEmpty()) {
            throw new BadRequestException("Plan price is required");
        }
        if (dto.getInterval() == null || dto.getInterval().trim().isEmpty()) {
            throw new BadRequestException("Plan interval is required");
        }

        // Validate interval values
        if (!isValidInterval(dto.getInterval())) {
            throw new BadRequestException("Invalid interval. Must be: monthly, yearly, weekly, or daily");
        }

        // Validate discount percentage if discount is enabled
        if (dto.getHasDiscount() != null && dto.getHasDiscount()) {
            if (dto.getDiscountPercentage() == null || dto.getDiscountPercentage().trim().isEmpty()) {
                throw new BadRequestException("Discount percentage is required when discount is enabled");
            }
            try {
                double discount = Double.parseDouble(dto.getDiscountPercentage());
                if (discount < 0 || discount > 100) {
                    throw new BadRequestException("Discount percentage must be between 0 and 100");
                }
            } catch (NumberFormatException e) {
                throw new BadRequestException("Invalid discount percentage format");
            }
        }

        SubscriptionPlan existingPlan = getSubscriptionPlanById(id);

        // Check for duplicate plan name (excluding current plan)
        boolean planExists = subscriptionPlanRepository.existsByNameAndIdNot(dto.getName(), id);
        if (planExists) {
            throw new BadRequestException("Subscription plan with this name already exists");
        }

        existingPlan.setName(dto.getName());
        existingPlan.setType(dto.getType());
        existingPlan.setPrice(dto.getPrice());
        existingPlan.setInterval(dto.getInterval());
        existingPlan.setDescription(dto.getDescription());
        existingPlan.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        existingPlan.setHasDiscount(dto.getHasDiscount() != null ? dto.getHasDiscount() : false);
        existingPlan.setDiscountPercentage(dto.getDiscountPercentage());
        existingPlan.setFeatures(dto.getFeatures());

        return subscriptionPlanRepository.save(existingPlan);
    }

    @Transactional
    public SubscriptionPlan togglePlanStatus(Long id) {
        SubscriptionPlan plan = getSubscriptionPlanById(id);
        plan.setIsActive(!plan.getIsActive());
        return subscriptionPlanRepository.save(plan);
    }

    @Transactional
    public boolean deleteSubscriptionPlan(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid subscription plan ID");
        }
        SubscriptionPlan plan = getSubscriptionPlanById(id);
        subscriptionPlanRepository.delete(plan);
        return true;
    }

    private boolean isValidInterval(String interval) {
        return interval.equals("monthly") || interval.equals("yearly") ||
                interval.equals("weekly") || interval.equals("daily");
    }
}
