package com.eyecommer.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordDTO {
    @NotBlank(message = "secretKey must be not blank")
    private String oldPassword;

    @NotBlank(message = "password must be not blank")
    private String newPassword;

    @NotBlank(message = "confirmPassword must be not blank")
    private String confirmPassword;
}
