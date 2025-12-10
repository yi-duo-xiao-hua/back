package com.example.hello.dto;

import java.util.List;

/**
 * 综合量表评估详情（用于雷达图）
 */
public class ComprehensiveAssessmentDetailVO {

    private Integer assessmentId;
    private String scaleName;
    private String assessmentDate;
    private RadarChartData radarChartData;
    private List<DetailedResult> detailedResults;

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

    public String getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public RadarChartData getRadarChartData() {
        return radarChartData;
    }

    public void setRadarChartData(RadarChartData radarChartData) {
        this.radarChartData = radarChartData;
    }

    public List<DetailedResult> getDetailedResults() {
        return detailedResults;
    }

    public void setDetailedResults(List<DetailedResult> detailedResults) {
        this.detailedResults = detailedResults;
    }

    public static class RadarChartData {
        private List<String> labels;
        private List<Integer> scores;
        private List<Integer> maxScores;
        private List<String> levels;

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Integer> getScores() {
            return scores;
        }

        public void setScores(List<Integer> scores) {
            this.scores = scores;
        }

        public List<Integer> getMaxScores() {
            return maxScores;
        }

        public void setMaxScores(List<Integer> maxScores) {
            this.maxScores = maxScores;
        }

        public List<String> getLevels() {
            return levels;
        }

        public void setLevels(List<String> levels) {
            this.levels = levels;
        }
    }

    public static class DetailedResult {
        private String disease;
        private Integer score;
        private String level;
        private String suggestion;
        private Integer maxScore;

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

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

        public Integer getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(Integer maxScore) {
            this.maxScore = maxScore;
        }
    }
}

