package com.eyecommer.Backend.dto.request;

import com.eyecommer.Backend.utils.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    // --- Thông tin Đăng nhập (BẮT BUỘC cho tài khoản nội bộ) ---
    @NotBlank(message = "username must be not blank")
    @Size(min= 3, message = "Username must have at least 3 characters!")
    @Size(max= 20, message = "Username can have have at most 80 characters!")
    private String username;

    @NotBlank(message = "password must be not blank")
    @Size(min = 8, message = "Password must have at least 8 characters!")
    @Size(max = 20, message = "Password can have have almost 30 characters!")
    private String password;

    // --- Thông tin Cá nhân bắt buộc ---
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @Email(message = "email invalid format")
    @NotBlank(message = "email must be not blank") // Thêm NotBlank cho Email
    private String email;

    @NotBlank(message = "phone must be not blank") // Đổi từ NotNull sang NotBlank
    @Size(min = 8, message = "phone must have atleast 9 characters!")
    @Size(max = 20, message = "phone can have have almost 11 characters!")
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    // Dùng @GenderSubset của bạn (giả định đã được import)
    private Gender gender;

    // --- Địa chỉ Profile (Bắt buộc cho hồ sơ nhân viên) ---
    @NotBlank(message = "profileAddressDetail must be not blank")
    private String profileAddressDetail;

    @NotBlank(message = "profileCity must be not blank")
    private String profileCity;

    @NotBlank(message = "profileDistrict must be not blank")
    private String profileDistrict;

    @NotBlank(message = "profilePostalCode must be not blank")
    private String profilePostalCode;

    // --- Các trường Quản lý/Vai trò ---

    @EnumPattern(name = "status",regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    // Yêu cầu bắt buộc phải có roleName và kiểm tra ở Service
    @NotBlank(message = "roleName must be specified")
    private String roleName;

}
