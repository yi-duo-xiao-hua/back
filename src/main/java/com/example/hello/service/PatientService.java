package com.example.hello.service;

import com.example.hello.common.Result;
import com.example.hello.dto.AssessmentDetailVO;
import com.example.hello.dto.PatientInitDTO;
import com.example.hello.dto.PatientProfileVO;
import com.example.hello.dto.PatientUpdateDTO;

import java.util.Map;

/**
 * 患者服务接口
 */
public interface PatientService {
    /**
     * 获取当前登录患者的基本信息和所有评估记录
     */
    Result<PatientProfileVO> getPatientProfile();

    /**
     * 修改患者基本信息
     */
    Result<Void> updatePatient(PatientUpdateDTO patientUpdateDTO);

    /**
     * 根据评估记录ID获取详情
     */
    Result<AssessmentDetailVO> getAssessmentDetail(Integer assessmentId);

    /**
     * 初始化患者信息
     */
    Result<Map<String, Integer>> initPatientInfo(PatientInitDTO patientInitDTO);
}

