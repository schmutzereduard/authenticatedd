package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.UsersController;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationRole;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserApplicationRoleService;
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
public class UsersControllerTest {

    @InjectMocks private UsersController usersController;
    @Mock private UserService userService;
    @Mock private UserApplicationRoleService userApplicationRoleService;
    @Mock private ApplicationService applicationService;
    @Mock private RoleService roleService;

    @Test
    void testAssignRoleSuccess() {

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(new User()));
        when(roleService.findByName("admin")).thenReturn(Optional.of(new Role()));
        when(userApplicationRoleService.findByUserAndApplication(any(), any())).thenReturn(Optional.of(new UserApplicationRole()));
        when(applicationService.findByName("myApp")).thenReturn(Optional.of(new Application()));

        ResponseEntity<?> response = usersController.assignRole("john_doe", "admin", "myApp");

        assertEquals(OK, response.getStatusCode());
        assertEquals("Role [admin] updated for user [john_doe] in application [myApp]", response.getBody());
    }

    @Test
    void testAssignRoleUserNotFound() {

        when(userService.findByUsername("john_doe")).thenReturn(Optional.empty());

        ResponseEntity<?> response = usersController.assignRole("john_doe", "admin", "myApp");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("User [john_doe] not found", response.getBody());
    }

    @Test
    void testAssignRoleRoleNotFound() {

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(new User()));
        when(roleService.findByName("manager")).thenReturn(Optional.empty());

        ResponseEntity<?> response = usersController.assignRole("john_doe", "manager", "myApp");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Role [manager] not found"));
    }

    @Test
    void testAssignRoleApplicationNotFound() {

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(new User()));
        when(roleService.findByName("admin")).thenReturn(Optional.of(new Role()));
        when(applicationService.findByName("myApp")).thenReturn(Optional.empty());

        ResponseEntity<?> response = usersController.assignRole("john_doe", "admin", "myApp");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().startsWith("Application [myApp] not found"));
    }
}
