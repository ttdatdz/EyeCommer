package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CartItemRequestDTO;
import com.eyecommer.Backend.dto.response.CartResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.CartService;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    // 1. GET Cart của user
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
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PageResponse<?> pageResponse = cartService.getCartByUser(
                    user.getId(),
                    pageNo,
                    pageSize,
                    sortBy,
                    search
            );

            return new ResponseData<>(HttpStatus.OK.value(), "Lấy giỏ hàng thành công", pageResponse);

        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy giỏ hàng thất bại vì: " + e.getMessage());
        }
    }

    // 2. Thêm item vào cart
    @PostMapping("/items")
    @PreAuthorize("hasAuthority('user')")
    public ResponseData<?> addItemToCart(Principal principal, @RequestBody CartItemRequestDTO itemRequest) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CartResponseDTO response = cartService.addItemToCart(user.getId(), itemRequest);
            return new ResponseData<>(HttpStatus.OK.value(), "Thêm sản phẩm vào giỏ hàng thành công", response);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),
                    "Thêm sản phẩm vào giỏ hàng thất bại vì: " + e.getMessage());
        }
    }

    // 3. Cập nhật số lượng item
    @PutMapping("/items/{variantProductId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseData<?> updateItemQuantity(Principal principal,
                                              @PathVariable Long variantProductId,
                                              @RequestParam Integer quantity) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CartResponseDTO response = cartService.updateItemQuantity(user.getId(), variantProductId, quantity);
            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật số lượng sản phẩm thành công", response);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),
                    "Cập nhật số lượng sản phẩm thất bại vì: " + e.getMessage());
        }
    }

    // 4. Xóa 1 item khỏi cart
    @DeleteMapping("/items/{variantProductId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseData<?> removeItemFromCart(Principal principal, @PathVariable Long variantProductId) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            cartService.removeItemFromCart(user.getId(), variantProductId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Xóa sản phẩm khỏi giỏ hàng thành công");
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),
                    "Xóa sản phẩm khỏi giỏ hàng thất bại vì: " + e.getMessage());
        }
    }

    // 5. Xóa toàn bộ giỏ hàng
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('user')")
    public ResponseData<?> clearCart(Principal principal) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            cartService.clearCart(user.getId());
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Xóa toàn bộ giỏ hàng thành công");
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(),
                    "Xóa toàn bộ giỏ hàng thất bại vì: " + e.getMessage());
        }
    }
}
