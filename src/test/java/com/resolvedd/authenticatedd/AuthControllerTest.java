package com.resolvedd.authenticatedd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.Credentials;
import com.resolvedd.authenticatedd.dto.TokenDTO;
import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.exception.GlobalExceptionHandler;
import com.resolvedd.authenticatedd.service.TokenService;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.utils.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.resolvedd.authenticatedd.constants.ExceptionConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @InjectMocks private AuthController authController;
    @Mock private TokenService tokenService;
    @Mock private UserService userService;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    private static final String MOCK_USERNAME_VALUE = "example@mock";
    private static final String MOCK_PASSWORD_VALUE = "Password@123";
    private static final String MOCK_TOKEN_VALUE = "mockToken";
    private final TokenDTO mockToken = new TokenDTO();
    private final Credentials mockCredentials = new Credentials();
    private final UserDTO mockUser = new UserDTO();
    {
        mockToken.setUsername(MOCK_USERNAME_VALUE);
        mockToken.setToken(MOCK_TOKEN_VALUE);

        mockCredentials.setUsername(MOCK_USERNAME_VALUE);
        mockCredentials.setPassword(MOCK_PASSWORD_VALUE);

        mockUser.setUsername(MOCK_USERNAME_VALUE);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void isAuthorizedSuccess() {

        try (MockedStatic<TokenUtils> mockedStatic = mockStatic(TokenUtils.class)) {

            mockedStatic.when(() -> TokenUtils.getToken(MOCK_TOKEN_VALUE)).thenReturn(MOCK_TOKEN_VALUE);
            mockedStatic.when(() -> TokenUtils.isTokenValid(anyLong())).thenReturn(true);

            when(tokenService.getByToken(mockToken.getToken())).thenReturn(mockToken);

            ResponseEntity<?> response = authController.isAuthorized(mockToken.getToken());

            assertEquals(OK, response.getStatusCode());
        }
    }

    @Test
    void isAuthorizedFail() throws Exception {

        try (MockedStatic<TokenUtils> mockedStatic = mockStatic(TokenUtils.class)) {

            mockedStatic.when(() -> TokenUtils.getToken(MOCK_TOKEN_VALUE)).thenReturn(MOCK_TOKEN_VALUE);
            mockedStatic.when(() -> TokenUtils.isTokenValid(anyLong())).thenReturn(false);

            when(tokenService.getByToken(mockToken.getToken())).thenReturn(mockToken);

            mockMvc.perform(get("/api/auth/isAuthorized")
                            .header(AUTHORIZATION, MOCK_TOKEN_VALUE))
//                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.body.detail").value(EXPIRED_TOKEN_MESSAGE));
        }
    }

    @Test
    void authenticateSuccess() {

        try (MockedStatic<TokenUtils> mockedStatic = mockStatic(TokenUtils.class)) {

            when(userService.isUserValid(mockCredentials)).thenReturn(true);
            when(tokenService.getByUsername(mockCredentials.getUsername())).thenReturn(mockToken);
            mockedStatic.when(() -> TokenUtils.isTokenValid(anyLong())).thenReturn(true);

            ResponseEntity<?> response = authController.authenticate(mockCredentials);

            assertEquals(OK, response.getStatusCode());
        }
    }

    @Test
    void authenticateFail() throws Exception {

        when(userService.isUserValid(any(Credentials.class))).thenReturn(false);

        mockMvc.perform(post("/api/auth/authenticate")
                        .header(AUTHORIZATION, MOCK_TOKEN_VALUE)
                        .content(new ObjectMapper().writeValueAsString(mockCredentials))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.body.detail").value(INVALID_CREDENTIALS_MESSAGE));
    }

    @Test
    void authenticateTokenExpired() throws Exception {

        try (MockedStatic<TokenUtils> mockedStatic = mockStatic(TokenUtils.class)) {

            mockedStatic.when(() -> TokenUtils.isTokenValid(anyLong())).thenReturn(false);

            when(userService.isUserValid(any(Credentials.class))).thenReturn(true);
            when(tokenService.getByUsername(mockCredentials.getUsername())).thenReturn(mockToken);

            mockMvc.perform(post("/api/auth/authenticate")
                            .header(AUTHORIZATION, MOCK_TOKEN_VALUE)
                            .content(new ObjectMapper().writeValueAsString(mockCredentials))
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.body.detail").value(EXPIRED_TOKEN_MESSAGE));
        }
    }

    @Test
    void registerSuccess() {

        when(userService.findByUsername(MOCK_USERNAME_VALUE)).thenReturn(null);
        when(userService.saveUser(mockCredentials.getUsername(), mockCredentials.getPassword())).thenReturn(mockUser);

        ResponseEntity<?> response = authController.register(mockCredentials);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void registerUserAlreadyExists() throws Exception {

        when(userService.findByUsername(MOCK_USERNAME_VALUE)).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .header(AUTHORIZATION, MOCK_TOKEN_VALUE)
                        .content(new ObjectMapper().writeValueAsString(mockCredentials))
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.body.detail").value(USER_ALREADY_EXISTS_MESSAGE));
    }
}
