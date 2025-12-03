package com.example.hello.service.impl;

import com.example.hello.common.JwtUtil;
import com.example.hello.common.Result;
import com.example.hello.dto.CurrentUserVO;
import com.example.hello.dto.LoginRequest;
import com.example.hello.dto.LoginResponseVO;
import com.example.hello.dto.RegisterRequest;
import com.example.hello.entity.Patient;
import com.example.hello.entity.User;
import com.example.hello.mapper.PatientMapper;
import com.example.hello.mapper.UserMapper;
import com.example.hello.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result<Void> register(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return Result.error("两次输入密码不一致");
        }
        User exist = userMapper.selectByUsername(registerRequest.getUsername());
        if (exist != null) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        userMapper.insertUser(user);
        return Result.success("注册成功", null);
    }

    @Override
    public Result<LoginResponseVO> login(LoginRequest loginRequest) {
        User user = userMapper.selectByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return Result.error("用户名或密码错误");
        }
        userMapper.updateLastLogin(user.getUserId(), LocalDateTime.now());
        LoginResponseVO responseVO = new LoginResponseVO();
        responseVO.setToken(jwtUtil.generateToken(user.getUserId(), user.getUsername()));
        LoginResponseVO.SimpleUserInfo userInfo = new LoginResponseVO.SimpleUserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        responseVO.setUserInfo(userInfo);
        return Result.success("登录成功", responseVO);
    }

    @Override
    public Result<Void> logout(String token) {
        ensureTokenValid(token);
        return Result.success("退出成功", null);
    }

    @Override
    public Result<CurrentUserVO> getCurrentUser(String token) {
        Claims claims = ensureTokenValid(token);
        Integer userId = claims.get("userId", Integer.class);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        CurrentUserVO vo = new CurrentUserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().format(DATETIME_FORMATTER) : null);
        vo.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().format(DATETIME_FORMATTER) : null);
        Patient patient = patientMapper.selectByUserId(user.getUserId());
        if (patient != null) {
            CurrentUserVO.PatientBriefVO patientVO = new CurrentUserVO.PatientBriefVO();
            patientVO.setPatientId(patient.getPatientId());
            patientVO.setName(patient.getName());
            vo.setPatientInfo(patientVO);
        }
        return Result.success(vo);
    }

    private Claims ensureTokenValid(String token) {
        if (!StringUtils.hasText(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        try {
            return jwtUtil.parseToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
    }
}


