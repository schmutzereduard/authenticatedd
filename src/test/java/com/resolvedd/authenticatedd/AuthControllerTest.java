package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserApplicationRoleService;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks private AuthController authController;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserService userService;
    @Mock private UserApplicationRoleService userApplicationRoleService;
    @Mock private ApplicationService applicationService;
    @Mock private RoleService roleService;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void testAuthenticateUserSuccess() {

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("hashedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("securepassword");
        loginRequest.setApplication("someapp");

        String mockToken = "thisIsMyToken";

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(applicationService.findByName(loginRequest.getApplication())).thenReturn(Optional.of(new Application()));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn(mockToken);

        ResponseEntity<?> response = authController.authenticate(loginRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(mockToken, ((LoginResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void testAuthenticateUserFail() {

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(applicationService.findByName(any())).thenReturn(Optional.of(new Application()));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        ResponseEntity<?> response = authController.authenticate(new LoginRequest());

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testAuthenticateUserNotFound() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticate(loginRequest);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("User [" + loginRequest.getUsername() + "] not found", Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void testAuthenticateApplicationNotFound() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setApplication("someapp");

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(new User()));
        when(applicationService.findByName(loginRequest.getApplication())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticate(loginRequest);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Application [" + loginRequest.getApplication() + "] not found",Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void testRegisterUserSuccess() {

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("securepassword");

        when(roleService.findByName(any())).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        doNothing().when(userApplicationRoleService).saveAll(anyList());

        ResponseEntity<?> response = authController.register(user);

        assertEquals(OK, response.getStatusCode());
        assertEquals("User "+ user.getUsername() + " registered as Guest for apps: ", Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void testRegisterUserAlreadyExists() {

        User user = new User();
        user.setUsername("john_doe");

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }
}
