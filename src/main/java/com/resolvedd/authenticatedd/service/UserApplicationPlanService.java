package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationPlan;
import com.resolvedd.authenticatedd.repository.UserApplicationPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationPlanService {

    private final UserApplicationPlanRepository userApplicationPlanRepository;

    public Optional<UserApplicationPlan> findByUserAndApplication(User user, Application application) {
        return userApplicationPlanRepository.findByUserAndApplication(user, application);
    }

    public void save(UserApplicationPlan userApplicationPlan) {
        userApplicationPlanRepository.save(userApplicationPlan);
    }

    public void saveAll(List<UserApplicationPlan> userApplicationPlans) {
        userApplicationPlanRepository.saveAll(userApplicationPlans);
    }
}
