package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.request.VariantUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.VariantResponseDTO;
import com.eyecommer.Backend.exception.ResourceNotFoundException; // Cần tạo exception này
import com.eyecommer.Backend.mapper.VariantMapper;
import com.eyecommer.Backend.model.Variant;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.VariantRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.VariantService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VariantServiceImpl implements VariantService {

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private VariantMapper variantMapper;
    @Autowired
    private GenericSearchRepository genericSearchRepository;

    private Variant findVariantOrThrow(Long id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));
    }

    @Override
    @Transactional
    public VariantResponseDTO createVariant(VariantRequestDTO requestDTO) {
        // Ánh xạ DTO sang Entity
        Variant variant = variantMapper.toEntity(requestDTO);

        // Lưu Entity
        Variant savedVariant = variantRepository.save(variant);

        // Ánh xạ Entity sang Response DTO
        return variantMapper.toDTO(savedVariant);
    }

    @Override
    public PageResponse<?> getAllVariants(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert mảng search string sang List<SearchCriteria>
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (nếu cần logic lọc đặc biệt, nếu không thì để null hoặc mặc định)
        SearchQueryCriteriaConsumer<Variant> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Variant.class, // Tìm kiếm trên Entity Variant
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Lấy List Entity và ánh xạ sang DTO
        List<Variant> variants = (List<Variant>) rawPage.getItems();
        List<VariantResponseDTO> dtoList = variantMapper.toDTOList(variants);

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<VariantResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }
    @Override
    public VariantResponseDTO getVariantById(Long id) {
        Variant variant = findVariantOrThrow(id);
        return variantMapper.toDTO(variant);
    }

    @Override
    @Transactional
    public VariantResponseDTO updateVariant(Long id, VariantUpdateDTO requestDTO) {
        Variant existingVariant = findVariantOrThrow(id);


        if (existingVariant.getVariantProductAttributes() != null &&
                !existingVariant.getVariantProductAttributes().isEmpty()) {

            throw new RuntimeException("Không thể cập nhật Thuộc tính vì nó đã được sử dụng để tạo biến thể sản phẩm (SKU).");
        }

        if (requestDTO.getName() != null) {
            existingVariant.setName(requestDTO.getName());
        }
        if (requestDTO.getDescription() != null) {
            existingVariant.setDescription(requestDTO.getDescription());
        }

        // Lưu và trả về
        Variant updatedVariant = variantRepository.save(existingVariant);
        return variantMapper.toDTO(updatedVariant);
    }


    @Override
    @Transactional
    public void deleteVariant(Long id) {
        Variant variant = findVariantOrThrow(id);


        if (variant.getVariantProductAttributes() != null &&
                !variant.getVariantProductAttributes().isEmpty()) {

            throw new RuntimeException("Không thể xóa Thuộc tính vì nó đã được sử dụng để tạo biến thể sản phẩm (SKU).");
        }

        variantRepository.delete(variant);
    }
}