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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final ApplicationService applicationService;
    private final UserRoleService userRoleService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Authenticate user and return token
    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> user = userService.findByUsername(request.getUsername());
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Extract user's roles and permissions
        List<UserRole> roles = userRoleService.findByUserAndApplication(user.get(), applicationService.findByName(request.getAppName()).orElseThrow());
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(UserRole::getRole).toList());
        claims.put("permissions", roles.stream()
                .flatMap(role -> permissionService.getAllPermissions().stream())
                .toList());

        // Generate JWT token
        String token = jwtUtil.generateToken(claims, user.get().getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // Register user for a specific application
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

        userRoleService.assignRoleToUser(user, defaultRole, application);

        return ResponseEntity.ok("User registered successfully for application: " + appName);
    }

    // Get all applications
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
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

        userRoleService.assignRoleToUser(user, role, application);

        return ResponseEntity.ok("Role assigned successfully");
    }
}
