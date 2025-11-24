package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.utils.Gender;
import com.eyecommer.Backend.utils.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Chuyển đổi UserRequestDTO sang User Entity (dùng cho CREATE/UPDATE).
     */
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = new User();

        // --- Thông tin Đăng nhập (Ánh xạ trực tiếp) ---
        // Không dùng if(StringUtils.isBlank()) ở đây
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());

        // --- Thông tin Cá nhân ---
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        if(dto.getGender()!=null){
            user.setGender(dto.getGender());
        }else{
            user.setGender(Gender.FEMALE);

        }

        // --- Địa chỉ Profile ---
        user.setProfileAddressDetail(dto.getProfileAddressDetail());
        user.setProfileCity(dto.getProfileCity());
        user.setProfileDistrict(dto.getProfileDistrict());
        user.setProfilePostalCode(dto.getProfilePostalCode());

        // --- Các trường Quản lý ---
        // Chuyển đổi String type từ DTO sang Enum UserType
        if(dto.getStatus()!=null){
            user.setStatus(dto.getStatus());
        }else{
            user.setStatus(UserStatus.ACTIVE);
        }


        return user;
    }

    /**
     * Chuyển đổi User Entity sang UserDetailResponse DTO.
     */
    public UserDetailResponse toDTO(User entity) {
        if (entity == null) return null;

        UserDetailResponse dto = new UserDetailResponse();
        dto.setId(entity.getId());

        // --- Thông tin Cá nhân ---
        dto.setUsername(entity.getUsername());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());

        // --- Địa chỉ Profile ---
        dto.setProfileAddressDetail(entity.getProfileAddressDetail());
        dto.setProfileCity(entity.getProfileCity());
        dto.setProfileDistrict(entity.getProfileDistrict());
        dto.setProfilePostalCode(entity.getProfilePostalCode());

        // --- Các trường Quản lý ---
        dto.setStatus(entity.getStatus());

        // --- Ánh xạ Đa Vai trò ---
        Set<String> roleNames = entity.getRoles().stream()
                .map(userHasRole -> userHasRole.getRole().getName())
                .collect(Collectors.toSet());

        dto.setRoles(roleNames);

        return dto;
    }

    /**
     * Chuyển List<User Entity> sang List<UserDetailResponse DTO>.
     */
    public List<UserDetailResponse> toDTOList(List<User> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}