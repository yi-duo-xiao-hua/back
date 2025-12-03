package com.example.hello.service;

import com.example.hello.common.Result;
import com.example.hello.dto.CurrentUserVO;
import com.example.hello.dto.LoginRequest;
import com.example.hello.dto.LoginResponseVO;
import com.example.hello.dto.RegisterRequest;

public interface AuthService {
    Result<Void> register(RegisterRequest registerRequest);

    Result<LoginResponseVO> login(LoginRequest loginRequest);

    Result<Void> logout(String token);

    Result<CurrentUserVO> getCurrentUser(String token);
}


