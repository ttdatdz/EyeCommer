package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.mapper.UserMapper;
import com.eyecommer.Backend.repository.UserHasRoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.model.Role;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.model.UserHasRole;
import com.eyecommer.Backend.repository.RoleRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.EmployeeService;
import com.eyecommer.Backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserHasRoleRepository userHasRoleRepository;
    private final UserMapper userMapper;

//    @Override
//    public PageResponse getAll(int pageNo, int pageSize, String search, String sortBy) {
//        PageResponse<?> page = userService.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy);
//        List<?> items = (List<?>) page.getItems();
//        List<EmployeeResponseDTO> mapped = new ArrayList<>();
//        // We want only users with role 'staff'. The repository may return User entities or DTOs.
//        // Strategy: for User entities, check roles directly; for DTO items (which lack role info),
//        // batch-load User entities by id and check their roles before mapping.
//        java.util.List<Long> dtoIdsToResolve = new java.util.ArrayList<>();
//        java.util.Map<Long, Object> dtoById = new java.util.HashMap<>();
//
//        if (items != null) {
//            for (Object it : items) {
//                if (it instanceof User) {
//                    User u = (User) it;
//                    boolean isStaff = u.getRoles() != null && u.getRoles().stream()
//                            .anyMatch(uh -> uh.getRole() != null && "staff".equalsIgnoreCase(uh.getRole().getName()));
//                    if (isStaff) mapped.add(mapper.toDTO(u));
//                    continue;
//                }
//
//                // If repository returned the project's DTO, collect id to resolve role
//                Long id = null;
//                if (it instanceof com.eyecommer.Backend.dto.response.UserDetailResponse) {
//                    id = ((com.eyecommer.Backend.dto.response.UserDetailResponse) it).getId();
//                }
//
//                if (id != null) {
//                    dtoIdsToResolve.add(id);
//                    dtoById.put(id, it);
//                }
//            }
//        }
//
//        // Batch load users for DTO ids and map only those with staff role
//        if (!dtoIdsToResolve.isEmpty()) {
//            Iterable<User> users = userRepository.findAllById(dtoIdsToResolve);
//            java.util.Map<Long, User> userMap = new java.util.HashMap<>();
//            users.forEach(u -> userMap.put(u.getId(), u));
//
//            for (Long id : dtoIdsToResolve) {
//                User u = userMap.get(id);
//                if (u == null) continue;
//                boolean isStaff = u.getRoles() != null && u.getRoles().stream()
//                        .anyMatch(uh -> uh.getRole() != null && "staff".equalsIgnoreCase(uh.getRole().getName()));
//                if (!isStaff) continue;
//
//                // Prefer mapping from full entity to ensure consistent fields
//                mapped.add(mapper.toDTO(u));
//            }
//        }
//
//        return PageResponse.builder()
//                .pageNo(page.getPageNo())
//                .pageSize(page.getPageSize())
//                .totalPage(page.getTotalPage())
//                .items(mapped)
//                .build();
//    }


    @Override
    public Long create(UserRequestDTO request) {
        // --- BƯỚC 0: KIỂM TRA TÍNH HỢP LỆ VÀ TRÙNG LẶP ---

        // 0a. Kiểm tra Username/Password/RoleName (Bắt buộc cho tài khoản nội bộ)
        if (StringUtils.isBlank(request.getUsername())) {
            throw new RuntimeException("Phải có username.");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            throw new RuntimeException("Phải có password.");
        }
        if (StringUtils.isBlank(request.getRoleName())) {
            throw new RuntimeException("Phải chỉ định roleName cho tài khoản nội bộ.");
        }

        // 0b. Kiểm tra trùng lặp (Tốt nhất nên đặt ở đây)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại."); // Sử dụng InvalidDataException nếu bạn có
        }
        if (StringUtils.isNotBlank(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại."); // Sử dụng InvalidDataException nếu bạn có
        }

        // 0c. Kiểm tra vai trò hợp lệ (Ví dụ: Chỉ cho phép tạo STAFF)
        // --- BƯỚC 1: XỬ LÝ DỮ LIỆU ---

        // 1a. Tìm Role Entity trước (để ném lỗi ResourceNotFoundException sớm)
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + request.getRoleName()));

        // 1b. ÁNH XẠ DTO SANG ENTITY
        User user = userMapper.toEntity(request);

        // 1c. BẢO MẬT: MÃ HÓA MẬT KHẨU (Bắt buộc)
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 1d. Thiết lập Status và Type mặc định (Nếu DTO không truyền)
        if (user.getStatus() == null) user.setStatus(com.eyecommer.Backend.utils.UserStatus.ACTIVE);


        // --- BƯỚC 2: LƯU VÀ PHÂN QUYỀN ---

        // 2a. LƯU ENTITY USER (Quan trọng: Phải lưu User trước để có ID)
        userRepository.save(user);

        // 2b. PHÂN QUYỀN: GÁN ROLE (Sử dụng Entity Role đã tìm ở bước 1a)
        UserHasRole userRole = new UserHasRole();
        userRole.setRole(role);
        userRole.setUser(user);

        // Lưu liên kết
        userHasRoleRepository.save(userRole);

        // --- BƯỚC 3: TRẢ VỀ ---
        return user.getId();
    }

//    @Override
//    public EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto) {
//        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
//        if (dto.getEmail() != null) u.setEmail(dto.getEmail());
//        if (dto.getFirstName() != null) u.setFirstName(dto.getFirstName());
//        if (dto.getLastName() != null) u.setLastName(dto.getLastName());
//        if (dto.getPhone() != null) u.setPhone(dto.getPhone());
//        if (dto.getDateOfBirth() != null) u.setDateOfBirth(dto.getDateOfBirth());
//        if (dto.getGender() != null) u.setGender(dto.getGender());
//        User saved = userRepository.save(u);
//        return mapper.toDTO(saved);
//    }

    @Override
    public void delete(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // soft-delete: mark user as inactive instead of removing from DB
        u.setStatus(com.eyecommer.Backend.utils.UserStatus.INACTIVE);
        userRepository.save(u);
    }


}
