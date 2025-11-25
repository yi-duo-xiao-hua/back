package com.example.hello.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 提交评估请求DTO
 */
public class AssessmentSubmitDTO {

    @NotNull(message = "患者ID不能为空")
    @Min(value = 1, message = "患者ID必须大于0")
    private Integer patientId;

    @NotNull(message = "量表ID不能为空")
    @Min(value = 1, message = "量表ID必须大于0")
    private Integer scaleId;

    @NotEmpty(message = "答题记录不能为空")
    @Valid
    private List<AnswerDTO> answers;

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

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    public static class AnswerDTO {
        @NotNull(message = "题目ID不能为空")
        @Min(value = 1, message = "题目ID必须大于0")
        private Integer questionId;

        @NotNull(message = "选择分值不能为空")
        private Integer selectedValue;

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public Integer getSelectedValue() {
            return selectedValue;
        }

        public void setSelectedValue(Integer selectedValue) {
            this.selectedValue = selectedValue;
        }
    }
}



