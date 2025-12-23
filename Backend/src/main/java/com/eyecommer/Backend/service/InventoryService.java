package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.response.InventoryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;

import java.util.List;

public interface InventoryService {
//    List<InventoryResponseDTO> getInventory();

    PageResponse<?> getInventory(int pageNo, int pageSize, String sortBy, String[] search);
}
