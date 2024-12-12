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
    void getUserProfileMissingUser() {

        when(jwtUtil.extractUsername(any())).thenReturn("someuser");

        ResponseEntity<?> response = profilesController.getUserProfile("","");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("User [someuser] not found", response.getBody());
    }

    @Test
    void getUserMissingApp() {

        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = profilesController.getUserProfile("someapp", "");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Application [someapp] not found", response.getBody());
    }

    @Test
    void getUserMissingProfile() {

        when(jwtUtil.extractUsername(any())).thenReturn("someuser");
        when(userService.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(applicationService.findByName(any())).thenReturn(Optional.of(new Application()));

        ResponseEntity<?> response = profilesController.getUserProfile("someapp", "");

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Profile not found for User [someuser] and Application [someapp]", response.getBody());
    }
}
