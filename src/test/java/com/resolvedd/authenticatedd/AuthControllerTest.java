package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.AuthenticationResponse;
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
import static org.springframework.http.HttpStatus.*;

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
    void authenticateSuccess() {

        String username = "user";
        String token = "token";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        ResponseEntity<?> response = authController.authenticate(token);

        assertEquals(OK, response.getStatusCode());
        assertEquals(username, ((AuthenticationResponse) Objects.requireNonNull(response.getBody())).getUsername());
    }

    @Test
    void authenticateFail() {

        String username = "user";
        String token = "token";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);

        ResponseEntity<?> response = authController.authenticate(token);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token !", response.getBody());

    }

    @Test
    void loginSuccess() {

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

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(mockToken, ((LoginResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void loginFail() {

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        ResponseEntity<?> response = authController.login(new LoginRequest());

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void loginUserNotFound() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");

        when(userService.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("User [" + loginRequest.getUsername() + "] not found", Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void registerSuccess() {

        RegisterRequest registerRequest = getRegisterRequest();

        when(applicationService.findByName(any())).thenReturn(Optional.of(new Application()));
        when(planService.findByName(any())).thenReturn(Optional.of(new Plan()));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        doNothing().when(userApplicationProfileService).saveAll(anyList());

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals("User [" + registerRequest.getUsername() + "] registered with selected plans: " + registerRequest.getApplications(), Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    void registerUserAlreadyExists() {

        RegisterRequest registerRequest = getRegisterRequest();

        when(userService.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void registerApplicationNotFound() {

        RegisterRequest registerRequest = getRegisterRequest();

        when(applicationService.findByName(any())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Application [macrocalculator] not found", Objects.requireNonNull(response.getBody()).toString());
    }

    private static RegisterRequest getRegisterRequest() {

        String applicationName = "macrocalculator";
        String planName = "standard";
        String username = "john_doe";

        Map<String, String> applicationPlans = new HashMap<>();
        applicationPlans.put(applicationName, planName);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("securepassword");
        registerRequest.setEmail("john_doe@example.com");
        registerRequest.setApplications(applicationPlans);
        return registerRequest;
    }
}
