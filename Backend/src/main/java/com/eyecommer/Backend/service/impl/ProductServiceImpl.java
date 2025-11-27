package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.*;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.mapper.ProductMapper;
import com.eyecommer.Backend.mapper.VariantImageMapper;
import com.eyecommer.Backend.mapper.VariantProductMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.ProductService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final VariantProductRepository variantProductRepository;
    private final GenericSearchRepository genericSearchRepository;
    private final OrderRepository orderRepository;
    // Danh sách các trạng thái đơn hàng được coi là "đang xử lý"
    private static final List<String> PENDING_STATUSES = List.of(
            "PENDING", //Đơn hàng vừa được đặt (Chờ xác nhận thanh toán/tồn kho).
            "PROCESSING", // Đơn hàng đã được xác nhận. Đang đóng gói hoặc đã giao cho đơn vị vận chuyển nhưng chưa lấy.
            "SHIPPED" //Shipper đã lấy hàng (Đang trên đường giao). Xóa sản phẩm khiến hệ thống mất khả năng theo dõi, cập nhật trạng thái nhận hàng, hoặc xử lý trả hàng/hoàn tiền sau này.
    );
    private final VariantImageMapper variantImageMapper;
    private final VariantProductMapper variantProductMapper;

    @Override
    @Transactional
    public List<ProductResponseDTO> createProduct(List<ProductRequestDTO> requests) {

        // 0. Validate SKU
        validateDuplicateSkus(requests);

        // 1. Load maps
        Map<Long, Category> categoryMap = loadCategoryMap(requests);
        Map<Long, Attribute> attributeMap = loadAttributeMap(requests);

        List<Product> savedProducts = new ArrayList<>();

        for (ProductRequestDTO req : requests) {
            Product product = productMapper.toEntity(req);

            processVariants(product, req, attributeMap);
            processCategories(product, req, categoryMap);

            savedProducts.add(productRepository.save(product));
        }

        return productMapper.toDTOList(savedProducts);
    }


    private void validateDuplicateSkus(List<ProductRequestDTO> requests) {
        Set<String> allSkus = new HashSet<>();

        for (ProductRequestDTO req : requests) {
            if (req.getVariantProducts() != null) {
                for (VariantProductRequestDTO v : req.getVariantProducts()) {
                    if (v.getSku() != null && !allSkus.add(v.getSku())) {
                        throw new RuntimeException("Lỗi: SKU trùng trong request: " + v.getSku());
                    }
                }
            }
        }

        List<VariantProduct> existing = variantProductRepository.findAllBySkuIn(allSkus);
        if (!existing.isEmpty()) {
            String existList = existing.stream()
                    .map(VariantProduct::getSku)
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Các SKU sau đã tồn tại: " + existList);
        }
    }


    private Map<Long, Category> loadCategoryMap(List<ProductRequestDTO> requests) {
        Set<Long> ids = new HashSet<>();

        for (ProductRequestDTO r : requests) {
            if (r.getCategoryIds() != null) {
                ids.addAll(r.getCategoryIds());
            }
        }

        return categoryRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
    }


    private Map<Long, Attribute> loadAttributeMap(List<ProductRequestDTO> requests) {
        Set<Long> ids = new HashSet<>();

        for (ProductRequestDTO r : requests) {
            if (r.getVariantProducts() != null) {
                r.getVariantProducts().forEach(v ->
                        ids.addAll(v.getVariantAttributeIds())
                );
            }
        }

        return attributeRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Attribute::getId, a -> a));
    }


    private void processVariants(Product product, ProductRequestDTO req,
                                 Map<Long, Attribute> attributeMap) {

        if (req.getVariantProducts() == null || req.getVariantProducts().isEmpty()) {
            createDefaultVariant(product);
            return;
        }

        Set<VariantProduct> variants = new HashSet<>();

        for (VariantProduct variant : product.getVariants()) {
            variant.setProduct(product);

            VariantProductRequestDTO original = req.getVariantProducts().stream()
                    .filter(v -> v.getSku().equals(variant.getSku()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy DTO variant của SKU: " + variant.getSku())
                    );

            Set<VariantProductAttribute> attrs = new HashSet<>();

            for (Long attrId : original.getVariantAttributeIds()) {
                Attribute attr = attributeMap.get(attrId);
                if (attr == null)
                    throw new RuntimeException("Attribute ID không hợp lệ: " + attrId);

                VariantProductAttribute vpa = new VariantProductAttribute();
                vpa.setVariantProduct(variant);
                vpa.setAttribute(attr);
                attrs.add(vpa);
            }

            variant.setAttributes(attrs);
            variants.add(variant);
        }

        product.setVariants(variants);
    }


    private void createDefaultVariant(Product product) {
        VariantProduct defaultVariant = new VariantProduct();

        String safeName = (product.getName() == null || product.getName().isEmpty())
                ? "UNKNOWN_PRODUCT"
                : product.getName();

        defaultVariant.setSku(safeName.toUpperCase().replaceAll("\\s", "") + "-SKU");
        defaultVariant.setPrice(product.getPrice());
        defaultVariant.setStock(0);
        defaultVariant.setProduct(product);

        product.getVariants().add(defaultVariant);
    }


    private void processCategories(Product product, ProductRequestDTO req,
                                   Map<Long, Category> categoryMap) {

        if (req.getCategoryIds() == null || req.getCategoryIds().isEmpty()) return;

        Set<ProductCategory> pcs = new HashSet<>();

        for (Long id : req.getCategoryIds()) {
            Category category = categoryMap.get(id);
            if (category == null)
                throw new RuntimeException("Category ID không hợp lệ: " + id);

            ProductCategory pc = new ProductCategory();
            pc.setProduct(product);
            pc.setCategory(category);
            pc.setIsDefault(false);
            pcs.add(pc);
        }

        product.setProductCategories(pcs);
    }
    // --- READ ALL (GET ALL) ---
    @Override
    public PageResponse<?> getAllProducts(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (Filter mặc định)
        SearchQueryCriteriaConsumer<Product> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Product.class, // Tìm kiếm trên Entity Product
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Lấy List Entity và ánh xạ sang DTO
        List<Product> products = (List<Product>) rawPage.getItems();
        List<ProductResponseDTO> dtoList = productMapper.toDTOList(products);

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<ProductResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    // --- READ DETAIL (GET DETAIL) ---
    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return productMapper.toDTO(product);
    }


    // --- DELETE ---
    @Override
    @Transactional
    public void deleteProduct(Long id) {

        //3 trường hợp không được xóa.
        // Thứ nhất biến thể của sản phẩm vẫn còn tồn kho(stock>0). Vì ngừng bán thì số sản phẩm đó sẽ k biết xử lý ra sao.Nên an toàn thì chỉ cho xóa khi stock =0
        //Thứ 2. Không được xóa khi vẫn còn người đặt hàng. Để tránh TH2 ta chỉ nên cho xóa khi stock = 0
        //Thứ 3. Không được xóa khi đơn hàng đặt biến thể của sản phẩm này vẫn đang trong trạng thái pending, processing, shipped
        Product product = findProductOrThrow(id);

        // --- 1. KIỂM TRA ĐƠN HÀNG ĐANG CHỜ XỬ LÝ ---
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            Set<VariantProduct> variants = product.getVariants();

            // Lấy tất cả IDs của các biến thể (VariantProduct) thuộc sản phẩm này
            Set<Long> variantIds = variants.stream()
                    .map(VariantProduct::getId)
                    .collect(Collectors.toSet());

            // Truy vấn DB: Kiểm tra xem có đơn hàng nào đang PENDING liên quan không
            long pendingOrderCount = orderRepository.countPendingOrderItemsByVariantIds(
                    variantIds,
                    PENDING_STATUSES
            );

            if (pendingOrderCount > 0) {
                throw new RuntimeException("Không thể xóa sản phẩm. Có " + pendingOrderCount +
                        " đơn hàng đang xử lý hoặc chưa hoàn tất liên quan đến sản phẩm này.");
            }

            // --- 2. KIỂM TRA TỒN KHO ---
            boolean hasStock = variants.stream()
                    .anyMatch(v -> v.getStock() != null && v.getStock() > 0);

            if (hasStock) {
                throw new RuntimeException("Không thể xóa sản phẩm.Sản phẩm vẫn còn tồn kho.");
            }
        }

        // --- 3. THỰC HIỆN XÓA MỀM (SOFT DELETE) ---
        // Tồn kho = 0 và không có đơn hàng đang xử lý -> INACTIVE
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private VariantProduct findVariantOrThrow(Product product, Long variantId) {
        if (product.getVariants() == null) {
            throw new RuntimeException("Sản phẩm không có biến thể nào được tải.");
        }

        return product.getVariants().stream()
                .filter(v -> v.getId() != null && v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy VariantProduct với ID: " + variantId + " trong sản phẩm này."));
    }



    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO requestDTO) {
        Product existingProduct = findProductOrThrow(id);

        // 1. CẬP NHẬT THÔNG TIN CƠ BẢN (An toàn)
        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setStatus(requestDTO.getStatus());
        existingProduct.setThumbnailUrl(requestDTO.getThumbnailUrl()); // Cập nhật Product Thumbnail
        existingProduct.setShortDescription(requestDTO.getShortDescription());

        // 2. CẬP NHẬT DANH MỤC (N-M)
        updateProductCategories(existingProduct, requestDTO.getCategoryIds());

        // 3. CẬP NHẬT BIẾN THỂ (1-N)
        updateProductVariants(existingProduct, requestDTO.getVariantProducts());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }
    // ------------------- HÀM HỖ TRỢ NGHIỆP VỤ UPDATE -------------------

    private void updateProductCategories(Product product, List<Long> newCategoryIds) {
        // LƯU Ý QUAN TRỌNG: Dùng Collection gốc của Entity
        Set<ProductCategory> existingPcs = product.getProductCategories();

        // 1. Xóa các liên kết cũ (Hibernate sẽ theo dõi và xóa khỏi DB)
        existingPcs.clear();

        if (newCategoryIds != null && !newCategoryIds.isEmpty()) {

            List<Category> categories = categoryRepository.findAllById(newCategoryIds);

            if (categories.size() != newCategoryIds.size()) {
                Set<Long> foundIds = categories.stream().map(Category::getId).collect(Collectors.toSet());
                String missingIds = newCategoryIds.stream().filter(id -> !foundIds.contains(id)).map(String::valueOf).collect(Collectors.joining(", "));
                throw new RuntimeException("Category ID không hợp lệ hoặc không tìm thấy: " + missingIds);
            }

            // 2. TẠO VÀ THÊM CÁC PHẦN TỬ MỚI VÀO COLLECTION GỐC
            categories.stream()
                    .map(category -> {
                        ProductCategory pc = new ProductCategory();
                        pc.setProduct(product);    // BẮT BUỘC: Gán mối quan hệ ngược lại
                        pc.setCategory(category);
                        pc.setIsDefault(false);
                        return pc;
                    })
                    // THÊM TRỰC TIẾP VÀO COLLECTION GỐC SAU KHI CLEAR
                    .forEach(existingPcs::add);

        }
    }

    private void updateProductVariants(Product product, List<VariantProductUpdateDTO> updateDTOs) {

        Set<Long> updatedVariantIds = updateDTOs.stream()
                .filter(v -> v.getId() != null)
                .map(VariantProductUpdateDTO::getId)
                .collect(Collectors.toSet());
        // 3.1. XỬ LÝ XÓA CÁC BIẾN THỂ CŨ
        Set<VariantProduct> variantsToRemove = product.getVariants().stream()
                .filter(v -> v.getId() != null && !updatedVariantIds.contains(v.getId()))
                .collect(Collectors.toSet());
        for (VariantProduct variant : variantsToRemove) {
            // Lấy Set ID của SKU bị xóa
            Set<Long> singleVariantId = Set.of(variant.getId());

            // 1. KIỂM TRA ĐƠN HÀNG ĐANG HOẠT ĐỘNG (PENDING_STATUSES)
            long activeOrderCount = orderRepository.countPendingOrderItemsByVariantIds(
                    singleVariantId,
                    PENDING_STATUSES // List.of("PENDING", "PROCESSING", "SHIPPED")
            );

            if (activeOrderCount > 0) {
                // Nếu có đơn hàng đang xử lý, TUYỆT ĐỐI KHÔNG XÓA.
                throw new RuntimeException("Không thể xóa SKU '" + variant.getSku() +
                        "' vì có " + activeOrderCount + " đơn hàng đang trong quá trình xử lý.");
            }
            if (variant.getStock() != null && variant.getStock() > 0) {
                throw new RuntimeException("Không thể xóa SKU '" + variant.getSku() + "' vì vẫn còn tồn kho (" + variant.getStock() + ").");
            }
            product.getVariants().remove(variant);
        }

        // 3.2. XỬ LÝ CÁC BIẾN THỂ MỚI HOẶC HIỆN TẠI (UPDATE/CREATE)
        for (VariantProductUpdateDTO dto : updateDTOs) {
            if (dto.getId() == null) {
                // TẠO MỚI SKU
                VariantProduct newVariant = variantProductMapper.toEntity(dto); // Giả định VPM có toEntity(UpdateDTO)
                newVariant.setProduct(product);
                product.getVariants().add(newVariant);

                // Cần xử lý Attribute và Image cho SKU mới
                updateVariantAttributes(newVariant, dto.getVariantAttributeIds());
                updateVariantImages(newVariant, dto.getImages());

            } else {
                // CẬP NHẬT SKU HIỆN TẠI
                VariantProduct existingVariant = findVariantOrThrow(product, dto.getId());

                // RÀNG BUỘC SỬA SKU: Không được sửa Mã SKU nếu đã có lịch sử giao dịch
                if (!existingVariant.getSku().equals(dto.getSku())) {
                    if (orderRepository.hasOrderItemHistory(existingVariant.getId())) {
                        throw new RuntimeException("Không thể sửa Mã SKU từ '" + existingVariant.getSku() +
                                "' sang '" + dto.getSku() + "' vì SKU cũ đã có lịch sử giao dịch.");
                    }
                }

                // Cập nhật các trường an toàn cơ bản
                existingVariant.setSku(dto.getSku());
                existingVariant.setPrice(dto.getPrice());
                // Giả định: Nếu client gửi Stock, chỉ cho phép cập nhật khi Stock = 0 (trường hợp tạo mới/reset)
                if (dto.getStock() != null && dto.getStock() != existingVariant.getStock()) {
                    // Nếu muốn bắt buộc phải kiểm tra, bạn nên chuyển logic này sang API Nhập kho.
                    // Nếu muốn giữ, bạn cần có logic kiểm tra quyền và log thay đổi.
                    throw new RuntimeException("Không thể update số lượng tồn kho, vì stock chỉ được cập nhật qua Nhập/Xuất kho");
                }
                // Cập nhật các mối quan hệ 1-N (Attribute và Images)
                updateVariantAttributes(existingVariant, dto.getVariantAttributeIds());
                updateVariantImages(existingVariant, dto.getImages());
            }
        }
    }

    // --- HÀM 3: CẬP NHẬT ATTRIBUTE (THUỘC TÍNH) ---
    private void updateVariantAttributes(VariantProduct variantProduct, List<Long> newAttributeIds) {

        // 1. LẤY THAM CHIẾU ĐẾN SET GỐC
        Set<VariantProductAttribute> existingAttributes = variantProduct.getAttributes();

        // 2. Xóa liên kết cũ (Hibernate theo dõi và xóa)
        // Nếu getAttributes() có thể là null, cần khởi tạo an toàn ở đây.
        if (existingAttributes == null) {
            existingAttributes = new HashSet<>();
            variantProduct.setAttributes(existingAttributes); // Gán Set mới nếu nó là null
        }
        existingAttributes.clear();

        if (newAttributeIds != null && !newAttributeIds.isEmpty()) {
            List<Attribute> attributes = attributeRepository.findAllById(newAttributeIds);

            // Kiểm tra tính toàn vẹn (Giữ nguyên)
            if (attributes.size() != newAttributeIds.size()) {
                Set<Long> foundIds = attributes.stream().map(Attribute::getId).collect(Collectors.toSet());
                String missingIds = newAttributeIds.stream().filter(id -> !foundIds.contains(id)).map(String::valueOf).collect(Collectors.joining(", "));
                throw new RuntimeException("Attribute ID không hợp lệ hoặc không tìm thấy: " + missingIds);
            }

            // 3. TẠO VÀ THÊM TRỰC TIẾP VÀO SET GỐC
            attributes.stream()
                    .map(attribute -> {
                        VariantProductAttribute vpa = new VariantProductAttribute();
                        vpa.setVariantProduct(variantProduct);
                        vpa.setAttribute(attribute);
                        return vpa;
                    })
                    .forEach(existingAttributes::add); // <-- THÊM VÀO SET GỐC!

            // KHÔNG GỌI: variantProduct.setAttributes(newVariantAttributes);
        }
    }

    // --- HÀM 4: CẬP NHẬT HÌNH ẢNH (IMAGES) ---
    private void updateVariantImages(VariantProduct variantProduct, List<VariantImageUpdateDTO> imageDTOs) {

        // 1. LẤY THAM CHIẾU ĐẾN SET GỐC
        Set<VariantImage> existingImages = variantProduct.getImages();

        // Khởi tạo an toàn
        if (existingImages == null) {
            existingImages = new HashSet<>();
            variantProduct.setImages(existingImages);
        }

        // 2. Xóa toàn bộ Entity VariantImage cũ (Cascade sẽ xóa khỏi DB)
        existingImages.clear();

        if (imageDTOs != null && !imageDTOs.isEmpty()) {

            // 3. TẠO VÀ THÊM TRỰC TIẾP VÀO SET GỐC
            imageDTOs.stream()
                    // Ánh xạ từng DTO sang Entity, thiết lập mối quan hệ ngược lại
                    .map(imgDto -> variantImageMapper.toEntity(imgDto, variantProduct))
                    .forEach(existingImages::add); // <-- THÊM VÀO SET GỐC!

            // KHÔNG CẦN GỌI variantProduct.setImages(newImages);
        }
    }


}
