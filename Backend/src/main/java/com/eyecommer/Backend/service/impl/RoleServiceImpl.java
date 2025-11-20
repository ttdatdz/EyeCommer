package com.eyecommer.Backend.service.impl;
import com.eyecommer.Backend.dto.response.RoleResponse;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.model.Role;
import com.eyecommer.Backend.repository.RoleRepository;
import com.eyecommer.Backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<RoleResponse>getAllByUserId(Long userId) {
        return roleRepository.getAllByUserId(userId)
                .stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId().longValue()) // vì Role kế thừa AbstractEntity<Integer>
                        .name(role.getName())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Role getByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}
