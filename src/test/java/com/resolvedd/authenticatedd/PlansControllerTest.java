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
    void testUpgradePlanSuccess() {

        String user = "john_doe";
        String plan = "premium";
        String app = "macrocalculator";

        when(userService.findByUsername(user)).thenReturn(Optional.of(new User()));
        when(planService.findByName(plan)).thenReturn(Optional.of(new Plan()));
        when(userApplicationProfileService.findByUserAndApplication(any(), any())).thenReturn(Optional.of(new UserApplicationProfile()));
        when(applicationService.findByName(app)).thenReturn(Optional.of(new Application()));

        ResponseEntity<?> response = plansController.upgradePlan(user, plan, app);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Plan [premium] updated for user [john_doe] in application [macrocalculator]", response.getBody());
    }

    @Test
    void testUpgradePlanUserNotFound() {

        String user = "john_doe";
        String plan = "premium";
        String app = "macrocalcualtor";

        when(userService.findByUsername(user)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(user, plan, app);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("User [john_doe] not found", response.getBody());
    }

    @Test
    void testUpgradePlanPlanNotFound() {

        String user = "john_doe";
        String plan = "super_premium";
        String macrocalculator = "myApp";

        when(userService.findByUsername(user)).thenReturn(Optional.of(new User()));
        when(planService.findByName(plan)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(user, plan, macrocalculator);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Plan [super_premium] not found"));
    }

    @Test
    void testUpgradePlanApplicationNotFound() {

        String user = "john_doe";
        String plan = "premium";
        String app = "macro";

        when(userService.findByUsername(user)).thenReturn(Optional.of(new User()));
        when(planService.findByName(plan)).thenReturn(Optional.of(new Plan()));
        when(applicationService.findByName(app)).thenReturn(Optional.empty());

        ResponseEntity<?> response = plansController.upgradePlan(user, plan, app);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Application [macro] not found"));
    }
}
