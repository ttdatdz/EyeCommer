package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.request.UserUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.exception.InvalidDataException;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.mapper.AddressMapper;
import com.eyecommer.Backend.mapper.UserMapper;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.Role;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.model.UserHasRole;
import com.eyecommer.Backend.repository.RoleRepository;
import com.eyecommer.Backend.repository.SearchRepository;
import com.eyecommer.Backend.repository.UserHasRoleRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.UserService;
import com.eyecommer.Backend.utils.UserStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            // 1. Tìm User
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 2. KIỂM TRA TRẠNG THÁI (QUAN TRỌNG)
            if (user.getStatus() == UserStatus.INACTIVE) {
                // Ném ngoại lệ BadCredentialsException hoặc cụ thể hơn
                // (UsernameNotFoundException là cách thường dùng để ẩn chi tiết)
                throw new DisabledException("User is inactive or blocked.");
            }

            // 3. Trả về UserDetails (nếu ACTIVE)
            return user;
        };
    }

    @Override
    public List<String> findAllRolesByUserId(long userId) {
        return userRepository.findAllRolesByUserId(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
    }

    @Override
    public User getByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public long saveUser(User user){
        userRepository.save(user);
        return user.getId();
    }

//    @Override
//    public UserDetailResponse updateUser(long userId, UserUpdateRequestDTO request) {
//        // Lấy Entity gốc từ DB
//        User user = getUserById(userId);
//
//        // --- 1. Cập nhật các trường thông thường (PATCH Logic) ---
//
//        // Trường String (Kiểm tra null VÀ rỗng/blank)
//        if (StringUtils.isNotBlank(request.getFirstName())) user.setFirstName(request.getFirstName());
//        if (StringUtils.isNotBlank(request.getLastName())) user.setLastName(request.getLastName());
//
//
//        // Cập nhật các trường địa chỉ Profile
//        if (StringUtils.isNotBlank(request.getProfileAddressDetail())) user.setProfileAddressDetail(request.getProfileAddressDetail());
//        if (StringUtils.isNotBlank(request.getProfileCity())) user.setProfileCity(request.getProfileCity());
//        if (StringUtils.isNotBlank(request.getProfileDistrict())) user.setProfileDistrict(request.getProfileDistrict());
//        if (StringUtils.isNotBlank(request.getProfilePostalCode())) user.setProfilePostalCode(request.getProfilePostalCode());
//
//        // Trường Date/Enum (Kiểm tra null)
//        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
//        if (request.getGender() != null) user.setGender(request.getGender());
//        if (request.getStatus() != null) user.setStatus(request.getStatus());
//
//        // --- 2. Xử lý các trường Đặc biệt (Username, Email, Password,phone) ---
//
//        // A. Username (Check trùng khi thay đổi)
//        if (StringUtils.isNotBlank(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
//            if (userRepository.existsByUsername(request.getUsername())) {
//                throw new InvalidDataException("Username '" + request.getUsername() + "' đã được sử dụng.");
//            }
//            user.setUsername(request.getUsername());
//        }
//
//        // B. Email (Check trùng khi thay đổi)
//        if (StringUtils.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
//            if (userRepository.existsByEmail(request.getEmail())) {
//                throw new InvalidDataException("Email '" + request.getEmail() + "' đã được sử dụng.");
//            }
//            user.setEmail(request.getEmail());
//        }
//        if (StringUtils.isNotBlank(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
//            if (userRepository.existsByPhone(request.getPhone())) {
//                throw new InvalidDataException("Phone '" + request.getPhone() + "' đã được sử dụng.");
//            }
//            user.setPhone(request.getPhone());
//        }
//        // C. Password (Mã hóa nếu được cung cấp)
//        if (StringUtils.isNotBlank(request.getPassword())) {
//            // Mã hóa mật khẩu mới
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        }
//
//
//        if (StringUtils.isNotBlank(request.getRoleName())) {
//            // Gọi hàm thay thế vai trò
//            updateUserRoles(userId, request.getRoleName());
//        }
//
//        // --- 3. Lưu và Trả về ---
//        User saved = userRepository.save(user);
//        // Chuyển Entity đã lưu sang DTO phản hồi chi tiết
//        return userMapper.toDTO(saved);
//    }
//    public void updateUserRoles(Long userId, String newRoleName) {
//
//        // 1. Kiểm tra User tồn tại
//        User user = getUserById(userId); // Giả định hàm helper getUserById(Long) tồn tại
//
//        // 2. Tìm Role Entity mới
//        Role newRole = roleRepository.findByName(newRoleName)
//                .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + newRoleName));
//
//        // 3. XÓA TẤT CẢ CÁC VAI TRÒ CŨ
//        List<UserHasRole> existingRoles = userHasRoleRepository.findByUserId(userId);
//
//        if (!existingRoles.isEmpty()) {
//            userHasRoleRepository.deleteAll(existingRoles);
//        }
//
//        // 4. TẠO VÀ LƯU VAI TRÒ MỚI
//        UserHasRole newUserRole = new UserHasRole();
//        newUserRole.setUser(user);
//        newUserRole.setRole(newRole);
//        // Tùy chọn: newUserRole.setAssignedAt(new Date());
//
//        userHasRoleRepository.save(newUserRole);
//    }
    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
    }
    @Override
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public UserDetailResponse getUser(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDTO(user);
    }

    @Override
    public PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return searchRepository.searchUserByCriteria(pageNo, pageSize, sortBy, address, search);
    }

    @Override
    public String confirmUser(int userId, String verifyCode) {
        return "Confirmed!";
    }




    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("user.not.found")));
    }
    //hàm chuyển addressDTO thành Address
    private Set<Address> convertToAddress(Set<AddressRequestDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(addressMapper.toEntity(a))
        );
        return result;
    }
}
