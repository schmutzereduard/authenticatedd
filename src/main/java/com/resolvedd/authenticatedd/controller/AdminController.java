package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserRole;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserRoleService;
import com.resolvedd.authenticatedd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final ApplicationService applicationService;
    private final RoleService roleService;

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestParam String username, @RequestParam String roleName, @RequestParam String appName) {

        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User [" + username + "] not found");
        }


        Optional<Role> role = roleService.findByName(roleName);
        if (role.isEmpty()) {
            return ResponseEntity.status(401).body("Role [" + roleName + "] not found\nAvailable roles: " + roleService.getAllRoles().stream().map(Role::getName).collect(Collectors.joining()));
        }

        Optional<Application> application = applicationService.findByName(appName);
        if (application.isEmpty()) {
            return ResponseEntity.status(401).body("Application [" + appName + "] not found\nAvailable applications: " + applicationService.getAllApplications().stream().map(Application::getName).collect(Collectors.joining()));
        }

        UserRole userRole = new UserRole();
        userRole.setRole(role.get());
        userRole.setUser(user.get());
        userRole.setApplication(application.get());
        user.get().addRole(userRole);
        userRoleService.save(userRole);

        return ResponseEntity.ok("Role assigned successfully");
    }
}
