package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
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
import com.eyecommer.Backend.service.MailService;
import com.eyecommer.Backend.service.UserService;
import com.eyecommer.Backend.utils.UserStatus;
import com.eyecommer.Backend.utils.UserType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return username ->  userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
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
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);
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
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
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
