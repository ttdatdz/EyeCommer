package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignInRequest {
    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "username must be not blank")
    private String password;

//    @NotNull(message = "username must be not null")
//    private Platform platform;
//
//    private String deviceToken;
//
//    private String version;
}
