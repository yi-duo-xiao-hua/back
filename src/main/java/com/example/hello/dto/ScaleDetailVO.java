package com.example.hello.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 量表题目详情VO
 */
public class ScaleDetailVO {
    private ScaleInfoVO scaleInfo;
    private List<QuestionVO> questions;

    public ScaleInfoVO getScaleInfo() {
        return scaleInfo;
    }

    public void setScaleInfo(ScaleInfoVO scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    public List<QuestionVO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionVO> questions) {
        this.questions = questions;
    }

    public static class ScaleInfoVO {
        private Integer scaleId;
        private String name;
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

    public static class QuestionVO {
        private Integer questionId;
        private Integer questionNumber;
        private String questionText;
        private String optionType;
        private List<OptionVO> options;
        private BigDecimal weight;

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
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

        public String getOptionType() {
            return optionType;
        }

        public void setOptionType(String optionType) {
            this.optionType = optionType;
        }

        public List<OptionVO> getOptions() {
            return options;
        }

        public void setOptions(List<OptionVO> options) {
            this.options = options;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public static class OptionVO {
            private String text;
            private Integer value;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public Integer getValue() {
                return value;
            }

            public void setValue(Integer value) {
                this.value = value;
            }
        }
    }
}



