package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.request.PurchaseReceiptItemRequestDTO;
import com.eyecommer.Backend.dto.request.PurchaseReceiptProductDTO;
import com.eyecommer.Backend.dto.request.PurchaseReceiptVariantDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;
import com.eyecommer.Backend.mapper.PurchaseReceiptMapper;
import com.eyecommer.Backend.model.*;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseReceiptServiceImpl implements PurchaseReceiptService {

    private final StockReceiptsRepository receiptRepo;
    private final StockReceiptItemRepository itemRepo;
    private final SupplierRepository supplierRepo;
    private final ProductRepository productRepo;
    private final VariantProductRepository variantRepo;
    private final PurchaseReceiptMapper mapper;
    private final GenericSearchRepository genericSearchRepository;

    // ================= CREATE =================
    @Override
    public PurchaseReceiptResponseDTO create(PurchaseReceiptCreateRequestDTO request) {

        Supplier supplier = supplierRepo.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        StockReceipts receipt = new StockReceipts();
        receipt.setSupplier(supplier);
        receipt.setStatus(PurchaseReceiptStatus.PENDING);
        receipt.setReceiptDate(LocalDateTime.now());
        receiptRepo.save(receipt);

        double totalAmount = 0;

        for (PurchaseReceiptProductDTO pReq : request.getProducts()) {

            Product product;

            // ===== PRODUCT =====
            if (pReq.getProductId() == null) {
                product = new Product();
                product.setName(pReq.getProductName());

                product.setCreatedFromReceipt(receipt);
                productRepo.save(product);
            } else {
                product = productRepo.findById(pReq.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
            }

            // ===== VARIANTS =====
            Set<String> skuInRequest = new HashSet<>();
            for (PurchaseReceiptVariantDTO vReq : pReq.getVariants()) {

                if (vReq.getQuantity() <= 0) {
                    throw new RuntimeException("Quantity must be greater than 0");
                }

                VariantProduct variant;

                // ===== VARIANT MỚI =====
                if (vReq.getVariantId() == null) {
                    if (!skuInRequest.add(vReq.getSku())) {
                        throw new RuntimeException("Duplicate SKU in request: " + vReq.getSku());
                    }

                    // Check trùng SKU trong DB
                    if (variantRepo.existsBySku(vReq.getSku())) {
                        throw new RuntimeException("SKU already exists: " + vReq.getSku());
                    }
                    variant = new VariantProduct();
                    variant.setProduct(product);
                    variant.setSku(vReq.getSku());
                    variant.setPrice(vReq.getSellingPrice()); // GIÁ BÁN
                    variant.setStock(0);
                    variant.setReservedStock(0);
                    variant.setCreatedFromReceipt(receipt);
                    variantRepo.save(variant);

                }
                // ===== VARIANT CŨ =====
                else {
                    variant = variantRepo.findById(vReq.getVariantId())
                            .orElseThrow(() -> new RuntimeException("Variant not found"));

                    // Validate variant thuộc product
                    if (!variant.getProduct().getId().equals(product.getId())) {
                        throw new RuntimeException("Variant does not belong to product");
                    }
                }

                // ===== RECEIPT ITEM =====
                StockReceiptItem item = new StockReceiptItem();
                item.setStockReceipt(receipt);
                item.setVariantProduct(variant);
                item.setQuantity(vReq.getQuantity());
                item.setPrice(vReq.getPurchasePrice()); // GIÁ NHẬP

                itemRepo.save(item);
                receipt.getItems().add(item);

                totalAmount += vReq.getQuantity() * vReq.getPurchasePrice();
            }
        }

        receipt.setTotalAmount(totalAmount);
        return mapper.toDTO(receipt);
    }

    // ================= UPDATE STATUS =================
    @Override
    @Transactional
    public PurchaseReceiptResponseDTO updateStatus(
            Long receiptId,
            PurchaseReceiptStatus newStatus
    ) {
        StockReceipts receipt = receiptRepo.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        // CHỈ CHO PHÉP TỪ PENDING
        if (receipt.getStatus() != PurchaseReceiptStatus.PENDING) {
            throw new RuntimeException("Only PENDING receipt can be updated");
        }

        if (receipt.getItems().isEmpty()) {
            throw new RuntimeException("Receipt has no items");
        }

        // ===== COMPLETED → CỘNG KHO =====
        if (newStatus == PurchaseReceiptStatus.COMPLETED) {
            for (StockReceiptItem item : receipt.getItems()) {
                VariantProduct variant = item.getVariantProduct();
                variant.setStock(variant.getStock() + item.getQuantity());
                variantRepo.save(variant);
            }
            // 2️⃣ SET PRODUCT = ACTIVE (CHỈ PRODUCT MỚI)
            Set<Product> productsCreatedFromReceipt = receipt.getItems().stream()
                    .map(item -> item.getVariantProduct().getProduct())
                    .filter(p -> p.getCreatedFromReceipt() != null &&
                            p.getCreatedFromReceipt().getId().equals(receipt.getId()))
                    .collect(Collectors.toSet());

            for (Product product : productsCreatedFromReceipt) {
                product.setStatus("ACTIVE"); // hoặc enum ProductStatus.ACTIVE
                productRepo.save(product);
            }
        }

        // ===== RETURNED → CHỈ DỌN DATA =====
        if (newStatus == PurchaseReceiptStatus.RETURNED) {

            // LƯU PRODUCT MỚI TẠO TỪ RECEIPT
            Set<Product> productsCreatedFromReceipt = receipt.getItems().stream()
                    .map(item -> item.getVariantProduct().getProduct())
                    .filter(p -> p.getCreatedFromReceipt() != null &&
                            p.getCreatedFromReceipt().getId().equals(receipt.getId()))
                    .collect(Collectors.toSet());

            Iterator<StockReceiptItem> iterator = receipt.getItems().iterator();

            while (iterator.hasNext()) {

                StockReceiptItem item = iterator.next();
                VariantProduct variant = item.getVariantProduct();

                // CHỈ XÓA VARIANT MỚI
                if (variant.getCreatedFromReceipt() != null &&
                        variant.getCreatedFromReceipt().getId().equals(receipt.getId())) {

                    iterator.remove();           // xóa StockReceiptItem
                    variantRepo.delete(variant); // xóa Variant
                }
            }

            // XÓA PRODUCT LUÔN (KHÔNG CHECK VARIANT COUNT)
            for (Product product : productsCreatedFromReceipt) {
                productRepo.delete(product);
            }
        }

        receipt.setStatus(newStatus);
        receiptRepo.save(receipt);

        return mapper.toDTO(receipt);
    }

    // ================= GET ALL =================
    @Override
    public PageResponse<?> getAll(int pageNo, int pageSize, String sortBy, String[] search) {

        List<SearchCriteria> criteria = SearchCriteriaUtils.convert(search);

        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                StockReceipts.class,
                pageNo,
                pageSize,
                criteria,
                sortBy,
                new GenericSearchQueryCriteriaConsumer<>(null, null, null)
        );

        List<StockReceipts> receipts =
                (List<StockReceipts>) rawPage.getItems();

        List<PurchaseReceiptResponseDTO> dtoList =
                receipts.stream()
                        .map(mapper::toDTO)
                        .toList();

        return PageResponse.builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    // ================= GET DETAIL =================
    @Override
    public PurchaseReceiptResponseDTO getDetail(Long id) {
        StockReceipts receipt = receiptRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));
        return mapper.toDTO(receipt);
    }
}

