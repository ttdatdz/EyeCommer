package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.mapper.OrderMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.GHNService;
import com.eyecommer.Backend.service.OrderService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import com.eyecommer.Backend.utils.SnapshotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final GHNService ghnService;
    private final ShipmentRepository shipmentRepository;
    private final GenericSearchRepository genericSearchRepository;

    @Override
    public OrderDetailResponseDTO getOrderDetail(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDetailDTO(order);
    }

    @Override
    public PageResponse<?> getMyOrders(Long userId, int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // Inject thêm filter userId vào → đảm bảo chỉ lọc order của user đó
        criteriaList.add(new SearchCriteria("user.id", ":", userId));

        // 2. Khởi tạo consumer filter mặc định (nếu cần)
        SearchQueryCriteriaConsumer<Order> consumer = new GenericSearchQueryCriteriaConsumer<>(null,null,null);

        // 3. Search theo generic repo
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Order.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        // 4. Convert list entity -> DTO
        List<OrderSummaryResponseDTO> dtoList = ((List<Order>) rawPage.getItems())
                .stream()
                .map(orderMapper::toSummaryDTO)
                .toList();

        // 5. Trả về PageResponse
        return PageResponse.<List<OrderSummaryResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    @Override
    public PageResponse<?> getAllOrders(int pageNo, int pageSize, String sortBy, String[] search) {
        // giống getMyOrders nhưng không filter theo user
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);
        SearchQueryCriteriaConsumer<Order> consumer = new GenericSearchQueryCriteriaConsumer<>(null,null,null);

        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Order.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        List<OrderSummaryResponseDTO> dtoList = ((List<Order>) rawPage.getItems())
                .stream()
                .map(orderMapper::toSummaryDTO)
                .toList();

        return PageResponse.<List<OrderSummaryResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }

    @Transactional
    @Override
    public void confirmOrder(ConfirmOrderRequestDTO request) {

        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 1️⃣ Check trạng thái
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be confirmed");
        }

        // 2️⃣ Check shipment tồn tại
        if (shipmentRepository.existsByOrder(order)) {
            throw new RuntimeException("Shipment already created");
        }

        // 3️⃣ Gọi GHN tạo shipment (FAIL → rollback)
        ghnService.createShipment(order);

        // 4️⃣ Update order
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        orderRepository.save(order);
    }



    @Override
    @Transactional
    public void cancelOrder(String orderCode, String reason) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        //  Chỉ cho hủy khi PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                    "Chỉ được hủy đơn khi trạng thái là PENDING"
            );
        }

        // ✅ Hoàn kho thật (đã trừ ở confirmSnapshot)
        for (OrderItem item : order.getOrderItems()) {

            VariantProduct variant = item.getVariantProduct();

            variant.setStock(
                    variant.getStock() + item.getQuantity()
            );
        }

        // ✅ Update order
        order.setStatus(OrderStatus.CANCELLED);
        order.setCanceledAt(LocalDateTime.now());
        order.setCancelReason(reason);

        orderRepository.save(order);
    }

}
