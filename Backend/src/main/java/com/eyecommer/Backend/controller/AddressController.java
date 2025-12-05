package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;

    private Long getUserID(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseData<?> createAddress(@RequestBody AddressRequestDTO request, Principal principal) {
        try {
            Long userId = getUserID(principal);
            AddressResponseDTO resp = addressService.createAddress(userId, request);

            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo địa chỉ thành công", resp);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Tạo địa chỉ thất bại vì: " + e.getMessage());
        }
    }



    @GetMapping("/{id}")
    public ResponseData<?> getById(@PathVariable Long id, Principal principal) {
        try {
            Long userId = getUserID(principal);
            AddressResponseDTO dto = addressService.getById(id, userId);

            return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết địa chỉ thành công", dto);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Lấy chi tiết thất bại vì: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequestDTO request,
            Principal principal
    ) {
        try {
            Long userId = getUserID(principal);
            AddressResponseDTO dto = addressService.updateAddress(id, userId, request);

            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật địa chỉ thành công", dto);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Cập nhật địa chỉ thất bại vì: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteAddress(@PathVariable Long id, Principal principal) {
        try {
            Long userId = getUserID(principal);
            addressService.deleteAddress(id, userId);

            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Xóa địa chỉ thành công");
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Xóa địa chỉ thất bại vì: " + e.getMessage());
        }
    }


    @GetMapping
    @PreAuthorize("hasAuthority('user')")
    public ResponseData<?> getCartByUser(
            Principal principal,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search
    ) {
        try {
            Long userId = getUserID(principal);

            PageResponse<?> pageResponse = addressService.getAllByUser(
                    userId,
                    pageNo,
                    pageSize,
                    sortBy,
                    search
            );

            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách địa chỉ thành công", pageResponse);

        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy danh sách thất bại vì: " + e.getMessage());
        }
    }
}
