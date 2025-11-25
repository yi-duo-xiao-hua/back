package com.example.hello.dto;

import java.util.List;

/**
 * 量表解读规则VO
 */
public class InterpretationVO {

    private InterpretationRules interpretationRules;

    public InterpretationRules getInterpretationRules() {
        return interpretationRules;
    }

    public void setInterpretationRules(InterpretationRules interpretationRules) {
        this.interpretationRules = interpretationRules;
    }

    public static class InterpretationRules {
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



