package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.ProfilesController;
import com.resolvedd.authenticatedd.jwt.JwtUtil;
import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.UserApplicationProfileService;
import com.resolvedd.authenticatedd.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class ProfilesControllerTest {

    @InjectMocks private ProfilesController profilesController;
    @Mock private UserService userService;
    @Mock private UserApplicationProfileService userApplicationProfileService;
    @Mock private ApplicationService applicationService;
    @Mock private JwtUtil jwtUtil;

    @Test
    void getUserProfileSuccess() {

        UserApplicationProfile mockProfile = new UserApplicationProfile();
        mockProfile.setId(1L);

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(applicationService.findByName(any())).thenReturn(Optional.of(new Application()));
        when(userApplicationProfileService.findByUserAndApplication(any(), any())).thenReturn(Optional.of(mockProfile));

        ResponseEntity<?> userProfile = profilesController.getUserProfile("", "");

        assertEquals(OK, userProfile.getStatusCode());
        assertEquals(mockProfile, userProfile.getBody());
    }

    @Test
    void getUserProfileUserNotFound() {

        String username = "someuser";

        when(jwtUtil.extractUsername(any())).thenReturn(username);

        ResponseEntity<?> response = profilesController.getUserProfile("","");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("User [" + username + "] not found", response.getBody());
    }

    @Test
    void getUserProfileApllicationNotFound() {

        String applicationName = "someapp";

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = profilesController.getUserProfile(applicationName, "");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Application [" + applicationName + "] not found", response.getBody());
    }

    @Test
    void getUserProfileProfileNotFound() {

        String username = "someuser";
        String applicationName = "someapp";

        when(jwtUtil.extractUsername(any())).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(applicationService.findByName(applicationName)).thenReturn(Optional.of(new Application()));

        ResponseEntity<?> response = profilesController.getUserProfile(applicationName, "");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Profile not found for User [" + username + "] and Application [" + applicationName + "]", response.getBody());
    }
}
