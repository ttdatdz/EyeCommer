package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.CartItemRequestDTO;
import com.eyecommer.Backend.dto.response.CartResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;

public interface CartService {

    PageResponse<?> getCartByUser(Long userId,int pageNo, int pageSize, String sortBy, String[] search);

    CartResponseDTO addItemToCart(Long userId, CartItemRequestDTO itemRequest);

    CartResponseDTO updateItemQuantity(Long userId, Long variantProductId, Integer quantity);

    void removeItemFromCart(Long userId, Long variantProductId);

    void clearCart(Long userId);
}