package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.*;
import com.resolvedd.authenticatedd.exception.*;
import com.resolvedd.authenticatedd.service.*;
import io.jsonwebtoken.ExpiredJwtException;
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
    public ResponseEntity<Long> isAuthorized(@RequestHeader(AUTHORIZATION) String bearerToken) {

        return ResponseEntity.ok(tokenService.isTokenValid(bearerToken));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestHeader(AUTHORIZATION) String basicAuthHeader) {

        String username = userService.isUserValid(basicAuthHeader);
        if (username == null)
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);

        TokenDTO tokenDTO = tokenService.getByUsername(username);
        if (tokenDTO == null) {
            tokenDTO = tokenService.generateToken(username);
        } else  {
            try {
                tokenService.isTokenValid(tokenDTO.getToken());
            } catch (ExpiredJwtException e) {
                tokenDTO = tokenService.generateToken(username);
            }
        }

        return ResponseEntity.ok(new AuthenticationResponse(tokenDTO.getUsername(), tokenDTO.getToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader(AUTHORIZATION) String basicAuthHeader) {

        UserDTO user = userService.registerUser(basicAuthHeader);
        return ResponseEntity.ok(buildString(CREATED, SPACE, user.getUsername()));
    }
}
