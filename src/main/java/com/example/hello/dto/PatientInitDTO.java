package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientInitDTO {

    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 50, message = "患者姓名长度不能超过50个字符")
    private String name;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    @NotBlank(message = "出生日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出生日期格式不正确")
    private String birthDate;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[0-9]{17}[0-9Xx]$", message = "身份证号格式错误")
    private String idCard;

    private String medicalHistoryRemark;

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
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
}



