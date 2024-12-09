package com.resolvedd.authenticatedd.dto;

import java.util.Map;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String email;

    @NotEmpty
    private Map<String, String> applications;
}
