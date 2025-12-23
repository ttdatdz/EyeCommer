package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.request.PurchaseReceiptItemRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;
import com.eyecommer.Backend.mapper.PurchaseReceiptMapper;
import com.eyecommer.Backend.model.StockReceiptItem;
import com.eyecommer.Backend.model.StockReceipts;
import com.eyecommer.Backend.model.Supplier;
import com.eyecommer.Backend.model.VariantProduct;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.service.PurchaseReceiptService;
import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseReceiptServiceImpl implements PurchaseReceiptService {

    private final StockReceiptsRepository receiptRepo;
    private final StockReceiptItemRepository itemRepo;
    private final SupplierRepository supplierRepo;
    private final VariantProductRepository variantRepo;
    private final PurchaseReceiptMapper mapper;
    private final GenericSearchRepository genericSearchRepository;

    @Override
    public void create(PurchaseReceiptCreateRequestDTO request) {

        // ===== XỬ LÝ SUPPLIER (TEXT INPUT) =====
        Supplier supplier = supplierRepo
                .findByEmailIgnoreCase(request.getSupplierEmail())
                .orElseGet(() -> {
                    Supplier s = new Supplier();
                    s.setName(request.getSupplierName());
                    s.setEmail(request.getSupplierEmail());
                    s.setPhone(request.getSupplierPhone());
                    s.setAddress(request.getSupplierAddress());
                    return supplierRepo.save(s);
                });

        // ===== CREATE RECEIPT =====
        StockReceipts receipt = new StockReceipts();
        receipt.setSupplier(supplier);
        receipt.setStatus(PurchaseReceiptStatus.PENDING);
        receipt.setReceiptDate(LocalDateTime.now());

        receiptRepo.save(receipt);

        double total = 0;

        // =====ITEMS =====
        for (PurchaseReceiptItemRequestDTO i : request.getItems()) {

            VariantProduct variant = variantRepo.findById(i.getVariantProductId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            StockReceiptItem item = new StockReceiptItem();
            item.setStockReceipt(receipt);
            item.setVariantProduct(variant);
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());

            itemRepo.save(item);
            receipt.getItems().add(item);

            total += i.getQuantity() * i.getPrice();
        }

        receipt.setTotalAmount(total);
    }

//    @Override
//    public void updateStatus(PurchaseReceiptUpdateStatusRequestDTO request) {
//
//        StockReceipts receipt = receiptRepo.findById(request.getReceiptId())
//                .orElseThrow();
//
//        // chỉ cho update từ PENDING
//        if (receipt.getStatus() != PurchaseReceiptStatus.PENDING) {
//            throw new RuntimeException("Receipt already processed");
//        }
//
//        if (request.getStatus() == PurchaseReceiptStatus.COMPLETED) {
//            // cộng kho
//            for (StockReceiptItem item : receipt.getItems()) {
//                VariantProduct variant = item.getVariantProduct();
//                variant.setStock(
//                        variant.getStock() + item.getQuantity()
//                );
//            }
//        }
//
//        receipt.setStatus(request.getStatus());
//    }

    @Override
    public PageResponse<?> getAll(
            int pageNo, int pageSize, String sortBy, String[] search) {

        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                StockReceipts.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                new GenericSearchQueryCriteriaConsumer<>(null, null, null)
        );

        List<PurchaseReceiptResponseDTO> dtoList =
                ((List<StockReceipts>) rawPage.getItems())
                        .stream()
                        .map(mapper::toDTO)
                        .toList();

        return PageResponse.<List<PurchaseReceiptResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    @Override
    public PurchaseReceiptResponseDTO getDetail(Long id) {
        return mapper.toDTO(
                receiptRepo.findById(id).orElseThrow()
        );
    }
}
