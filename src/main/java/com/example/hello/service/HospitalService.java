package com.example.hello.service;

import com.example.hello.common.Result;
import com.example.hello.dto.HospitalVO;

import java.util.List;

/**
 * 医院服务接口
 */
public interface HospitalService {
    /**
     * 获取所有医院列表
     */
    Result<List<HospitalVO>> getAllHospitals();
}

