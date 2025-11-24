package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.Gender;
import com.eyecommer.Backend.utils.UserStatus;
import com.eyecommer.Backend.utils.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode
@Builder
@NoArgsConstructor // Cần thiết cho việc deserialize JSON
@AllArgsConstructor
public class UserDetailResponse {

    // --- Thông tin Cơ bản ---
    private Long id;
    private String username;
    private String email;
    private String phone;

    // --- Thông tin Cá nhân ---
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd") // Format khớp với Entity
    private Date dateOfBirth;

    private Gender gender;

    // --- Thông tin Phân loại/Quản lý ---
    private UserStatus status;

    // Thêm trường Vai trò (ROLE) - quan trọng cho hiển thị quyền
    private Set<String> roles; // Trả về tập hợp các vai trò (ví dụ: ["ADMIN", "STAFF"])

    // --- Địa chỉ Profile (Cần thiết cho hiển thị hồ sơ) ---
    private String profileAddressDetail;
    private String profileCity;
    private String profileDistrict;
    private String profilePostalCode;

}