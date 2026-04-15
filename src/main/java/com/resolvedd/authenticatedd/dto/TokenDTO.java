package com.resolvedd.authenticatedd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {

    private String username;
    private String token;
    private long expiresAt;
}
