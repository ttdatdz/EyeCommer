package com.eyecommer.Backend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
public class GHNCreateOrderRequest {

    // ===== PAYMENT =====
    private Integer payment_type_id; //phí vận chuyển (shipping) 1: shop trả, 2: COD
    private Integer cod_amount; // số tiền thu hộ từ khách, nếu đã thanh toán = vnpay thì số tiền thu hộ =0

    // ===== NOTE =====
    private String note; //ghi chú cho shipper.“Giao giờ hành chính” or “Gọi trước khi giao”.Optional, không bắt buộc
    private String required_note = "KHONGCHOXEMHANG"; //Quy định cách giao hàng

    // ===== FROM (SHOP) =====
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;

    // ===== TO (CUSTOMER) =====
    private String to_name;
    private String to_phone;
    private String to_address;
    private Integer to_district_id;
    private String to_ward_code;

    // ===== SERVICE =====
    private Integer service_type_id; //loại dịch vụ giao hàng. 1 là nhanh, 2 là tiêu chuẩn

    // ===== PACKAGE =====
    private Integer weight; //cân nặng gram
    //length, width, height (cm)
    private Integer length;
    private Integer width;
    private Integer height;

    private String content; //nội dung đơn hàng

    // ===== ITEMS =====
    private List<GHNItemDTO> items; //danh sách sản phẩm
}

