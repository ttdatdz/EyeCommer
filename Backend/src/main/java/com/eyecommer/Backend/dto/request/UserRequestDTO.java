package com.eyecommer.Backend.dto.request;

import com.eyecommer.Backend.utils.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Getter
public class UserRequestDTO {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;
    @NotBlank(message = "lastName must be not blank")
    private String lastName;
    @Email(message = "email invalid format")
    private String email;
    @EnumPattern(name = "status",regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;
    @GenderSubset(anyOf = {Gender.MALE, Gender.FEMALE})
    private Gender gender;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;
    private String phoneNumber;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @NotEmpty(message = "addresses can not empty")
    private Set<AddressRequestDTO> addresses;
    public UserRequestDTO(String firstName, String lastName, String email, UserStatus status, Gender gender, String type, String phoneNumber, Date dateOfBirth, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.gender = gender;
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.password = password;
    }

    public @NotNull(message = "dateOfBirth must be not null") Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotNull(message = "dateOfBirth must be not null") Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotNull(message = "username must be not null") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "username must be not null") String username) {
        this.username = username;
    }

    public @NotNull(message = "password must be not null") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "password must be not null") String password) {
        this.password = password;
    }

    public @NotNull(message = "type must be not null") String getType() {
        return type;
    }

    public void setType(@NotNull(message = "type must be not null") String type) {
        this.type = type;
    }

//    public String getEmail() {
//        return email;
//    }
//    public UserStatus getStatus() {
//        return status;
//    }
//
//    public Gender getGender() {
//        return gender;
//    }
//
//    public void setGender(Gender gender) {
//        this.gender = gender;
//    }
//
//    public void setStatus(UserStatus status) {
//        this.status = status;
//    }
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
}
