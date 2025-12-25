package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.request.PurchaseReceiptUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.PurchaseReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-receipts")
@RequiredArgsConstructor
public class PurchaseReceiptController {

    private final PurchaseReceiptService service;

    @PostMapping
    public ResponseData<?> create(@RequestBody PurchaseReceiptCreateRequestDTO request) {
        try {

            return new ResponseData<>(200, "Create purchase receipt success", service.create(request));
        } catch (Exception e) {
            return new ResponseData<>(400, e.getMessage());
        }
    }

    @PutMapping
    public ResponseData<?> update(
            @RequestBody PurchaseReceiptUpdateRequestDTO request
    ) {
        try {
            return new ResponseData<>(
                    200,
                    "Update purchase receipt success",
                    service.updateStatus(request.getReceiptId(), request.getNewStatus())
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Update purchase receipt failed: " + e.getMessage()
            );
        }
    }

    @GetMapping
    public ResponseData<?> getAll(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {


        try {
            return new ResponseData<>(
                    200,
                    "Lấy danh sách phiếu nhập thành công",
                    service.getAll(pageNo, pageSize, sortBy, search)
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Lấy danh sách phiếu nhập thất bại: " + e.getMessage()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseData<?> getDetail(@PathVariable Long id) {

        try {
            return new ResponseData<>(
                    200,
                    "Lấy chi tiết phiếu nhập thành công",
                    service.getDetail(id)
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Lấy chi tiết phiếu nhập thất bại: " + e.getMessage()
            );
        }
    }
}

