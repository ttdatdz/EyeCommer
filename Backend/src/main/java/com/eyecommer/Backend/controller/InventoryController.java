package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.response.InventoryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

//    @GetMapping
//    public ResponseData<?> getInventory() {
//        try {
//            List<InventoryResponseDTO> inventory =
//                    inventoryService.getInventory();
//
//            return new ResponseData<>(
//                    HttpStatus.OK.value(),
//                    "Lấy danh sách tồn kho thành công",
//                    inventory
//            );
//        } catch (Exception e) {
//            return new ResponseData<>(
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    "Lấy tồn kho thất bại: " + e.getMessage()
//            );
//        }
//    }
    @GetMapping
    public ResponseData<?> getInventory(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {
        try {
            PageResponse<?> pageResponse = inventoryService.getInventory(pageNo, pageSize, sortBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách tồn kho thành công", pageResponse);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy tồn kho thất bại: " + e.getMessage());
        }
    }
}
