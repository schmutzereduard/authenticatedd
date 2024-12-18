package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.PlansController;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Plan;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.PlanService;
import com.resolvedd.authenticatedd.service.UserApplicationProfileService;
import com.resolvedd.authenticatedd.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
public class PlansControllerTest {

    @InjectMocks private PlansController plansController;
    @Mock private UserService userService;
    @Mock private UserApplicationProfileService userApplicationProfileService;
    @Mock private ApplicationService applicationService;
    @Mock private PlanService planService;

    @Test
    void upgradePlanSuccess() {

        String username = "john_doe";
        String planName = "premium";
        String applicationName = "macrocalculator";

        when(userService.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(planService.findByName(planName)).thenReturn(Optional.of(new Plan()));
        when(userApplicationProfileService.findByUserAndApplication(any(), any())).thenReturn(Optional.of(new UserApplicationProfile()));
        when(applicationService.findByName(applicationName)).thenReturn(Optional.of(new Application()));

        ResponseEntity<?> response = plansController.upgradePlan(username, planName, applicationName);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Plan [" + planName + "] updated for user [" + username + "] in application [" + applicationName + "]", response.getBody());
    }

    @Test
    void upgradePlanUserNotFound() {

        String username = "john_doe";
        String planName = "premium";
        String applicationName = "macrocalcualtor";

        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(username, planName, applicationName);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("User [" + username + "] not found", response.getBody());
    }

    @Test
    void upgradePlanPlanNotFound() {

        String username = "john_doe";
        String planName = "super_premium";
        String applicationName = "macrocalculator";

        when(userService.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(planService.findByName(planName)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(username, planName, applicationName);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Plan [" + planName + "] not found"));
    }

    @Test
    void upgradePlanApplicationNotFound() {

        String username = "john_doe";
        String planName = "premium";
        String applicationName = "macro";

        when(userService.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(planService.findByName(planName)).thenReturn(Optional.of(new Plan()));
        when(applicationService.findByName(applicationName)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(username, planName, applicationName);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Application [" + applicationName + "] not found"));
    }
}
