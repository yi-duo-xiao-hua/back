package com.example.hello.dto;

import java.time.LocalDateTime;

/**
 * 医生详情VO
 */
public class DoctorDetailVO {
    private Integer doctorId;
    private Integer hospitalId;
    private String name;
    private String department;
    private String title;
    private String specialty;
    private String introduction;
    private String avatarUrl;
    private String avatarOriginalName;
    private Integer avatarFileSize;
    private String avatarFileType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarOriginalName() {
        return avatarOriginalName;
    }

    public void setAvatarOriginalName(String avatarOriginalName) {
        this.avatarOriginalName = avatarOriginalName;
    }

    public Integer getAvatarFileSize() {
        return avatarFileSize;
    }

    public void setAvatarFileSize(Integer avatarFileSize) {
        this.avatarFileSize = avatarFileSize;
    }

    public String getAvatarFileType() {
        return avatarFileType;
    }

    public void setAvatarFileType(String avatarFileType) {
        this.avatarFileType = avatarFileType;
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

