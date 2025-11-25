package com.example.hello.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体类
 */
public class Patient {
    private Integer patientId;
    private Integer userId;
    private String name;
    private Integer gender;
    private LocalDate birthDate;
    private String idCard;
    private String medicalHistoryRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Patient() {
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMedicalHistoryRemark() {
        return medicalHistoryRemark;
    }

    public void setMedicalHistoryRemark(String medicalHistoryRemark) {
        this.medicalHistoryRemark = medicalHistoryRemark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

