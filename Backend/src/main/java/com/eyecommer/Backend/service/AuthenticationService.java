package com.eyecommer.Backend.service;
import com.eyecommer.Backend.dto.ChangePasswordDTO;
import com.eyecommer.Backend.dto.request.ResetPasswordDTO;
import com.eyecommer.Backend.dto.request.SignInRequest;
import com.eyecommer.Backend.dto.request.SignUpRequestDTO;
import com.eyecommer.Backend.dto.response.TokenResponse;
import com.eyecommer.Backend.exception.InvalidDataException;
import com.eyecommer.Backend.model.Role;
import com.eyecommer.Backend.model.Token;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.model.UserHasRole;
import com.eyecommer.Backend.repository.RoleRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.utils.Gender;
import com.eyecommer.Backend.utils.TokenType;
import com.eyecommer.Backend.utils.UserStatus;
import com.eyecommer.Backend.utils.UserType;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        // 1️⃣ Gọi Spring Security xác thực username/password
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );

        // 2️⃣ Lưu Authentication vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3️⃣ Lấy user đã xác thực
        var user = (User) authentication.getPrincipal();

        // 4️⃣ Tạo token
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // save token to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        // 5️⃣ Trả về response
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refreshToken(String refreshToken) {
        log.info("---------- refreshToken ----------" + refreshToken);

        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must not be blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        var user = userService.getByUsername(userName);

        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allowed access with this token");
        }
        // create new access token
        String accessToken = jwtService.generateToken(user);
//         save token to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
    public String logout(String token ) {
        log.info("---------- logout ----------");

        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        tokenService.delete(userName);

        return "Deleted!";
    }

    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        log.info("---------- forgotPassword ----------");

        // check email exists or not
        User user = userService.getUserByEmail(email);

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        // save to db
         tokenService.save(Token.builder().username(user.getUsername()).resetToken(resetToken).build());

        // tạo link reset
        String resetLink = String.format(
                "http://localhost:3000/auth/reset-password?resetToken=%s",
                resetToken
        );
        // gửi mail cho user
        mailService.sendResetPasswordLink(user.getEmail(), resetLink);

        return resetToken;
    }
    public String changePasswordForForgotPassword(ResetPasswordDTO request) {
        log.info("---------- changePassword for forgot pass word ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        // get user by reset token
        var user = validateToken(request.getSecretKey());

        // update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);

        // Xóa token reset sau khi dùng
        tokenService.delete(user.getUsername());
        return "Changed";
    }
    public String changePassword(ChangePasswordDTO request, String username) {
        log.info("---------- changePassword ----------");

        User user = userService.getByUsername(username);

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException("Old password is incorrect");
        }

        // Kiểm tra mật khẩu mới
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.saveUser(user);

        // Xóa toàn bộ token (logout tất cả)
        tokenService.delete(user.getUsername());

        return "Password changed successfully. Please log in again.";
    }
    private User validateToken(String token) {
        // lấy user name từ token
        var userName = jwtService.extractUsername(token, TokenType.RESET_TOKEN);
        if (userName == null) {
            throw new InvalidDataException("Invalid token");
        }
        var user = userService.getByUsername(userName);

        // Kiểm tra hết hạn
        if (!jwtService.isValid(token, TokenType.RESET_TOKEN,user)) {
            throw new InvalidDataException("Token has expired");
        }
        // Kiểm tra user có active không
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }
        return user;
    }

//    public void register(SignUpRequestDTO request) {
//        // 1️⃣ Kiểm tra trùng username hoặc email
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        // 2️⃣ Mã hóa mật khẩu
//        String encodedPassword = passwordEncoder.encode(request.getPassword());
//
//        // 3️⃣ Tạo user entity
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(encodedPassword);
//        user.setStatus(UserStatus.ACTIVE); // có thể đặt mặc định
//        user.setType(UserType.USER);
//        //gán giá trị mặc định cho các field của user, sau này user vào sửa hoặc được update sau
//        user.setFirstName("");
//        user.setLastName("");
//        user.setDateOfBirth(new Date());
//        user.setGender(Gender.FEMALE); // tùy enum
//
//        // 4️⃣ Ánh xạ roles
//        Set<UserHasRole> userRoles = new HashSet<>();
//
//        // Nếu người dùng không truyền roles → mặc định ROLE_USER
//        Set<String> requestedRoles = request.getRoles() != null
//                ? request.getRoles()
//                : Set.of("USER");
//
//        for (String roleName : requestedRoles) {
//            Role role = roleRepository.findByName(roleName.toLowerCase())
//                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
//
//            UserHasRole userHasRole = new UserHasRole();
//            userHasRole.setUser(user);
//            userHasRole.setRole(role);
//            userRoles.add(userHasRole);
//        }
//        for (UserHasRole roleName : userRoles) {
//            user.saveRole(roleName);
//        }
//        // 5️⃣ Lưu vào DB
//        userService.saveUser(user);
//    }

    public void register(SignUpRequestDTO request) {

        // 1️⃣ Kiểm tra trùng username hoặc email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2️⃣ Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3️⃣ Tạo user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setStatus(UserStatus.ACTIVE);
        user.setType(UserType.USER);
        user.setFirstName("");
        user.setLastName("");
        user.setDateOfBirth(new Date());
        user.setGender(Gender.FEMALE);

        // 4️⃣ Ánh xạ roles
        Set<String> requestedRoles = request.getRoles() != null
                ? request.getRoles()
                : Set.of("USER");

        for (String roleName : requestedRoles) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            UserHasRole userHasRole = new UserHasRole();
            userHasRole.setUser(user);
            userHasRole.setRole(role);

            user.getRoles().add(userHasRole);
        }

        // 5️⃣ Lưu vào DB — chỉ cần save User là đủ, roles sẽ cascade xuống
        userRepository.save(user);
    }

//    public String resetPassword(String resetToken) {
//        log.info("---------- resetPassword ----------");
//
//        // validate token
//        var user = validateToken(resetToken);
//
//        // check token by username
//        tokenService.getByUsername(user.getUsername());
//
//        return "Reset";
//    }

}
