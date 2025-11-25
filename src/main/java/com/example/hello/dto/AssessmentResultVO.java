package com.example.hello.dto;

/**
 * 评估结果返回VO
 */
public class AssessmentResultVO {
    private Integer assessmentId;
    private Integer totalScore;
    private String assessmentResult;
    private String recommendation;
    private boolean needDoctorRecommendation;
    private String redirectUrl;

    public Integer getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Integer assessmentId) {
        this.assessmentId = assessmentId;
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

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public boolean isNeedDoctorRecommendation() {
        return needDoctorRecommendation;
    }

    public void setNeedDoctorRecommendation(boolean needDoctorRecommendation) {
        this.needDoctorRecommendation = needDoctorRecommendation;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}



