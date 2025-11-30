package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.dto.response.VoucherResponseDTO;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer/vouchers")
public class CustomerVoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private UserRepository userRepository;

    // READ (Get Available Vouchers for Customer)
    @GetMapping("/available")
    public ResponseData<?> getAvailableVouchers(Principal principal) {
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            List<VoucherResponseDTO> response =
                    voucherService.getVouchersForCustomer(user.getId());

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách Voucher của user thành công",
                    response
            );

        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lấy danh sách Voucher thất bại vì: " + e.getMessage()
            );
        }
    }
    @PostMapping("/claim")
    public ResponseData<?> claimVoucher(
            Principal principal,
            @RequestParam Long voucherId
    ) {
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            VoucherResponseDTO response =
                    voucherService.claimVoucher(voucherId, user.getId());

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Nhận voucher thành công",
                    response
            );

        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Nhận voucher thất bại vì: " + e.getMessage()
            );
        }
    }
}
