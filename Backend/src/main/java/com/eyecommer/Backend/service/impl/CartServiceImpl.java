package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.CartItemRequestDTO;
import com.eyecommer.Backend.dto.response.CartResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.mapper.CartMapper;
import com.eyecommer.Backend.model.Cart;
import com.eyecommer.Backend.model.CartItem;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.model.VariantProduct;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.CartService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VariantProductRepository variantProductRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final GenericSearchRepository genericSearchRepository;

    @Override
    public PageResponse<?> getCartByUser(
            Long userId,
            int pageNo,
            int pageSize,
            String sortBy,
            String[] search
    ) {
        // 1. Convert FE search → criteria list
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Inject thêm filter userId vào → đảm bảo chỉ lọc cart của user đó
        criteriaList.add(new SearchCriteria("user.id", ":", userId));

        // 3. Consumer (giống Product)
        SearchQueryCriteriaConsumer<Cart> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 4. Query bằng generic search
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Cart.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 5. Lấy danh sách cartItems rồi map DTO
        List<Cart> carts = (List<Cart>) rawPage.getItems();
        List<CartResponseDTO> dtoList = cartMapper.toDTOList(carts);

        // 6. Trả về PageResponse dạng DTO
        return PageResponse.<List<CartResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    @Override
    public CartResponseDTO addItemToCart(Long userId, CartItemRequestDTO itemRequest) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        VariantProduct variant = variantProductRepository.findById(itemRequest.getVariantProductId())
                .orElseThrow(() -> new EntityNotFoundException("VariantProduct not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantProductId(cart.getId(), variant.getId())
                .orElse(new CartItem());

        cartItem.setCart(cart);
        cartItem.setVariantProduct(variant);
        cartItem.setQuantity((cartItem.getQuantity() == null ? 0 : cartItem.getQuantity()) + itemRequest.getQuantity());
        cartItem.setPrice(variant.getPrice());

        cartItemRepository.save(cartItem);

        cart.getItems().add(cartItem);
        return cartMapper.toDTO(cart);
    }

    @Override
    public CartResponseDTO updateItemQuantity(Long userId, Long variantProductId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantProductId(cart.getId(), variantProductId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return cartMapper.toDTO(cart);
    }

    @Override
    public void removeItemFromCart(Long userId, Long variantProductId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartIdAndVariantProductId(cart.getId(), variantProductId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.getItems().clear(); // xóa trong bộ nhớ để JPA trigger orphanRemoval
        cartRepository.save(cart);
    }
}

