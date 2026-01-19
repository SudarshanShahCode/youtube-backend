package com.youtube.services.impl;

import com.youtube.dtos.AuthResponse;
import com.youtube.dtos.LoginRequest;
import com.youtube.dtos.UserDto;
import com.youtube.dtos.VerifyUserDto;
import com.youtube.entities.Role;
import com.youtube.entities.User;
import com.youtube.repositories.RoleRepository;
import com.youtube.repositories.UserRepository;
import com.youtube.services.UserService;
import com.youtube.utils.EmailUtils;
import com.youtube.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Value("${email.from}")
    private String from;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailUtils emailUtils;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           EmailUtils emailUtils,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailUtils = emailUtils;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = toEntity(userDto);

        // generate verification code and expiry
        var verifyCode = generateVerifyCode();
        var verifyCodeExpiry = Instant.now().plusSeconds(86_400);

        // encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set default role
        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRole(role);
        user.setVerifyCode(verifyCode);
        user.setVerifyCodeExpiry(verifyCodeExpiry);

        User savedUser = userRepository.save(user);

        final String to = user.getEmail();
        final String subject = "Verify your account";
        final String htmlBody = """
                <h1>Please verify your account</h1>
                <br/>
                <p>Your verification code is: <strong>%s</strong></p>
                <br/><br/>
                *** This is an automated email, please do not reply ***
                """.formatted(verifyCode);


        emailUtils.sendEmail(from, to, subject, htmlBody);

        return toDto(savedUser);
    }

    @Override
    public boolean verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepository.findByEmail(verifyUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email"));

        if (user.getVerifyCode() == null) {
            throw new IllegalStateException("Account is already verified");
        }

        if (!user.getVerifyCode().equals(verifyUserDto.getVerifyCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (user.getVerifyCodeExpiry().isBefore(Instant.now())) {
            throw new IllegalStateException("Verification code has expired");
        }

        user.setVerifyCode(null);
        user.setVerifyCodeExpiry(null);
        user.setIsVerified(true);

        userRepository.save(user);

        final String to = user.getEmail();
        final String subject = "Verification successful";
        final String htmlBody = """
                <p>Dear User,</p>
                <br/>
                <p>Your account has been verified successfully.</p>
                <br/><br/>
                *** This is an automated email, please do not reply ***
                """;

        emailUtils.sendEmail(from, to, subject, htmlBody);

        return true;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("user found");
            if (!user.getIsVerified()) {
                throw new RuntimeException("Please verify your account");
            }
            log.info("user verified");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword())
            );
            log.info("user authenticated");
            List<@Nullable String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtUtils.generateToken(user.getEmail(), roles, user.getUserId(), 86_400_000L);
            String refreshToken = jwtUtils.generateToken(user.getEmail(), roles, user.getUserId(), 172_800_000L);

            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(Instant.now().plusSeconds(172_800_000));

            userRepository.save(user);
            log.info("Done till here!");
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException _) {
            log.error("Invalid Credentials for user: {}", loginRequest.getEmail());
            return AuthResponse.builder().build();
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return AuthResponse.builder().build();
        }
    }

    /* Additional Methods */

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public List<UserDto> getAllUsers() {
        return List.of();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return null;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return null;
    }

    private User toEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .refreshToken(user.getRefreshToken())
                .refreshTokenExpiry(user.getRefreshTokenExpiry())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String generateVerifyCode() {
        int min = 100000;
        int max = 999999;

        // Generate a random integer between the min (inclusive) and max (exclusive)
        int verifyCode = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.valueOf(verifyCode);
    }
}
