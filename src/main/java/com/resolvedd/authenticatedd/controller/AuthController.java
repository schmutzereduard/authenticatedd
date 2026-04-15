package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.*;
import com.resolvedd.authenticatedd.exception.*;
import com.resolvedd.authenticatedd.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.resolvedd.authenticatedd.constants.Constants.CREATED;
import static com.resolvedd.authenticatedd.constants.Constants.SPACE;
import static com.resolvedd.authenticatedd.constants.ExceptionConstants.*;
import static com.resolvedd.authenticatedd.utils.StringUtils.buildString;
import static com.resolvedd.authenticatedd.utils.StringUtils.isNullOrEmpty;
import static com.resolvedd.authenticatedd.utils.TokenUtils.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/isAuthorized")
    public ResponseEntity<AuthenticationResponse> isAuthorized(@RequestHeader(AUTHORIZATION) String authHeader) {

        String token = getToken(authHeader);
        if (isNullOrEmpty(token))
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);

        TokenDTO tokenDTO = tokenService.getByToken(token);
        if (tokenDTO == null)
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);

        if (!isTokenValid(tokenDTO.getExpiresAt()))
            throw new ExpiredTokenException(EXPIRED_TOKEN_MESSAGE);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(tokenDTO.getUsername());
        authenticationResponse.setToken(tokenDTO.getToken());
        authenticationResponse.setExpiresAt(tokenDTO.getExpiresAt());

        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody Credentials credentials) {

        if (isNullOrEmpty(credentials.getUsername()) || isNullOrEmpty(credentials.getPassword()))
            throw new MissingCredentialsException(MISSING_CREDENTIALS_MESSAGE);

        if (!userService.isUserValid(credentials))
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);

        TokenDTO tokenDTO = tokenService.getByUsername(credentials.getUsername());
        if (tokenDTO == null) {
            tokenDTO = tokenService.generateToken(credentials.getUsername());
        } else if (!isTokenValid(tokenDTO.getExpiresAt())) {
            throw new ExpiredTokenException(EXPIRED_TOKEN_MESSAGE);
        }

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(tokenDTO.getUsername());
        authenticationResponse.setToken(tokenDTO.getToken());
        authenticationResponse.setExpiresAt(tokenDTO.getExpiresAt());

        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Credentials credentials) {

        if (isNullOrEmpty(credentials.getUsername()) || isNullOrEmpty(credentials.getPassword()))
            throw new MissingCredentialsException(MISSING_CREDENTIALS_MESSAGE);

        UserDTO existingUser = userService.findByUsername(credentials.getUsername());
        if (existingUser != null) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MESSAGE);
        }

        UserDTO user = userService.saveUser(credentials.getUsername(), credentials.getPassword());
        return ResponseEntity.ok(buildString(CREATED, SPACE, user.getUsername()));
    }
}
