package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccessController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final RoleService roleService;

    @GetMapping("/access")
    public ResponseEntity<?> hasAccess(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String action
    ) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String username = jwtUtil.extractUsername(token);
        String applicationName = jwtUtil.extract(token, "application");

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Application application = applicationService.findByName(applicationName)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        boolean hasAccess = user.getRoles()
                .stream()
                .filter(userRole -> userRole.getApplication().equals(application)) // Filter by application
                .map(UserRole::getRole) // Get roles for the user
                .flatMap(role -> role.getPermissions().stream()) // Get permissions for each role
                .filter(rolePermission -> rolePermission.getApplication().equals(application)) // Ensure the permission is for the same application
                .map(RolePermission::getPermission) // Map to the actual permission object
                .anyMatch(permission -> permission.getName().equals(action)); // Check if the permission matches the action

        if (!hasAccess) {
            return ResponseEntity.status(403).body("Access denied for action: " + action);
        }

        return ResponseEntity.ok("Access granted for action: " + action);
    }

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
