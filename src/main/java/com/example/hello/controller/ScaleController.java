package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.AssessmentResultVO;
import com.example.hello.dto.AssessmentSubmitDTO;
import com.example.hello.dto.ComprehensiveAssessmentDetailVO;
import com.example.hello.dto.ComprehensiveAssessmentResultVO;
import com.example.hello.dto.ComprehensiveAssessmentSubmitDTO;
import com.example.hello.dto.ComprehensiveScaleVO;
import com.example.hello.dto.InterpretationVO;
import com.example.hello.dto.ScaleDetailVO;
import com.example.hello.dto.ScaleListVO;
import com.example.hello.service.ScaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 量表评估控制器
 */
@RestController
public class ScaleController {

    @Autowired
    private ScaleService scaleService;

    /**
     * 获取量表列表
     */
    @GetMapping("/scales")
    public Result<List<ScaleListVO>> listScales() {
        return scaleService.listScales();
    }

    /**
     * 获取量表题目
     */
    @GetMapping("/scales/{id}/questions")
    public Result<ScaleDetailVO> getScaleQuestions(@PathVariable("id") Integer scaleId) {
        return scaleService.getScaleQuestions(scaleId);
    }

    /**
     * 获取综合量表规则及基础信息
     */
    @GetMapping("/scales/comprehensive")
    public Result<ComprehensiveScaleVO> getComprehensiveScale() {
        return scaleService.getComprehensiveScale();
    }

    /**
     * 提交评估
     */
    @PostMapping("/assessments")
    public Result<AssessmentResultVO> submitAssessment(@Valid @RequestBody AssessmentSubmitDTO submitDTO) {
        return scaleService.submitAssessment(submitDTO);
    }

    /**
     * 提交综合量表评估
     */
    @PostMapping("/assessments/comprehensive")
    public Result<ComprehensiveAssessmentResultVO> submitComprehensiveAssessment(
            @Valid @RequestBody ComprehensiveAssessmentSubmitDTO submitDTO) {
        return scaleService.submitComprehensiveAssessment(submitDTO);
    }

    /**
     * 获取综合量表评估详情（雷达图）
     */
    @GetMapping("/assessments/comprehensive/{id}")
    public Result<ComprehensiveAssessmentDetailVO> getComprehensiveAssessmentDetail(
            @PathVariable("id") Integer assessmentId) {
        return scaleService.getComprehensiveAssessmentDetail(assessmentId);
    }

    /**
     * 获取量表解读
     */
    @GetMapping("/scales/{id}/interpretation")
    public Result<InterpretationVO> getInterpretation(@PathVariable("id") Integer scaleId) {
        return scaleService.getInterpretation(scaleId);
    }
}



