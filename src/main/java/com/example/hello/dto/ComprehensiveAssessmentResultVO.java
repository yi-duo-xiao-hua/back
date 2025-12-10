package com.example.hello.dto;

/**
 * 综合量表评估结果VO
 */
public class ComprehensiveAssessmentResultVO {

    private Integer assessmentId;
    private Integer totalScore;
    private DetailedScores detailedScores;
    private AssessmentResults assessmentResults;
    private Boolean needDoctorRecommendation;
    private String severeDisease;
    private String recommendationDisease;
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

    public DetailedScores getDetailedScores() {
        return detailedScores;
    }

    public void setDetailedScores(DetailedScores detailedScores) {
        this.detailedScores = detailedScores;
    }

    public AssessmentResults getAssessmentResults() {
        return assessmentResults;
    }

    public void setAssessmentResults(AssessmentResults assessmentResults) {
        this.assessmentResults = assessmentResults;
    }

    public Boolean getNeedDoctorRecommendation() {
        return needDoctorRecommendation;
    }

    public void setNeedDoctorRecommendation(Boolean needDoctorRecommendation) {
        this.needDoctorRecommendation = needDoctorRecommendation;
    }

    public String getSevereDisease() {
        return severeDisease;
    }

    public void setSevereDisease(String severeDisease) {
        this.severeDisease = severeDisease;
    }

    public String getRecommendationDisease() {
        return recommendationDisease;
    }

    public void setRecommendationDisease(String recommendationDisease) {
        this.recommendationDisease = recommendationDisease;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static class DetailedScores {
        private Integer depression;
        private Integer schizophrenia;
        private Integer anxiety;
        private Integer insomnia;
        private Integer obsessive;

        public Integer getDepression() {
            return depression;
        }

        public void setDepression(Integer depression) {
            this.depression = depression;
        }

        public Integer getSchizophrenia() {
            return schizophrenia;
        }

        public void setSchizophrenia(Integer schizophrenia) {
            this.schizophrenia = schizophrenia;
        }

        public Integer getAnxiety() {
            return anxiety;
        }

        public void setAnxiety(Integer anxiety) {
            this.anxiety = anxiety;
        }

        public Integer getInsomnia() {
            return insomnia;
        }

        public void setInsomnia(Integer insomnia) {
            this.insomnia = insomnia;
        }

        public Integer getObsessive() {
            return obsessive;
        }

        public void setObsessive(Integer obsessive) {
            this.obsessive = obsessive;
        }
    }

    public static class AssessmentResults {
        private DiseaseResult depression;
        private DiseaseResult schizophrenia;
        private DiseaseResult anxiety;
        private DiseaseResult insomnia;
        private DiseaseResult obsessive;

        public DiseaseResult getDepression() {
            return depression;
        }

        public void setDepression(DiseaseResult depression) {
            this.depression = depression;
        }

        public DiseaseResult getSchizophrenia() {
            return schizophrenia;
        }

        public void setSchizophrenia(DiseaseResult schizophrenia) {
            this.schizophrenia = schizophrenia;
        }

        public DiseaseResult getAnxiety() {
            return anxiety;
        }

        public void setAnxiety(DiseaseResult anxiety) {
            this.anxiety = anxiety;
        }

        public DiseaseResult getInsomnia() {
            return insomnia;
        }

        public void setInsomnia(DiseaseResult insomnia) {
            this.insomnia = insomnia;
        }

        public DiseaseResult getObsessive() {
            return obsessive;
        }

        public void setObsessive(DiseaseResult obsessive) {
            this.obsessive = obsessive;
        }
    }

    public static class DiseaseResult {
        private String level;
        private String suggestion;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
}

