package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.ForgotPasswordRequest;
import com.eyecommer.Backend.dto.request.ResetPasswordDTO;
import com.eyecommer.Backend.dto.request.SignInRequest;
import com.eyecommer.Backend.dto.request.SignUpRequestDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.dto.response.TokenResponse;
import com.eyecommer.Backend.exception.InvalidDataException;
import com.eyecommer.Backend.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

//import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseData<TokenResponse> login(@RequestBody SignInRequest request) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(),"Đăng nhập thành công",authenticationService.authenticate(request));
        }catch(Exception e) {
            return new ResponseData<>(HttpStatus.FORBIDDEN.value(),"Đăng nhập thất bại vì: " + e.getMessage());
        }

    }
    @PostMapping("/register")
    public ResponseData<?> register(@RequestBody @Valid SignUpRequestDTO request) {
        try {
            authenticationService.register(request);
            return new ResponseData<>(HttpStatus.OK.value(),"Đăng ký thành công");
        }catch(Exception e) {
            return new ResponseData<>(HttpStatus.FORBIDDEN.value(),"Đăng ký thất bại vì: " + e.getMessage());
        }

    }
    @PostMapping("/refresh")
    public ResponseData<TokenResponse> refresh(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        try {
            TokenResponse tokenResponse = authenticationService.refreshToken(refreshToken);
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Thành công",
                    tokenResponse
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Thất bại: " + e.getMessage()
            );
        }
    }
    @PostMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest  request) {
        try {
           String authorization = request.getHeader(AUTHORIZATION);
            if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
                throw new InvalidDataException("Missing or invalid Authorization header");
            }
            String token = authorization.substring("Bearer ".length());
            return new ResponseData<>(HttpStatus.OK.value(),"thành công",authenticationService.logout(token));
        }catch(Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),"thất bại vì: " + e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public ResponseData<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String email = request.getEmail();
            if (StringUtils.isBlank(email)) {
                throw new InvalidDataException("Missing or invalid Email");
            }
            return new ResponseData<>(HttpStatus.OK.value(),"thành công",(authenticationService.forgotPassword(email)));
        }catch(Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),"thất bại vì: " + e.getMessage());
        }
    }

//    @PostMapping("/reset-password")
//    public ResponseData<String> resetPassword(@RequestParam String resetToken) {
//        try {
//            System.out.println("resetToken: " + resetToken);
//            if (StringUtils.isBlank(resetToken)) {
//                throw new InvalidDataException("Missing or invalid Reset Token");
//            }
//            return new ResponseData<>(HttpStatus.OK.value(),"thành công",(authenticationService.resetPassword(resetToken)));
//        }catch(Exception e) {
//            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),"thất bại vì: " + e.getMessage());
//        }
//    }

    @PostMapping("/change-password")
    public ResponseData<String> changePassword(@RequestBody @Valid ResetPasswordDTO request) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(),"thành công",(authenticationService.changePasswordForForgotPassword(request)));
        }catch(Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),"thất bại vì: " + e.getMessage());
        }
    }
}
