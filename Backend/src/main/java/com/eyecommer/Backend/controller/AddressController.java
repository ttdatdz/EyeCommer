package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/user/{userId}")
    public AddressResponseDTO createAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequestDTO dto) {

        return addressService.createAddress(userId, dto);
    }

    @PutMapping("/{addressId}")
    public AddressResponseDTO updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO dto) {

        return addressService.updateAddress(addressId, dto);
    }

    @DeleteMapping("/{addressId}")
    public String deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return "Address deleted successfully";
    }

    @GetMapping("/{addressId}")
    public AddressResponseDTO getAddressById(@PathVariable Long addressId) {
        return addressService.getAddressById(addressId);
    }

    @GetMapping("/user/{userId}")
    public List<AddressResponseDTO> getAddressesByUser(@PathVariable Long userId) {
        return addressService.getAddressesByUser(userId);
    }
}
