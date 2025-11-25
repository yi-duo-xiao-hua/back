package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.AssessmentResultVO;
import com.example.hello.dto.AssessmentSubmitDTO;
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
     * 提交评估
     */
    @PostMapping("/assessments")
    public Result<AssessmentResultVO> submitAssessment(@Valid @RequestBody AssessmentSubmitDTO submitDTO) {
        return scaleService.submitAssessment(submitDTO);
    }

    /**
     * 获取量表解读
     */
    @GetMapping("/scales/{id}/interpretation")
    public Result<InterpretationVO> getInterpretation(@PathVariable("id") Integer scaleId) {
        return scaleService.getInterpretation(scaleId);
    }
}



