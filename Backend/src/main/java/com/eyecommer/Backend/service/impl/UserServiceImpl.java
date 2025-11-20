package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.configuration.Translator;
import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.request.UserRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.UserDetailResponse;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.mapper.AddressMapper;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.SearchRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.MailService;
import com.eyecommer.Backend.service.UserService;
import com.eyecommer.Backend.utils.UserStatus;
import com.eyecommer.Backend.utils.UserType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.UnsupportedEncodingException;
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
    private final MailService mailService;
    private final AddressMapper addressMapper;
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
    public int addUser(UserRequestDTO user) {
        System.out.println("Save user into database");
        if(user.getFirstName().equals("dat")){
            throw new ResourceNotFoundException("dat không tồn tại");
        }
        return 0;
    }

    @Override
    public long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        //Map userDTO thành user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhoneNumber())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        //Lấy địa chỉ từ addressDTO, map nó qua Address và lưu nó vào db, trong saveAddress phải set lại user, nếu k sẽ k biết address này của user nào
        request.getAddresses().forEach(a ->
                user.saveAddress(addressMapper.toEntity(a)));
        userRepository.save(user);

        if(user.getId()!=null){
            //send mail
            mailService.sendConfirmLink(user.getEmail(),user.getId(),"scret-key");
        }
        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

    @Override
    public long saveUser(User user){
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhoneNumber());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
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
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(UserType.valueOf(user.getType().name()))
                .build();
    }

    @Override
    public PageResponse getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)){
//            firstName:ASC | DESC
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            //Tạo máy dò matcher gắn pattern + sortBy vào
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }else{
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        // Lấy dữ liệu phân trang từ DB
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sorts));
        Page<User> page = userRepository.findAll(pageable);
        // Convert từng User entity -> UserDetailResponse (DTO để trả ra ngoài)
        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(UserType.valueOf(user.getType().name()))
                        .build())
                .toList();
        // Đóng gói kết quả vào PageResponse
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }
    @Override
    public PageResponse  getAllUsersWithSortByMultilColumn(int pageNo, int pageSize, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        //hasText: check null + rỗng + toàn khoảng trắng
        if (StringUtils.hasText(sortBy)) {
            String[] sortByArr = sortBy.split(",");
            for (String sortByStr : sortByArr) {
                //    firstName:ASC | DESC
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                //Tạo máy dò matcher gắn pattern + sortBy vào
                Matcher matcher = pattern.matcher(sortByStr);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }
        // Lấy dữ liệu phân trang từ DB
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sorts));
        Page<User> page = userRepository.findAll(pageable);
        // Convert từng User entity -> UserDetailResponse (DTO để trả ra ngoài)
        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(UserType.valueOf(user.getType().name()))
                        .build())
                .toList();
        // Đóng gói kết quả vào PageResponse
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
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
