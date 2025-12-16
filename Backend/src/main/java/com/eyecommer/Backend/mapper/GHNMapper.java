package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.*;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.Order;
import com.eyecommer.Backend.utils.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GHNMapper {

    public GHNCreateOrderRequest toCreateOrderRequest(Order order) {

        GHNCreateOrderRequest req = new GHNCreateOrderRequest();

        // PAYMENT
        req.setPayment_type_id(
                order.getPaymentStatus() == PaymentStatus.PAID ? 1 : 2
        );
        req.setCod_amount(
                order.getPaymentStatus() == PaymentStatus.PAID
                        ? 0
                        : order.getTotalAmount().intValue()
        );

        // FROM (SHOP - CONFIG)
        req.setFrom_name("EyeCommerce");
        req.setFrom_phone("0987654321");
        req.setFrom_address("72 Thành Thái, Quận 10, HCM");
        req.setFrom_ward_name("Phường 14");
        req.setFrom_district_name("Quận 10");
        req.setFrom_province_name("HCM");

        // TO (CUSTOMER)
        Address addr = order.getAddress();
        req.setTo_name(addr.getReceiverName());
        req.setTo_phone(addr.getReceiverPhone());
        req.setTo_address(addr.getAddressDetail());
        req.setTo_district_id(addr.getDistrictId());
        req.setTo_ward_code(addr.getWardCode());

        // SERVICE
        req.setService_type_id(2);

        // ITEMS
        List<GHNItemDTO> items = order.getOrderItems().stream()
                .map(item -> {
                    GHNItemDTO dto = new GHNItemDTO();
                    dto.setName(item.getVariantProduct().getSku());
                    dto.setCode(item.getVariantProduct().getSku());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice().intValue());
                    dto.setWeight(500); // fallback
                    return dto;
                })
                .toList();

        req.setItems(items);

        // PACKAGE
        int totalWeight = items.stream()
                .mapToInt(i -> i.getWeight() * i.getQuantity())
                .sum();

        req.setWeight(totalWeight);
        req.setLength(20);
        req.setWidth(20);
        req.setHeight(10);

        req.setContent("EyeCommerce Order");

        return req;
    }

    public GHNFeeRequest toGHNFeeRequest(
            ShippingFeeEstimateRequest req
    ) {

        GHNFeeRequest ghn = new GHNFeeRequest();

        // FROM
        if(req.getFrom_district_id()!=null){
            ghn.setFrom_district_id(req.getFrom_district_id());
        }else{
            ghn.setFrom_district_id(1452);
        }
        if(req.getFrom_ward_code()!=null){
            ghn.setFrom_ward_code(req.getFrom_ward_code());
        }else{
            ghn.setFrom_ward_code("21014");
        }

        // TO
        ghn.setTo_district_id(req.getTo_district_id());
        ghn.setTo_ward_code(req.getTo_ward_code());

        // SERVICE
        ghn.setService_id(req.getService_id());
        ghn.setService_type_id(null);

        // PACKAGE
        ghn.setWeight(req.getWeight());
        ghn.setLength(req.getLength());
        ghn.setWidth(req.getWidth());
        ghn.setHeight(req.getHeight());

        // OPTIONAL
        ghn.setInsurance_value(0);
        ghn.setCod_failed_amount(0);
        ghn.setCoupon(null);
        ghn.setItems(null);

        return ghn;
    }
}

