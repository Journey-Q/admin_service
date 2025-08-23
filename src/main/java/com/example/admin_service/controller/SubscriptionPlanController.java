// Controller
package com.example.admin_service.controller;

import com.example.admin_service.dto.CreateSubscriptionPlanDTO;
import com.example.admin_service.entity.SubscriptionPlan;
import com.example.admin_service.services.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("service/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(@RequestBody CreateSubscriptionPlanDTO dto) {
        SubscriptionPlan createdPlan = subscriptionPlanService.createSubscriptionPlan(dto);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getSubscriptionPlanById(@PathVariable Long id) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanService.getSubscriptionPlanById(id);
        return ResponseEntity.ok(subscriptionPlan);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlan>> getActiveSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanService.getActiveSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SubscriptionPlan>> getSubscriptionPlansByType(@PathVariable String type) {
        List<SubscriptionPlan> plans = subscriptionPlanService.getSubscriptionPlansByType(type);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/interval/{interval}")
    public ResponseEntity<List<SubscriptionPlan>> getSubscriptionPlansByInterval(@PathVariable String interval) {
        List<SubscriptionPlan> plans = subscriptionPlanService.getSubscriptionPlansByInterval(interval);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/discounted")
    public ResponseEntity<List<SubscriptionPlan>> getDiscountedPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanService.getDiscountedPlans();
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(
            @PathVariable Long id,
            @RequestBody CreateSubscriptionPlanDTO dto) {
        SubscriptionPlan updatedPlan = subscriptionPlanService.updateSubscriptionPlan(id, dto);
        return ResponseEntity.ok(updatedPlan);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<SubscriptionPlan> togglePlanStatus(@PathVariable Long id) {
        SubscriptionPlan updatedPlan = subscriptionPlanService.togglePlanStatus(id);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscriptionPlan(@PathVariable Long id) {
        boolean response = subscriptionPlanService.deleteSubscriptionPlan(id);
        if (response) {
            return ResponseEntity.ok("deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}
