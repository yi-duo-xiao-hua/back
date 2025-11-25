package com.example.hello.service.impl;

import com.example.hello.common.Result;
import com.example.hello.dto.HospitalVO;
import com.example.hello.entity.Hospital;
import com.example.hello.mapper.HospitalMapper;
import com.example.hello.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医院服务实现类
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Override
    public Result<List<HospitalVO>> getAllHospitals() {
        List<Hospital> hospitals = hospitalMapper.selectAllHospitals();
        List<HospitalVO> voList = hospitals.stream().map(hospital -> {
            HospitalVO vo = new HospitalVO();
            BeanUtils.copyProperties(hospital, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }
}

