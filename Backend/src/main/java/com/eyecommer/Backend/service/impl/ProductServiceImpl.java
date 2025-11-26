package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.request.ProductUpdateRequestDTO;
import com.eyecommer.Backend.dto.request.VariantProductRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.mapper.ProductMapper;
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

    // --- UPDATE (PUT) ---
    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO requestDTO) {
        Product existingProduct = findProductOrThrow(id);

        // 🚨 LƯU Ý QUAN TRỌNG:
        // Logic UPDATE sản phẩm có biến thể/thuộc tính là CỰC KỲ phức tạp
        // (xử lý thêm/xóa/sửa biến thể, thêm/xóa/sửa Attribute).
        // Ở đây, ta chỉ cập nhật các trường cơ bản.

        if (requestDTO.getName() != null) existingProduct.setName(requestDTO.getName());
        if (requestDTO.getDescription() != null) existingProduct.setDescription(requestDTO.getDescription());
        if (requestDTO.getPrice() != null) existingProduct.setPrice(requestDTO.getPrice());
        if (requestDTO.getStatus() != null) existingProduct.setStatus(requestDTO.getStatus());

        // Cần thêm logic xử lý Cập nhật Biến thể và Danh mục ở đây! (Không thể tự động map)

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
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
}
