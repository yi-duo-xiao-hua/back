package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.HospitalVO;
import com.example.hello.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医院管理控制器
 */
@RestController
@RequestMapping("/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 获取医院列表
     */
    @GetMapping
    public Result<List<HospitalVO>> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }
}

