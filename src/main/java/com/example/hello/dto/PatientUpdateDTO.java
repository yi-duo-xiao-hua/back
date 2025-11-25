package com.example.hello.dto;

import com.example.hello.common.validation.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 患者信息修改DTO
 */
public class PatientUpdateDTO {
    @NotNull(message = "患者ID不能为空")
    private Integer patientId;

    @NotBlank(message = "患者姓名不能为空")
    @Size(min = 1, max = 50, message = "患者姓名长度必须在1-50字符之间")
    private String name;

    @NotNull(message = "性别不能为空")
    @Gender(message = "性别值错误，必须为1(男)或2(女)")
    private Integer gender;

    @NotBlank(message = "出生日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出生日期格式错误，格式应为yyyy-MM-dd")
    private String birthDate;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", message = "身份证号格式错误")
    private String idCard;

    private String medicalHistoryRemark;

    public PatientUpdateDTO() {
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
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

