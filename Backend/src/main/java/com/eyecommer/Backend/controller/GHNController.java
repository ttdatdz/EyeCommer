package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.DistrictRequestDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
public class GHNController {

    private final GHNService ghnLocationService;

    @GetMapping("/provinces")
    public ResponseData<?> getProvinces() {
        try {
            return new ResponseData<>(
                    200,
                    "Lấy danh sách tỉnh thành công",
                    ghnLocationService.getProvinces()
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
                    ghnLocationService.getDistricts(request.getProvince_id())
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
                    ghnLocationService.getWards(district_id)
            );
        }catch (Exception e){
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Có lỗi xảy ra bên server "+ e.getMessage());
        }
    }
}