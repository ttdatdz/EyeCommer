package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.PurchaseReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/purchase-receipts")
@RequiredArgsConstructor
public class PurchaseReceiptController {

    private final PurchaseReceiptService service;

    @PostMapping
    public ResponseData<?> create(
            @RequestBody PurchaseReceiptCreateRequestDTO request) {
        service.create(request);
        return new ResponseData<>(200, "Tạo phiếu nhập thành công");
    }

//    @PutMapping("/status")
//    public ResponseData<?> updateStatus(
//            @RequestBody PurchaseReceiptUpdateStatusRequestDTO request) {
//        service.updateStatus(request);
//        return new ResponseData<>(200, "Cập nhật trạng thái thành công");
//    }

    @GetMapping
    public ResponseData<?> getAll(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {

        return new ResponseData<>(
                200,
                "Lấy danh sách phiếu nhập thành công",
                service.getAll(pageNo, pageSize, sortBy, search)
        );
    }

    @GetMapping("/{id}")
    public ResponseData<?> getDetail(@PathVariable Long id) {
        return new ResponseData<>(
                200,
                "Lấy chi tiết phiếu nhập thành công",
                service.getDetail(id)
        );
    }
}

