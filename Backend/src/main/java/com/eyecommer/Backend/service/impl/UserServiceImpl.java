package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.mapper.AddressMapper;
import com.eyecommer.Backend.mapper.UserMapper;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.RoleRepository;
import com.eyecommer.Backend.repository.UserHasRoleRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.UserService;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import com.eyecommer.Backend.utils.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final GenericSearchRepository genericSearchRepository;

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


    public PageResponse<?> getAllUser(int pageNo, int pageSize, String sortBy, String[] search) {

        // convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);
        criteriaList.add(new SearchCriteria("status", ":", UserStatus.ACTIVE));
        SearchQueryCriteriaConsumer<User> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // dùng generic search repo
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                User.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        List<User> users = (List<User>) rawPage.getItems();

        List<UserDetailResponse> dtoList = userMapper.toDTOList(users);
        List<UserDetailResponse> filtered = dtoList.stream()
                .filter(u -> u.getRoles() != null &&
                        u.getRoles().stream()
                                .anyMatch(r -> r.toLowerCase().equals("user")))
                .toList();

        // trả lại pageResponse nhưng items = DTO

        return PageResponse.<List<UserDetailResponse>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(filtered)
                .build();
    }
    public PageResponse<?> getAllStaff(int pageNo, int pageSize, String sortBy, String[] search) {

        // convert search -> criteria
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);
        criteriaList.add(new SearchCriteria("status", ":", UserStatus.ACTIVE));
        SearchQueryCriteriaConsumer<User> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // dùng generic search repo
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                User.class,
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );

        List<User> users = (List<User>) rawPage.getItems();

        List<UserDetailResponse> dtoList = userMapper.toDTOList(users);
        List<UserDetailResponse> filtered = dtoList.stream()
                .filter(u -> u.getRoles() != null &&
                        u.getRoles().stream()
                                .anyMatch(r -> r.toLowerCase().equals("staff")))
                .toList();
        // trả lại pageResponse nhưng items = DTO

        return PageResponse.<List<UserDetailResponse>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(filtered)
                .build();
    }

    @Override
    public String confirmUser(int userId, String verifyCode) {
        return "Confirmed!";
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("user.not.found")));
    }
}
