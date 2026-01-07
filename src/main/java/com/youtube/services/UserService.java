package com.youtube.services;

import com.youtube.dtos.LoginRequest;
import com.youtube.dtos.UserDto;

public interface UserService {

    void register(UserDto userDto);

    void login(LoginRequest loginRequest);
}
