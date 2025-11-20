package com.eyecommer.Backend.dto.response;
import com.eyecommer.Backend.utils.Gender;
import com.eyecommer.Backend.utils.UserStatus;
import com.eyecommer.Backend.utils.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserStatus status;
    private Gender gender;
    private Date dateOfBirth;
    private String phone;
    private String username;
    private UserType type;
}
