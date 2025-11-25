package com.example.hello.dto;

import java.util.List;

/**
 * 评估记录详情VO
 */
public class AssessmentDetailVO {
    private Integer assessmentId;
    private Integer patientId;
    private String scaleName;
    private Integer totalScore;
    private String assessmentResult;
    private String assessmentDate;
    private List<AnswerVO> answers;
    private String recommendation;

    public AssessmentDetailVO() {
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

    public List<AnswerVO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerVO> answers) {
        this.answers = answers;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    /**
     * 答题详情VO
     */
    public static class AnswerVO {
        private Integer questionNumber;
        private String questionText;
        private Integer selectedValue;

        public AnswerVO() {
        }

        public Integer getQuestionNumber() {
            return questionNumber;
        }

        public void setQuestionNumber(Integer questionNumber) {
            this.questionNumber = questionNumber;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public Integer getSelectedValue() {
            return selectedValue;
        }

        public void setSelectedValue(Integer selectedValue) {
            this.selectedValue = selectedValue;
        }
    }
}

