package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.ChangePasswordDTO;
import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.dto.response.ResponseError;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.exception.InvalidDataException;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.service.AuthenticationService;
import com.eyecommer.Backend.service.UserService;
import com.eyecommer.Backend.utils.UserStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private static final String ERROR_MESSAGE = "errorMessage={}";
    private final AuthenticationService authenticationService;


//    @PostMapping
//    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
//        try {
//            long userId = userService.createUser(request);
//            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
//        } catch (Exception e) {
//            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
//        }
//    }

    @GetMapping("/confirm/{userId}")
    public ResponseData<String> confirm(@Min(1) @PathVariable int userId, @RequestParam String verifyCode) {
        log.info("Confirm user, userId={}, verifyCode={}", userId, verifyCode);

        try {
            userService.confirmUser(userId, verifyCode);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User has confirmed successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Confirm was failed");
        }
    }
    @PutMapping("/update/{userId}")
    public ResponseData<Void> updateUser(@PathVariable long userId ,@RequestBody UserRequestDTO request) {
        log.info("Request update userId={}", userId);
        try {
            userService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
//            e.getCause() trả về exception gốc đã gây ra exception hiện tại.
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }
    @PatchMapping("/{userId}")
    public ResponseData<Void> updateStatus(@Min(1) @PathVariable long userId, @RequestParam UserStatus status) {
        log.info("Request change status, userId={}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }
    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseData<UserDetailResponse> getDetailUser(@PathVariable @Min(1) long userId){

        try{
            return new ResponseData<>(HttpStatus.OK.value(),"get detail user successfully",userService.getUser(userId));
        }catch(ResourceNotFoundException e){
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/list-user-and-search-with-paging-and-sorting")
    public ResponseData<?> getAllUsersAndSearchWithPagingAndSorting(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                    @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                    @RequestParam(required = false) String search,
                                                                    @RequestParam(required = false) String sortBy) {
        log.info("Request get list of users and search with paging and sorting");
        try{
            PageResponse<?> users = userService.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        }catch(Exception e){
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

    }

    @GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(defaultValue = "") String... search) {
        log.info("Request advance search query by criteria");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithCriteria(pageNo,
                    pageSize, sortBy, address, search));
        }catch(Exception e){
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

    }
    @PostMapping("/change-password")
    public ResponseData<String> changePassword(@RequestBody @Valid ChangePasswordDTO request) {
        try {
            // Lấy username từ token đã được xác thực trong SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new InvalidDataException("Access token invalid or expired");
            }

            String username = authentication.getName();
            return new ResponseData<>(HttpStatus.OK.value(), "Thành công",
                    authenticationService.changePassword(request, username));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Thất bại vì: " + e.getMessage());
        }
    }
}
