package com.resolvedd.authenticatedd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {

    private String username;
    private String token;
}
