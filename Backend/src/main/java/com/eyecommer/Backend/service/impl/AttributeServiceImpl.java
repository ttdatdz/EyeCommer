package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.request.VariantUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.AttributeResponseDTO;
import com.eyecommer.Backend.exception.ResourceNotFoundException; // Cần tạo exception này
import com.eyecommer.Backend.mapper.AttributeMapper;
import com.eyecommer.Backend.model.Attribute;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.AttributeRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.AttributeService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttributeServiceImpl implements AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeMapper attributeMapper;
    @Autowired
    private GenericSearchRepository genericSearchRepository;

    private Attribute findVariantOrThrow(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));
    }

    @Override
    @Transactional
    public AttributeResponseDTO createVariant(VariantRequestDTO requestDTO) {
        // Ánh xạ DTO sang Entity
        Attribute attribute = attributeMapper.toEntity(requestDTO);

        // Lưu Entity
        Attribute savedAttribute = attributeRepository.save(attribute);

        // Ánh xạ Entity sang Response DTO
        return attributeMapper.toDTO(savedAttribute);
    }

    @Override
    public PageResponse<?> getAllVariants(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert mảng search string sang List<SearchCriteria>
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (nếu cần logic lọc đặc biệt, nếu không thì để null hoặc mặc định)
        SearchQueryCriteriaConsumer<Attribute> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Attribute.class, // Tìm kiếm trên Entity Variant
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Lấy List Entity và ánh xạ sang DTO
        List<Attribute> attributes = (List<Attribute>) rawPage.getItems();
        List<AttributeResponseDTO> dtoList = attributeMapper.toDTOList(attributes);

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<AttributeResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }
    @Override
    public AttributeResponseDTO getVariantById(Long id) {
        Attribute attribute = findVariantOrThrow(id);
        return attributeMapper.toDTO(attribute);
    }

    @Override
    @Transactional
    public AttributeResponseDTO updateVariant(Long id, VariantUpdateDTO requestDTO) {
        Attribute existingAttribute = findVariantOrThrow(id);


        if (existingAttribute.getVariantProductAttributes() != null &&
                !existingAttribute.getVariantProductAttributes().isEmpty()) {

            throw new RuntimeException("Không thể cập nhật Thuộc tính vì nó đã được sử dụng để tạo biến thể sản phẩm (SKU).");
        }

        if (requestDTO.getName() != null) {
            existingAttribute.setName(requestDTO.getName());
        }
        if (requestDTO.getDescription() != null) {
            existingAttribute.setDescription(requestDTO.getDescription());
        }

        // Lưu và trả về
        Attribute updatedAttribute = attributeRepository.save(existingAttribute);
        return attributeMapper.toDTO(updatedAttribute);
    }


    @Override
    @Transactional
    public void deleteVariant(Long id) {
        Attribute attribute = findVariantOrThrow(id);


        if (attribute.getVariantProductAttributes() != null &&
                !attribute.getVariantProductAttributes().isEmpty()) {

            throw new RuntimeException("Không thể xóa Thuộc tính vì nó đã được sử dụng để tạo biến thể sản phẩm (SKU).");
        }

        attributeRepository.delete(attribute);
    }
}