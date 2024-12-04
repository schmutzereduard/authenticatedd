package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.*;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Application application = applicationService.findByName(request.getApplication())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("application", application.getName());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, @RequestParam String appName) {

        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        // Assign default role for the application
        Application application = applicationService.findByName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        Role defaultRole = roleService.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));

        return ResponseEntity.ok("User registered successfully for application: " + appName);
    }

    // Assign a role to a user for a specific application
    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestParam String username, @RequestParam String roleName, @RequestParam String appName) {

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleService.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Application application = applicationService.findByName(appName)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));


        return ResponseEntity.ok("Role assigned successfully");
    }
}
