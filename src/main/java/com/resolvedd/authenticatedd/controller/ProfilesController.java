package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.jwt.JwtUtil;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.UserApplicationProfileService;
import com.resolvedd.authenticatedd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfilesController {

    private final UserService userService;
    private final UserApplicationProfileService userApplicationProfileService;
    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{applicationName}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable String applicationName,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        Optional<User> opUser = userService.findByUsername(username);
        if(opUser.isEmpty()) {
            return ResponseEntity.status(404).body("User [" + username + "] not found");
        }

        Optional<Application> optApp = applicationService.findByName(applicationName);
        if (optApp.isEmpty()) {
            return ResponseEntity.status(404).body("Application [" + applicationName + "] not found");
        }

        User user = opUser.get();
        Application application = optApp.get();

        Optional<UserApplicationProfile> optProfile = userApplicationProfileService.findByUserAndApplication(user, application);
        if (optProfile.isEmpty()) {
            return ResponseEntity.status(404).body("Profile not found for User [" + username + "] and Application [" + applicationName + "]");
        } else {
            return ResponseEntity.ok(optProfile.get());
        }
    }
}
