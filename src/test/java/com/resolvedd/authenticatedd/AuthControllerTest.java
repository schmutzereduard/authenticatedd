package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.controller.AuthController;
import com.resolvedd.authenticatedd.dto.TokenDTO;
import com.resolvedd.authenticatedd.dto.UserDTO;
import com.resolvedd.authenticatedd.exception.GlobalExceptionHandler;
import com.resolvedd.authenticatedd.exception.UserAlreadyExistsException;
import com.resolvedd.authenticatedd.service.TokenService;
import com.resolvedd.authenticatedd.service.UserService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @InjectMocks private AuthController authController;
    @Mock private TokenService tokenService;
    @Mock private UserService userService;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    private static final Long MOCK_USER_ID_VALUE = 1L;
    private static final String MOCK_USERNAME_VALUE = "example@mock";
    private static final String MOCK_BASIC_AUTH_HEADER_VALUE = "mockBasicAuthHeader";
    private static final String MOCK_TOKEN_VALUE = "mockToken";
    private final TokenDTO mockToken = new TokenDTO();
    private final UserDTO mockUser = new UserDTO();
    {
        mockToken.setUsername(MOCK_USERNAME_VALUE);
        mockToken.setToken(MOCK_TOKEN_VALUE);
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

        when(tokenService.isTokenValid(mockToken.getToken())).thenReturn(MOCK_USER_ID_VALUE);

        ResponseEntity<?> response = authController.isAuthorized(mockToken.getToken());

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void isAuthorizedFail() throws Exception {

        when(tokenService.isTokenValid(mockToken.getToken())).thenThrow(new JwtException(EXPIRED_TOKEN_MESSAGE));

        mockMvc.perform(get("/api/auth/isAuthorized")
                            .header(AUTHORIZATION, MOCK_TOKEN_VALUE))
//                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(EXPIRED_TOKEN_MESSAGE));
    }

    @Test
    void authenticateSuccess() {

        when(userService.isUserValid(MOCK_BASIC_AUTH_HEADER_VALUE)).thenReturn(MOCK_USERNAME_VALUE);
        when(tokenService.getByUsername(MOCK_USERNAME_VALUE)).thenReturn(mockToken);
        when(tokenService.isTokenValid(mockToken.getToken())).thenReturn(MOCK_USER_ID_VALUE);

        ResponseEntity<?> response = authController.authenticate(MOCK_BASIC_AUTH_HEADER_VALUE);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void authenticateFail() throws Exception {

        when(userService.isUserValid(any())).thenReturn(null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .header(AUTHORIZATION, MOCK_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(INVALID_CREDENTIALS_MESSAGE));
    }

    @Test
    void authenticateTokenExpired() throws Exception {

        when(userService.isUserValid(any())).thenReturn(MOCK_USERNAME_VALUE);
        when(tokenService.getByUsername(MOCK_USERNAME_VALUE)).thenReturn(mockToken);
        when(tokenService.isTokenValid(mockToken.getToken())).thenThrow(new JwtException(EXPIRED_TOKEN_MESSAGE));

        mockMvc.perform(post("/api/auth/authenticate")
                            .header(AUTHORIZATION, MOCK_BASIC_AUTH_HEADER_VALUE)
                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(EXPIRED_TOKEN_MESSAGE));
    }

    @Test
    void registerSuccess() {

        when(userService.registerUser(MOCK_BASIC_AUTH_HEADER_VALUE)).thenReturn(mockUser);

        ResponseEntity<?> response = authController.register(MOCK_BASIC_AUTH_HEADER_VALUE);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void registerUserAlreadyExists() throws Exception {

        when(userService.registerUser(MOCK_BASIC_AUTH_HEADER_VALUE)).thenThrow(new UserAlreadyExistsException(USER_ALREADY_EXISTS_MESSAGE));

        mockMvc.perform(post("/api/auth/register")
                        .header(AUTHORIZATION, MOCK_BASIC_AUTH_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(USER_ALREADY_EXISTS_MESSAGE));
    }
}
