package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserRoleService;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks private AuthController authController;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserService userService;
    @Mock private UserRoleService userRoleService;
    @Mock private ApplicationService applicationService;
    @Mock private RoleService roleService;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void testAuthenticateUserSuccess() {

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("securepassword");
        user.setEmail("user@example.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("securepassword");
        loginRequest.setApplication("someapp");

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(applicationService.findByName(loginRequest.getApplication())).thenReturn(Optional.of(new Application()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn("thisIsMyToken");

        ResponseEntity<?> response = authController.authenticate(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("thisIsMyToken", ((LoginResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("securepassword");
        user.setEmail("john.doe@example.com");

        when(userService.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(applicationService.getAllApplications()).thenReturn(new ArrayList<>());
        when(roleService.findByName("guest")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode("securepassword")).thenReturn("encodedPassword");
        doNothing().when(userRoleService).saveAll(anyList());

        // Act
        ResponseEntity<?> response = authController.register(user);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("registered as Guest"));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.register(user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }
}
