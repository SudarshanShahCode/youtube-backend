package com.youtube.services;

import com.youtube.dtos.AuthResponse;
import com.youtube.dtos.LoginRequest;
import com.youtube.dtos.UserDto;
import com.youtube.dtos.VerifyUserDto;

import java.util.List;

public interface UserService {

    UserDto register(UserDto userDto);

    boolean verifyUser(VerifyUserDto verifyUserDto);

    AuthResponse login(LoginRequest loginRequest);

    // Additional methods
    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto getUserByEmail(String email);

    AuthResponse refreshToken(String token);
}
