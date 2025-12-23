package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.response.InventoryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.mapper.InventoryMapper;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.VariantProduct;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.InventoryService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final GenericSearchRepository genericSearchRepository;

    @Override
    public PageResponse<?> getInventory(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (Filter mặc định)
        SearchQueryCriteriaConsumer<VariantProduct> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                VariantProduct.class, // Tìm kiếm trên Entity VariantProduct
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Lấy List Entity và ánh xạ sang DTO
        List<VariantProduct> variantProducts = (List<VariantProduct>) rawPage.getItems();
        List<InventoryResponseDTO> dtoList = inventoryMapper.toDTOList(variantProducts);

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<InventoryResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

}
