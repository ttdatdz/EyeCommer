package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.utils.UserStatus;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    List<String> findAllRolesByUserId(long userId);
    User getUserByEmail(String email);
    User getByUsername(String userName);
    int addUser(UserRequestDTO user);
    long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException;
    long saveUser(User request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);
    UserDetailResponse getUser(Long userId);
    //Phân trang + sort 1 column
    PageResponse getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);
    //Phân trang + sort nhiều column
    PageResponse getAllUsersWithSortByMultilColumn(int pageNo, int pageSize, String sortBy);
    //Phân trang + sort + tìm kiếm
    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);
    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);

    String confirmUser(int userId, String verifyCode);


}
