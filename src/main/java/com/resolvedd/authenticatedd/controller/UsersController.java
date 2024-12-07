package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationRole;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserApplicationRoleService;
import com.resolvedd.authenticatedd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
@PreAuthorize("hasRole('admin')")
public class UsersController {

    private final UserService userService;
    private final UserApplicationRoleService userApplicationRoleService;
    private final ApplicationService applicationService;
    private final RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestParam String username,
                                        @RequestParam String roleName,
                                        @RequestParam String appName) {

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User [" + username + "] not found");
        }
        User user = userOpt.get();

        Optional<Role> roleOpt = roleService.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Role [" + roleName + "] not found\nAvailable roles: " +
                    roleService.getAllRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
        }
        Role role = roleOpt.get();

        Optional<Application> applicationOpt = applicationService.findByName(appName);
        if (applicationOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Application [" + appName + "] not found\nAvailable applications: " +
                    applicationService.getAllApplications().stream().map(Application::getName).collect(Collectors.joining(", ")));
        }
        Application application = applicationOpt.get();

        Optional<UserApplicationRole> userApplicationRoleOpt = userApplicationRoleService.findByUserAndApplication(user, application);
        if (userApplicationRoleOpt.isPresent()) {
            UserApplicationRole userApplicationRole = userApplicationRoleOpt.get();
            userApplicationRole.setRole(role);
            userApplicationRoleService.save(userApplicationRole);
            return ResponseEntity.ok("Role [" + roleName + "] updated for user [" + username + "] in application [" + appName + "]");
        } else {
            UserApplicationRole newUserApplicationRole = new UserApplicationRole(user, application, role);
            userApplicationRoleService.save(newUserApplicationRole);
            return ResponseEntity.ok("Role [" + roleName + "] assigned to user [" + username + "] in application [" + appName + "]");
        }
    }
}
