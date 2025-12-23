package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.SupplierCreateRequestDTO;
import com.eyecommer.Backend.dto.request.SupplierUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.SupplierResponseDTO;
import com.eyecommer.Backend.mapper.SupplierMapper;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.Supplier;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.StockReceiptsRepository;
import com.eyecommer.Backend.repository.SupplierRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.SupplierService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepo;
    private final StockReceiptsRepository receiptRepo;
    private final SupplierMapper mapper;
    private final GenericSearchRepository genericSearchRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponseDTO create(SupplierCreateRequestDTO request) {
        if (supplierRepo.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Supplier email already exists");
        }


        if (supplierRepo.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Supplier phone already exists");
        }
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());


        return mapper.toDTO(supplierRepo.save(supplier));
    }

    @Override
    public SupplierResponseDTO update(Long id, SupplierUpdateRequestDTO request) {

        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // ===== CHECK EMAIL (trừ chính nó) =====
        supplierRepo.findByEmailIgnoreCase(request.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Email already used by another supplier");
                });

        // ===== CHECK PHONE (trừ chính nó) =====
        supplierRepo.findByPhone(request.getPhone())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Phone already used by another supplier");
                });

        // ===== UPDATE DATA =====
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        return mapper.toDTO(supplierRepo.save(supplier));
    }

    @Override
    public void delete(Long id) {

        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (receiptRepo.existsBySupplier(supplier)) {
            throw new RuntimeException("Cannot delete supplier: has purchase receipts");
        }

        supplierRepo.delete(supplier);
    }

    // --- READ ALL (GET ALL) ---
    @Override
    public PageResponse<?> getAllSuppliers(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (Filter mặc định)
        SearchQueryCriteriaConsumer<Supplier> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Supplier.class, // Tìm kiếm trên Entity Product
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Lấy List Entity và ánh xạ sang DTO
        List<Supplier> Suppliers = (List<Supplier>) rawPage.getItems();
        List<SupplierResponseDTO> dtoList = supplierMapper.toDTOList(Suppliers);

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<SupplierResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    @Override
    public SupplierResponseDTO getDetail(Long id) {

        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        return mapper.toDTO(supplier);
    }
}
