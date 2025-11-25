package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 医生DTO（用于新增和修改）
 */
public class DoctorDTO {
    private Integer doctorId;

    @NotNull(message = "医院ID不能为空")
    private Integer hospitalId;

    @NotBlank(message = "医生姓名不能为空")
    @Size(max = 50, message = "医生姓名长度不能超过50字符")
    private String name;

    @NotBlank(message = "所属科室不能为空")
    @Size(max = 50, message = "所属科室长度不能超过50字符")
    private String department;

    @NotBlank(message = "职称不能为空")
    @Size(max = 50, message = "职称长度不能超过50字符")
    private String title;

    @NotBlank(message = "擅长病症不能为空")
    @Size(max = 255, message = "擅长病症长度不能超过255字符")
    private String specialty;

    private String introduction;
    private String avatarUrl;

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
}

