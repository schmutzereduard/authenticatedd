package com.resolvedd.authenticatedd.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {

    private final String username;
    private final String token;
}
