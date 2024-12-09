package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Plan;
import com.resolvedd.authenticatedd.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public Optional<Plan> findByName(String name) {
        return planRepository.findByName(name);
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public void save(Plan plan) {
        planRepository.save(plan);
    }
}
