package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.VoucherRequestDTO;
import com.eyecommer.Backend.dto.request.VoucherUpdateDTO;
import com.eyecommer.Backend.dto.response.VoucherResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.VoucherService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private static final Logger log = LoggerFactory.getLogger(VoucherController.class);
    @Autowired
    private VoucherService voucherService;

    // 1. CREATE (POST)
    @PostMapping
    public ResponseData<?> createVoucher(@Valid @RequestBody VoucherRequestDTO requestDTO) {
        try {
            VoucherResponseDTO response = voucherService.createVoucher(requestDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo Voucher thành công", response);
        } catch (IllegalArgumentException e) {
            // Lỗi nghiệp vụ (ví dụ: trùng mã code, validation)
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Tạo Voucher thất bại vì: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống " +e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Tạo Voucher thất bại vì lỗi hệ thống." + e.getMessage());
        }
    }

    // 2. READ ALL (GET) - Lấy danh sách Voucher có phân trang
    // 2. READ ALL (GET) - Lấy danh sách Voucher có phân trang, sắp xếp, tìm kiếm
    @GetMapping
    public ResponseData<?> getAllVouchers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {
        try {
            // GỌI SERVICE VỚI CÁC THAM SỐ PHÂN TRANG, SẮP XẾP, TÌM KIẾM
            PageResponse<?> pageResponse = voucherService.getAllVouchers(pageNo, pageSize, sortBy, search);

            // Mã 200 OK vì đây là thao tác lấy dữ liệu thành công
            return new ResponseData<>( HttpStatus.OK.value(),"Lấy danh sách Voucher thành công", pageResponse);

        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return new ResponseData<>( HttpStatus.INTERNAL_SERVER_ERROR.value(),"Lấy danh sách Voucher thất bại vì: " + e.getMessage());
        }
    }

    // 3. READ DETAIL (GET by ID)
    @GetMapping("/{id}")
    public ResponseData<?> getVoucherById(@PathVariable Long id) {
        try {
            VoucherResponseDTO response = voucherService.getVoucherById(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết Voucher thành công", response);
        } catch (EntityNotFoundException e) {
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Lấy chi tiết Voucher thất bại vì: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống: " +e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy chi tiết Voucher thất bại vì lỗi hệ thống.");
        }
    }

    // 4. UPDATE (PUT) - Đã đổi thành PUT để nhất quán với RESTful nếu bạn muốn thay thế toàn bộ
    // Nếu bạn muốn dùng PATCH như ví dụ, chỉ cần đổi @PutMapping thành @PatchMapping
    @PutMapping("/{id}")
    public ResponseData<?> updateVoucher(
            @PathVariable Long id,
            @Valid @RequestBody VoucherUpdateDTO updateDTO) {
        try {
            VoucherResponseDTO response = voucherService.updateVoucher(id, updateDTO);
            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật Voucher thành công", response);
        } catch (EntityNotFoundException e) {
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Cập nhật Voucher thất bại vì: " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Lỗi nghiệp vụ (đã có người dùng nhận/đã đến ngày bắt đầu, trùng mã code)
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Cập nhật Voucher thất bại vì: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống: " +e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Cập nhật Voucher thất bại vì lỗi hệ thống.");
        }
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteVoucher(@PathVariable Long id) {
        try {
            voucherService.deleteVoucher(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Xóa Voucher thành công");
        } catch (EntityNotFoundException e) {
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Xóa Voucher thất bại vì: " + e.getMessage());
        } catch (IllegalStateException e) {
            // Lỗi nghiệp vụ (đã có người dùng nhận/đã đến ngày bắt đầu)
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Xóa Voucher thất bại vì: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi hệ thống " +e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Xóa Voucher thất bại vì lỗi hệ thống.");
        }
    }
}

//// Controller riêng cho luồng khách hàng
//@RestController
//@RequestMapping("/api/customer/vouchers")
//class CustomerVoucherController {
//
//    @Autowired
//    private VoucherService voucherService;
//
//    // READ (Get Available Vouchers for Customer)
//    @GetMapping("/available")
//    public ResponseData<?> getAvailableVouchers() {
//        try {
//            List<VoucherResponseDTO> response = voucherService.getAvailableVouchersForCustomer();
//            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách Voucher khả dụng thành công", response);
//        } catch (Exception e) {
//            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy danh sách Voucher khả dụng thất bại vì: " + e.getMessage());
//        }
//    }
//
//    // TODO: Bổ sung API cho nghiệp vụ Lấy voucher (phát hành theo mùa) và Áp dụng voucher
//}