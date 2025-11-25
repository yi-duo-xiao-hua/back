package com.example.hello.entity;

import java.time.LocalDateTime;

/**
 * 评估记录实体类
 */
public class Assessment {
    private Integer assessmentId;
    private Integer patientId;
    private Integer scaleId;
    private Integer totalScore;
    private String assessmentResult;
    private LocalDateTime assessmentDate;
    private String answers; // JSON格式存储
    private String recommendation;
    
    // 关联查询字段
    private String scaleName;
    
    // 答题详情（用于返回，从JSON answers解析）
    private Object answersList;

    public Assessment() {
    }

    public Integer getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getScaleId() {
        return scaleId;
    }

    public void setScaleId(Integer scaleId) {
        this.scaleId = scaleId;
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

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getScaleName() {
        return scaleName;
    }

    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public Object getAnswersList() {
        return answersList;
    }

    public void setAnswersList(Object answersList) {
        this.answersList = answersList;
    }
}

