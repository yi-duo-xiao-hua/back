package com.example.hello.service;

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

import java.util.List;

/**
 * 量表评估服务
 */
public interface ScaleService {

    /**
     * 获取全部量表
     */
    Result<List<ScaleListVO>> listScales();

    /**
     * 根据量表ID获取题目
     */
    Result<ScaleDetailVO> getScaleQuestions(Integer scaleId);

    /**
     * 提交评估结果
     */
    Result<AssessmentResultVO> submitAssessment(AssessmentSubmitDTO submitDTO);

    /**
     * 获取量表解读规则
     */
    Result<InterpretationVO> getInterpretation(Integer scaleId);

    /**
     * 获取综合量表基础信息（规则、病症列表）
     */
    Result<ComprehensiveScaleVO> getComprehensiveScale();

    /**
     * 提交综合量表评估
     */
    Result<ComprehensiveAssessmentResultVO> submitComprehensiveAssessment(
            ComprehensiveAssessmentSubmitDTO submitDTO);

    /**
     * 获取综合量表评估详情（雷达图）
     */
    Result<ComprehensiveAssessmentDetailVO> getComprehensiveAssessmentDetail(Integer assessmentId);
}



