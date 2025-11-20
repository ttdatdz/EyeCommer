package com.eyecommer.Backend.service;


import com.eyecommer.Backend.dto.response.RoleResponse;
import com.eyecommer.Backend.model.Role;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllByUserId(Long userId);
    Role getByName(String name);
}
