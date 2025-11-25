package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.request.UserUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.exception.InvalidDataException;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHasRoleRepository userHasRoleRepository;
    private final UserMapper userMapper;



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
        if (StringUtils.isNotBlank(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone đã tồn tại."); // Sử dụng InvalidDataException nếu bạn có
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


    @Override
    public void delete(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // soft-delete: mark user as inactive instead of removing from DB
        u.setStatus(com.eyecommer.Backend.utils.UserStatus.INACTIVE);
        userRepository.save(u);
    }
    @Override
    public UserDetailResponse updateUser(long userId, UserUpdateRequestDTO request) {
        // Lấy Entity gốc từ DB
        User user = getUserById(userId);
        Long currentUserId = user.getId();
        // --- 1. Cập nhật các trường thông thường (PATCH Logic) ---

        // Trường String (Kiểm tra null VÀ rỗng/blank)
        if (StringUtils.isNotBlank(request.getFirstName())) user.setFirstName(request.getFirstName());
        if (StringUtils.isNotBlank(request.getLastName())) user.setLastName(request.getLastName());


        // Cập nhật các trường địa chỉ Profile
        if (StringUtils.isNotBlank(request.getProfileAddressDetail())) user.setProfileAddressDetail(request.getProfileAddressDetail());
        if (StringUtils.isNotBlank(request.getProfileCity())) user.setProfileCity(request.getProfileCity());
        if (StringUtils.isNotBlank(request.getProfileDistrict())) user.setProfileDistrict(request.getProfileDistrict());
        if (StringUtils.isNotBlank(request.getProfilePostalCode())) user.setProfilePostalCode(request.getProfilePostalCode());

        // Trường Date/Enum (Kiểm tra null)
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        // --- 2. Xử lý các trường Đặc biệt (Username, Email, Password,phone) ---

        if (StringUtils.isNotBlank(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            // Kiểm tra xem username mới có tồn tại với ID KHÁC không
            if (userRepository.findByUsernameAndIdNot(request.getUsername(), currentUserId).isPresent()) {
                throw new InvalidDataException("Username '" + request.getUsername() + "' đã được sử dụng.");
            }
            user.setUsername(request.getUsername());
        }

        // B. Email (Check trùng, loại trừ bản thân)
        if (StringUtils.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            // Kiểm tra xem email mới có tồn tại với ID KHÁC không
            if (userRepository.findByEmailAndIdNot(request.getEmail(), currentUserId).isPresent()) {
                throw new InvalidDataException("Email '" + request.getEmail() + "' đã được sử dụng.");
            }
            user.setEmail(request.getEmail());
        }

        // C. Phone (Check trùng, loại trừ bản thân)
        if (StringUtils.isNotBlank(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            // Kiểm tra xem phone mới có tồn tại với ID KHÁC không
            if (userRepository.findByPhoneAndIdNot(request.getPhone(), currentUserId).isPresent()) {
                throw new InvalidDataException("Phone '" + request.getPhone() + "' đã được sử dụng.");
            }
            user.setPhone(request.getPhone());
        }
        // C. Password (Mã hóa nếu được cung cấp)
        if (StringUtils.isNotBlank(request.getPassword())) {
            // Mã hóa mật khẩu mới
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        if (StringUtils.isNotBlank(request.getRoleName())) {
            // Gọi hàm thay thế vai trò
            updateUserRoles(userId, request.getRoleName());
        }

        // --- 3. Lưu và Trả về ---
        User saved = userRepository.save(user);
        // Chuyển Entity đã lưu sang DTO phản hồi chi tiết
        return userMapper.toDTO(saved);
    }
    public void updateUserRoles(Long userId, String newRoleName) {

        // 1. Lấy User Entity (Đảm bảo nó là Entity được quản lý/Managed Entity)
        User user = getUserById(userId);

        // 2. Tìm Role Entity mới
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + newRoleName));

        // 3. XÓA VAI TRÒ CŨ (Cần thực hiện trước khi thêm mới)

        // Xóa tất cả các liên kết vai trò hiện tại khỏi REPOSITORY VÀ BỘ NHỚ
        if (!user.getRoles().isEmpty()) {
            // Lấy tất cả các liên kết cũ
            List<UserHasRole> existingRolesList = new ArrayList<>(user.getRoles());

            // Xóa khỏi tập hợp trong Bộ nhớ (QUAN TRỌNG: để đồng bộ hóa)
            user.getRoles().clear();

            // Xóa khỏi Database (Sử dụng deleteAll để xóa tất cả bản ghi cũ)
            userHasRoleRepository.deleteAll(existingRolesList);
        }

        // 4. TẠO VÀ LƯU VAI TRÒ MỚI
        UserHasRole newUserRole = new UserHasRole();
        newUserRole.setUser(user);
        newUserRole.setRole(newRole);

        // THÊM: Đồng bộ hóa mối quan hệ 2 chiều (QUAN TRỌNG)
        user.getRoles().add(newUserRole);

        // 5. Lưu liên kết mới
        userHasRoleRepository.save(newUserRole);
    }
    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("user.not.found")));
    }

}
