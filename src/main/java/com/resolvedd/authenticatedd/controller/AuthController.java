package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.dto.RegisterRequest;
import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.*;
import com.resolvedd.authenticatedd.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PlanService planService;
    private final ApplicationService applicationService;
    private final UserApplicationProfileService userApplicationProfileService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestParam String username,
            @RequestHeader("Authorization") String authHeader
    ) {

        String token = authHeader.replace("Bearer ", "");

        if (jwtUtil.validateToken(token, username)) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.status(401).body("Invalid token !");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        Optional<User> optUser = userService.findByUsername(request.getUsername());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(401).body("User [" + request.getUsername() + "] not found");
        }

        User user = optUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), new HashMap<>());

        return ResponseEntity.ok(new LoginResponse(user.getUsername(), token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        Optional<User> existingUser = userService.findByUsername(registerRequest.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        userService.saveUser(user);

        List<UserApplicationProfile> userApplicationProfiles = new ArrayList<>();

        for (Map.Entry<String, String> applicationEntry : registerRequest.getApplications().entrySet()) {

            String applicationName = applicationEntry.getKey();
            Optional<Application> optApplication = applicationService.findByName(applicationName);
            if (optApplication.isEmpty()) {
                return ResponseEntity.status(404).body("Application [" + applicationName + "] not found");
            }

            String applicationPlanName = applicationEntry.getValue();
            Optional<Plan> optPlan = planService.findByName(applicationPlanName);
            if (optPlan.isEmpty()) {
                return ResponseEntity.status(404).body("Plan [" + applicationPlanName + "] not found");
            }

            userApplicationProfiles.add(new UserApplicationProfile(user, optApplication.get(), optPlan.get()));
        }

        user.setApplicationProfiles(userApplicationProfiles);
        userApplicationProfileService.saveAll(userApplicationProfiles);

        return ResponseEntity.ok(
                "User [" + user.getUsername()
                        + "] registered with selected plans: " + registerRequest.getApplications());
    }
}
