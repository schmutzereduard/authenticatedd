package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Plan;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.PlanService;
import com.resolvedd.authenticatedd.service.UserApplicationProfileService;
import com.resolvedd.authenticatedd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlansController {

    private final UserService userService;
    private final UserApplicationProfileService userApplicationProfileService;
    private final ApplicationService applicationService;
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PostMapping("/upgrade-plan")
    public ResponseEntity<?> upgradePlan(@RequestParam String username,
                                         @RequestParam String planName,
                                         @RequestParam String appName) {

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User [" + username + "] not found");
        }
        User user = userOpt.get();

        Optional<Plan> planOpt = planService.findByName(planName);
        if (planOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Plan [" + planName + "] not found\nAvailable plans: [" +
                    planService.getAllPlans().stream().map(Plan::getName).collect(Collectors.joining(", ")) + "]");
        }
        Plan plan = planOpt.get();

        Optional<Application> applicationOpt = applicationService.findByName(appName);
        if (applicationOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Application [" + appName + "] not found\nAvailable applications: [" +
                    applicationService.getAllApplications().stream().map(Application::getName).collect(Collectors.joining(", ")) + "]");
        }
        Application application = applicationOpt.get();

        Optional<UserApplicationProfile> userApplicationProfileOpt = userApplicationProfileService.findByUserAndApplication(user, application);
        if (userApplicationProfileOpt.isPresent()) {
            UserApplicationProfile userApplicationProfile = userApplicationProfileOpt.get();
            userApplicationProfile.setPlan(plan);
            userApplicationProfileService.save(userApplicationProfile);
            return ResponseEntity.ok("Plan [" + planName + "] updated for user [" + username + "] in application [" + appName + "]");
        } else {
            UserApplicationProfile newUserApplicationProfile = new UserApplicationProfile(user, application, plan);
            userApplicationProfileService.save(newUserApplicationProfile);
            return ResponseEntity.ok("Plan [" + planName + "] assigned to user [" + username + "] in application [" + appName + "]");
        }
    }
}
