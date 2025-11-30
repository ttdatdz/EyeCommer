package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.CartItemResponseDTO;
import com.eyecommer.Backend.dto.response.CartResponseDTO;
import com.eyecommer.Backend.model.Cart;
import com.eyecommer.Backend.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    public CartResponseDTO toDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setItems(cart.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()));
        dto.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());
        dto.setTotalQuantity(cart.getItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum());
        return dto;
    }

    public CartItemResponseDTO toItemDTO(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(item.getId());
        dto.setVariantProductId(item.getVariantProduct().getId());
        dto.setVariantName(item.getVariantProduct().getSku()); // hoặc tên khác
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public List<CartResponseDTO> toDTOList(List<Cart> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
