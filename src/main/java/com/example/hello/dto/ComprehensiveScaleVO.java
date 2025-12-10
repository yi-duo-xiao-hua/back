package com.example.hello.dto;

import java.util.List;
import java.util.Map;

/**
 * 综合量表基础信息VO
 */
public class ComprehensiveScaleVO {

    private ScaleInfo scaleInfo;
    private List<String> diseaseTypes;
    private Map<String, DiseaseRule> interpretationRules;

    public ScaleInfo getScaleInfo() {
        return scaleInfo;
    }

    public void setScaleInfo(ScaleInfo scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    public List<String> getDiseaseTypes() {
        return diseaseTypes;
    }

    public void setDiseaseTypes(List<String> diseaseTypes) {
        this.diseaseTypes = diseaseTypes;
    }

    public Map<String, DiseaseRule> getInterpretationRules() {
        return interpretationRules;
    }

    public void setInterpretationRules(Map<String, DiseaseRule> interpretationRules) {
        this.interpretationRules = interpretationRules;
    }

    public static class ScaleInfo {
        private Integer scaleId;
        private String name;
        private String code;
        private String description;
        private Integer questionCount;

        public Integer getScaleId() {
            return scaleId;
        }

        public void setScaleId(Integer scaleId) {
            this.scaleId = scaleId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getQuestionCount() {
            return questionCount;
        }

        public void setQuestionCount(Integer questionCount) {
            this.questionCount = questionCount;
        }
    }

    public static class DiseaseRule {
        private List<ScoreRange> ranges;

        public List<ScoreRange> getRanges() {
            return ranges;
        }

        public void setRanges(List<ScoreRange> ranges) {
            this.ranges = ranges;
        }
    }

    public static class ScoreRange {
        private Integer min;
        private Integer max;
        private String level;
        private String suggestion;

        public Integer getMin() {
            return min;
        }

        public void setMin(Integer min) {
            this.min = min;
        }

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
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
    }
}

