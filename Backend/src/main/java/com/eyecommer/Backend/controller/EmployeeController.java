package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.request.UserUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.*;
import com.eyecommer.Backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eyecommer.Backend.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseData<?> advanceSearchWithCriteria(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {

        log.info("Request advance search query by criteria");
        try{
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách Staff thành công",
                    userService.getAllStaff(pageNo, pageSize, sortBy,search)
            );
        } catch(Exception e){
//            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Lấy danh sách staff thất bại vì: "+ e.getMessage());
        }
    }
    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseData<?> create(@RequestBody @Valid UserRequestDTO dto) {
        try {
            Long employeeID = employeeService.create(dto);
            return new ResponseData<>(200, "Created", employeeID);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Failed: " + e.getMessage());
        }
    }
    @PatchMapping("/{userId}")
    public ResponseData<UserDetailResponse> updateUser(@PathVariable long userId , @RequestBody UserUpdateRequestDTO request) {
        try {
            employeeService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail " + e.getMessage());
        }
    }

}
