package com.example.hello.mapper;

import com.example.hello.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper {
    /**
     * 根据用户ID查询患者信息
     */
    Patient selectByUserId(Integer userId);

    /**
     * 根据患者ID查询患者信息
     */
    Patient selectByPatientId(Integer patientId);

    /**
     * 更新患者信息
     */
    int updatePatient(Patient patient);

    /**
     * 检查患者是否存在
     */
    int checkPatientExists(Integer patientId);
}

