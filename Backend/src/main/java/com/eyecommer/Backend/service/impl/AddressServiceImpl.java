package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.dto.response.CartResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.mapper.AddressMapper;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.Cart;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.AddressRepository;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.AddressService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final GenericSearchRepository genericSearchRepository;

    @Override
    @Transactional
    public AddressResponseDTO createAddress(Long userId, AddressRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Nếu là default → clear default của user này
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultForUser(user.getId());
        }
        Address address = addressMapper.toEntity(request, user);
        Address saved = addressRepository.save(address);

        return addressMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public AddressResponseDTO updateAddress(Long addressId, Long userId, AddressRequestDTO request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa địa chỉ này");
        }

        // Nếu cập nhật thành default → xóa default các address khác
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.clearDefaultForUser(address.getUser().getId());
        }

        addressMapper.updateFromDto(request, address);
        Address saved = addressRepository.save(address);

        return addressMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này");
        }

        addressRepository.delete(address);
    }


    @Override
    @Transactional(readOnly = true)
    public AddressResponseDTO getById(Long id, Long userId) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem địa chỉ này");
        }

        return addressMapper.toDTO(address);
    }

    @Override
    public PageResponse<?> getAllByUser(
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
        SearchQueryCriteriaConsumer<Address> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 4. Query bằng generic search
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Address.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 5. Lấy danh sách cartItems rồi map DTO
        List<Address> addresses = (List<Address>) rawPage.getItems();
        List<AddressResponseDTO> dtoList = addressMapper.toDTOList(addresses);

        // 6. Trả về PageResponse dạng DTO
        return PageResponse.<List<AddressResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }
}
