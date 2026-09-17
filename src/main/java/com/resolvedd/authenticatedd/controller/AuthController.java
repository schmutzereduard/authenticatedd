package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.*;
import com.resolvedd.authenticatedd.exception.*;
import com.resolvedd.authenticatedd.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.resolvedd.authenticatedd.constants.Constants.*;
import static com.resolvedd.authenticatedd.constants.ExceptionConstants.*;
import static com.resolvedd.authenticatedd.utils.StringUtils.buildString;
import static com.resolvedd.authenticatedd.utils.StringUtils.isNullOrEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/isAuthorized")
    public ResponseEntity<AuthenticationResponse> isAuthorized(@RequestHeader(AUTHORIZATION) String authHeader) {

        TokenDTO validToken = tokenService.isValidToken(authHeader);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(validToken.getUsername());
        authenticationResponse.setToken(validToken.getToken());

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
        } else  {
            tokenDTO = tokenService.isValidToken(tokenDTO.getToken());
        }

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(tokenDTO.getUsername());
        authenticationResponse.setToken(tokenDTO.getToken());

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
