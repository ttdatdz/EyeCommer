package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.*;
import com.eyecommer.Backend.service.UserService;
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
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;
//    @GetMapping
//    @PreAuthorize("hasAuthority('admin')")
//    public ResponseData<PageResponse> getAll(
//            @RequestParam(defaultValue = "0") int pageNo,
//            @RequestParam(defaultValue = "10") int pageSize,
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) String sortBy
//    ) {
//        try {
//            PageResponse resp = employeeService.getAll(pageNo, pageSize, search, sortBy);
//            return new ResponseData<>(200, "Success", resp);
//        } catch (Exception e) {
//            return new ResponseData<>(400, "Failed: " + e.getMessage());
//        }
//    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseData<String> delete(@PathVariable Long id) {
        try {
            employeeService.delete(id);
            return new ResponseData<>(200, "Employee deleted successfully");
        } catch (Exception e) {
            return new ResponseData<>(400, "Failed: " + e.getMessage());
        }
    }
//    @PostMapping
//    @PreAuthorize("hasAuthority('admin')")
//    public ResponseData<?> create(@RequestBody @Valid UserRequestDTO dto) {
//        try {
//            Long employeeID = employeeService.create(dto);
//            return new ResponseData<>(200, "Created", employeeID);
//        } catch (Exception e) {
//            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Failed: " + e.getMessage());
//        }
//    }

//    @PatchMapping("/{id}")
//    @PreAuthorize("hasAuthority('admin')")
//    public ResponseData<EmployeeResponseDTO> update(@PathVariable Long id, @RequestBody @Valid EmployeeUpdateDTO dto) {
//        try {
//            EmployeeResponseDTO updated = employeeService.update(id, dto);
//            return new ResponseData<>(200, "Updated", updated);
//        } catch (Exception e) {
//            return new ResponseData<>(400, "Failed: " + e.getMessage());
//        }
//    }




}
