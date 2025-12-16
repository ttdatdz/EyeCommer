package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.*;
import com.eyecommer.Backend.dto.response.GHNAvailableServiceResponse;
import com.eyecommer.Backend.dto.response.GHNFeeResponse;
import com.eyecommer.Backend.dto.response.GHNLeadTimeResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
public class GHNController {

    private final GHNService ghnService;

    @GetMapping("/provinces")
    public ResponseData<?> getProvinces() {
        try {
            return new ResponseData<>(
                    200,
                    "Lấy danh sách tỉnh thành công",
                    ghnService.getProvinces()
            );
        }catch (Exception e){
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Có lỗi xảy ra bên server "+ e.getMessage());
        }

    }

    @GetMapping("/districts")
    public ResponseData<?> getDistricts(@RequestBody DistrictRequestDTO request) {
        try {
            return new ResponseData<>(
                    200,
                    "Lấy danh sách quận/huyện thành công",
                    ghnService.getDistricts(request.getProvince_id())
            );
        }catch (Exception e){
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Có lỗi xảy ra bên server "+ e.getMessage());
        }

    }

    @GetMapping("/wards")
    public ResponseData<?> getWards(@RequestParam Integer district_id) {

        try {
            return new ResponseData<>(
                    200,
                    "Lấy danh sách phường/xã thành công",
                    ghnService.getWards(district_id)
            );
        }catch (Exception e){
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Có lỗi xảy ra bên server "+ e.getMessage());
        }
    }

    @PostMapping("/leadtime")
    public ResponseData<?> getLeadTime(
            @RequestBody GHNLeadTimeRequest request
    ) {
        try {
            GHNLeadTimeResponse result =
                    ghnService.calculateLeadTime(request);

            return new ResponseData<>(
                    200,
                    "Tính thời gian giao hàng thành công",
                    result
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Tính thời gian giao hàng thất bại: " + e.getMessage()
            );
        }
    }
    @GetMapping("/available-services")
    public ResponseData<?> getAvailableServices(
            @RequestBody GHNAvailableServiceRequest request
    ) {
        try {
            List<GHNAvailableServiceResponse> result =
                    ghnService.getAvailableServices(request);

            return new ResponseData<>(
                    200,
                    "Lấy danh sách dịch vụ GHN thành công",
                    result
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Lấy danh sách dịch vụ GHN thất bại: " + e.getMessage()
            );
        }
    }

    @PostMapping("/fee")
    public ResponseData<?> calculateShippingFee(
            @RequestBody ShippingFeeEstimateRequest request
    ) {
        try {
            GHNFeeResponse result =
                    ghnService.calculateShippingFee(request);

            return new ResponseData<>(
                    200,
                    "Tính phí ship GHN thành công",
                    result
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    "Tính phí ship GHN thất bại: " + e.getMessage()
            );
        }
    }
}