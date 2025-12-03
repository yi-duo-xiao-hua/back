package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.CurrentUserVO;
import com.example.hello.dto.LoginRequest;
import com.example.hello.dto.LoginResponseVO;
import com.example.hello.dto.RegisterRequest;
import com.example.hello.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        return authService.logout(token);
    }

    @GetMapping("/me")
    public Result<CurrentUserVO> me(HttpServletRequest request) {
        String token = request.getHeader("token");
        return authService.getCurrentUser(token);
    }
}


