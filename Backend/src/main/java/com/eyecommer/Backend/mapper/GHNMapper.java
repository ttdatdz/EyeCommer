package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.request.GHNCreateOrderRequest;
import com.eyecommer.Backend.dto.request.GHNItemDTO;
import com.eyecommer.Backend.model.Order;
import org.springframework.stereotype.Component;

@Component
public class GHNMapper {

    public GHNCreateOrderRequest toCreateOrderRequest(
            Order order,
            ConfirmOrderRequestDTO req
    ) {

        GHNCreateOrderRequest ghnReq = new GHNCreateOrderRequest();
        ghnReq.setTo_name(order.getAddress().getReceiverName());
        ghnReq.setTo_phone(order.getAddress().getReceiverPhone());
        ghnReq.setTo_address(order.getAddress().getAddressDetail());
        ghnReq.setTo_district_id(order.getAddress().getDistrictId());
        ghnReq.setTo_ward_code(order.getAddress().getWardCode());

        ghnReq.setService_type_id(req.getServiceTypeId());
        ghnReq.setPayment_type_id(req.getPaymentTypeId());

        ghnReq.setItems(
                order.getOrderItems().stream().map(i -> {
                    GHNItemDTO dto = new GHNItemDTO();
                    dto.setName(i.getVariantProduct().getSku());
                    dto.setQuantity(i.getQuantity());
                    dto.setPrice(i.getPrice());
                    return dto;
                }).toList()
        );

        return ghnReq;
    }
}

