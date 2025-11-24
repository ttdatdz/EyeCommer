package com.eyecommer.Backend.dto.request;

// Giả định các imports cho Enum Gender, UserStatus, UserType, etc.
import com.eyecommer.Backend.utils.Gender;
import com.eyecommer.Backend.utils.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data // Bao gồm @Getter và @Setter
public class UserUpdateRequestDTO {

    // TẤT CẢ CÁC TRƯỜNG ĐỀU LÀ TÙY CHỌN (OPTIONAL)

    // Không cần validation @NotBlank/@NotNull

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    // Ngày sinh cần format chuẩn
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private Gender gender;
    private String profileAddressDetail;
    private String profileCity;
    private String profileDistrict;
    private String profilePostalCode;
    private UserStatus status;
    private String roleName;
    // Loại bỏ 'type' nếu bạn đã xóa khỏi Entity
}