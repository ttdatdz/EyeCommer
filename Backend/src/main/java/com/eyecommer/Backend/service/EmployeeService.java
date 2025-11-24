package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;

public interface EmployeeService {
//    PageResponse getAll(int pageNo, int pageSize, String search, String sortBy);
    Long create(UserRequestDTO dto);
//    EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto);
    void delete(Long id);
}
