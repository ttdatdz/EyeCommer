package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.request.UserUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;

public interface EmployeeService {
//    PageResponse getAll(int pageNo, int pageSize, String search, String sortBy);
    Long create(UserRequestDTO dto);
    void delete(Long id);
    UserDetailResponse updateUser(long userId, UserUpdateRequestDTO request);
}
