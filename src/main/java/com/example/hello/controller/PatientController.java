package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.AssessmentDetailVO;
import com.example.hello.dto.PatientProfileVO;
import com.example.hello.dto.PatientUpdateDTO;
import com.example.hello.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 患者管理控制器
 */
@RestController
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取当前登录患者的基本信息和所有评估记录
     * 请求路径：/patients/profile
     * 请求方式：GET
     */
    @GetMapping("/patients/profile")
    public Result<PatientProfileVO> getPatientProfile() {
        return patientService.getPatientProfile();
    }

    /**
     * 修改患者基本信息
     * 请求路径：/patients
     * 请求方式：PUT
     */
    @PutMapping("/patients")
    public Result<Void> updatePatient(@Valid @RequestBody PatientUpdateDTO patientUpdateDTO) {
        return patientService.updatePatient(patientUpdateDTO);
    }

    /**
     * 获取患者评估记录详情
     * 请求路径：/assessments/{id}
     * 请求方式：GET
     */
    @GetMapping("/assessments/{id}")
    public Result<AssessmentDetailVO> getAssessmentDetail(@PathVariable("id") Integer assessmentId) {
        return patientService.getAssessmentDetail(assessmentId);
    }
}

