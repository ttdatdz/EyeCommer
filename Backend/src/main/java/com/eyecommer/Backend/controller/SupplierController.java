package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.SupplierCreateRequestDTO;
import com.eyecommer.Backend.dto.request.SupplierUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid SupplierCreateRequestDTO request) {
        try {

            return new ResponseData<>(200, "Create supplier success", supplierService.create(request));
        } catch (IllegalArgumentException e) {
            return new ResponseData<>(400, e.getMessage());
        } catch (Exception e) {
            return new ResponseData<>(500, "Create supplier failed: " + e.getMessage());
        }
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseData<?> update(
            @PathVariable Long id,
            @RequestBody @Valid SupplierUpdateRequestDTO request) {
        try {
            return new ResponseData<>(200, "Update supplier success",supplierService.update(id, request));
        } catch (IllegalArgumentException e) {
            return new ResponseData<>(400, e.getMessage());
        } catch (Exception e) {
            return new ResponseData<>(500, "Update supplier failed: " + e.getMessage());
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        try {
            supplierService.delete(id);
            return new ResponseData<>(200, "Delete supplier success");
        } catch (IllegalStateException e) {
            // dùng cho case: supplier đang được tham chiếu
            return new ResponseData<>(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ResponseData<>(400, e.getMessage());
        } catch (Exception e) {
            return new ResponseData<>(500, "Delete supplier failed: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseData<?> getAllSuppliers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {
        try {
            PageResponse<?> pageResponse = supplierService.getAllSuppliers(pageNo, pageSize, sortBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách Suppliers thành công", pageResponse);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy danh sách Suppliers thất bại vì: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseData<?> getDetail(@PathVariable Long id) {
        try {
            return new ResponseData<>(
                    200,
                    "Get supplier detail success",
                    supplierService.getDetail(id)
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Get supplier detail failed: " + e.getMessage()
            );
        }
    }
}

