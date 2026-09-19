package com.resolvedd.authenticatedd.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthorizationResponse {

    private final Long userId;
}
