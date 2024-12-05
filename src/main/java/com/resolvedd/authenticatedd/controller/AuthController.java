package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.*;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final ApplicationService applicationService;
    private final UserRoleService userRoleService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {

        Optional<User> user = userService.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User [" + request.getUsername() + "] not found");
        }

        Optional<Application> application = applicationService.findByName(request.getApplication());
        if (application.isEmpty()) {
            return ResponseEntity.status(401).body("Application [" + request.getApplication() + "] not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("application", application.get().getName());
        String token = jwtUtil.generateToken(user.get().getUsername(), claims);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {

        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        Role defaultRole = roleService.findByName("guest").get();

        List<UserRole> userRoles = new ArrayList<>();

        List<Application> allApplications = applicationService.getAllApplications();
        for (Application application : allApplications) {
            UserRole userRole = new UserRole();
            userRole.setApplication(application);
            userRole.setRole(defaultRole);
            userRole.setUser(user);
            userRoles.add(userRole);
        }

        user.setRoles(userRoles);
        userRoleService.saveAll(userRoles);


        return ResponseEntity.ok(
                "User " + user.getUsername() + " registered as Guest for apps: " + allApplications.stream().map(Application::getName).collect(Collectors.joining())
        );
    }
}
