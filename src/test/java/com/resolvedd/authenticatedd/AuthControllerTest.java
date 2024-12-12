package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.dto.RegisterRequest;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Plan;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.PlanService;
import com.resolvedd.authenticatedd.service.UserApplicationProfileService;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
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
    @Mock private UserApplicationProfileService userApplicationProfileService;
    @Mock private ApplicationService applicationService;
    @Mock private PlanService planService;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void testAuthenticateUserSuccess() {

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("hashedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("securepassword");

        String mockToken = "thisIsMyToken";

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyMap())).thenReturn(mockToken);

        ResponseEntity<?> response = authController.authenticate(loginRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(mockToken, ((LoginResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void testAuthenticateUserFail() {

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));
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
    void testRegisterUserSuccess() {

        Map<String, String> applicationPlans = new HashMap<>();
        applicationPlans.put("macrocalculator", "standard");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setPassword("securepassword");
        registerRequest.setEmail("john_doe@example.com");
        registerRequest.setApplications(applicationPlans);

        when(applicationService.findByName(any())).thenReturn(Optional.of(new Application()));
        when(planService.findByName(any())).thenReturn(Optional.of(new Plan()));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        doNothing().when(userApplicationProfileService).saveAll(anyList());

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals("User [john_doe] registered with selected plans: {macrocalculator=standard}", Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void testRegisterUserAlreadyExists() {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setPassword("securepassword");
        registerRequest.setEmail("john_doe@example.com");
        registerRequest.setApplications(new HashMap<>());

        when(userService.findByUsername("john_doe")).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }
}
