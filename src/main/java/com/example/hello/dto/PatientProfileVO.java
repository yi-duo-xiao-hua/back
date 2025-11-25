package com.example.hello.dto;

import java.util.List;

/**
 * 患者基本信息VO（包含评估记录列表）
 */
public class PatientProfileVO {
    private PatientInfoVO patientInfo;
    private List<AssessmentItemVO> assessmentList;

    public PatientProfileVO() {
    }

    public PatientInfoVO getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientInfoVO patientInfo) {
        this.patientInfo = patientInfo;
    }

    public List<AssessmentItemVO> getAssessmentList() {
        return assessmentList;
    }

    public void setAssessmentList(List<AssessmentItemVO> assessmentList) {
        this.assessmentList = assessmentList;
    }

    /**
     * 患者基本信息
     */
    public static class PatientInfoVO {
        private Integer patientId;
        private Integer userId;
        private String name;
        private Integer gender;
        private String birthDate;
        private String idCard;
        private String medicalHistoryRemark;
        private String createTime;
        private String updateTime;

        public PatientInfoVO() {
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

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    /**
     * 评估记录项VO
     */
    public static class AssessmentItemVO {
        private Integer assessmentId;
        private String scaleName;
        private Integer totalScore;
        private String assessmentResult;
        private String assessmentDate;
        private String recommendation;

        public AssessmentItemVO() {
        }

        public Integer getAssessmentId() {
            return assessmentId;
        }

        public void setAssessmentId(Integer assessmentId) {
            this.assessmentId = assessmentId;
        }

        public String getScaleName() {
            return scaleName;
        }

        public void setScaleName(String scaleName) {
            this.scaleName = scaleName;
        }

        public Integer getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(Integer totalScore) {
            this.totalScore = totalScore;
        }

        public String getAssessmentResult() {
            return assessmentResult;
        }

        public void setAssessmentResult(String assessmentResult) {
            this.assessmentResult = assessmentResult;
        }

        public String getAssessmentDate() {
            return assessmentDate;
        }

        public void setAssessmentDate(String assessmentDate) {
            this.assessmentDate = assessmentDate;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}

