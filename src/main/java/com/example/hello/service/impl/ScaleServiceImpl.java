package com.example.hello.service.impl;

import com.example.hello.common.Result;
import com.example.hello.dto.AssessmentResultVO;
import com.example.hello.dto.AssessmentSubmitDTO;
import com.example.hello.dto.InterpretationVO;
import com.example.hello.dto.ScaleDetailVO;
import com.example.hello.dto.ScaleListVO;
import com.example.hello.entity.Assessment;
import com.example.hello.entity.Scale;
import com.example.hello.entity.ScaleQuestion;
import com.example.hello.mapper.AssessmentMapper;
import com.example.hello.mapper.PatientMapper;
import com.example.hello.mapper.ScaleMapper;
import com.example.hello.mapper.ScaleQuestionMapper;
import com.example.hello.service.ScaleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 量表服务实现
 */
@Service
public class ScaleServiceImpl implements ScaleService {

    private static final List<String> SEVERE_KEYWORDS = List.of("重度", "重性", "严重", "高危");
    private static final String DOCTOR_REDIRECT_URL = "/doctors?department=精神心理科";

    @Autowired
    private ScaleMapper scaleMapper;

    @Autowired
    private ScaleQuestionMapper scaleQuestionMapper;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private PatientMapper patientMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result<List<ScaleListVO>> listScales() {
        List<Scale> scales = scaleMapper.selectAll();
        List<ScaleListVO> list = scales.stream().map(scale -> {
            ScaleListVO vo = new ScaleListVO();
            vo.setScaleId(scale.getScaleId());
            vo.setName(scale.getName());
            vo.setCode(scale.getCode());
            vo.setDescription(scale.getDescription());
            vo.setDiseaseType(scale.getDiseaseType());
            vo.setQuestionCount(scale.getQuestionCount());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<ScaleDetailVO> getScaleQuestions(Integer scaleId) {
        if (scaleId == null || scaleId <= 0) {
            return Result.error("量表ID不能为空");
        }
        Scale scale = scaleMapper.selectById(scaleId);
        if (scale == null) {
            return Result.error("量表不存在");
        }
        List<ScaleQuestion> questions = scaleQuestionMapper.selectByScaleId(scaleId);
        ScaleDetailVO detailVO = new ScaleDetailVO();
        ScaleDetailVO.ScaleInfoVO infoVO = new ScaleDetailVO.ScaleInfoVO();
        infoVO.setScaleId(scale.getScaleId());
        infoVO.setName(scale.getName());
        infoVO.setDescription(scale.getDescription());
        infoVO.setQuestionCount(scale.getQuestionCount());
        detailVO.setScaleInfo(infoVO);
        List<ScaleDetailVO.QuestionVO> questionVOS = questions.stream()
            .sorted(Comparator.comparing(ScaleQuestion::getQuestionNumber))
            .map(this::convertQuestion)
            .collect(Collectors.toList());
        detailVO.setQuestions(questionVOS);
        return Result.success(detailVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AssessmentResultVO> submitAssessment(AssessmentSubmitDTO submitDTO) {
        if (submitDTO == null) {
            return Result.error("请求参数不能为空");
        }
        // 校验患者
        if (submitDTO.getPatientId() == null || submitDTO.getPatientId() <= 0) {
            return Result.error("患者ID不能为空");
        }
        int exists = patientMapper.checkPatientExists(submitDTO.getPatientId());
        if (exists == 0) {
            return Result.error("患者不存在");
        }
        // 校验量表
        Scale scale = scaleMapper.selectById(submitDTO.getScaleId());
        if (scale == null) {
            return Result.error("量表不存在");
        }
        List<ScaleQuestion> questions = scaleQuestionMapper.selectByScaleId(scale.getScaleId());
        if (questions.isEmpty()) {
            return Result.error("量表暂无配置题目");
        }
        Map<Integer, ScaleQuestion> questionMap = questions.stream()
            .collect(Collectors.toMap(ScaleQuestion::getQuestionId, q -> q));
        // 校验答题
        List<AssessmentSubmitDTO.AnswerDTO> answers = submitDTO.getAnswers();
        if (answers == null || answers.isEmpty()) {
            return Result.error("答题记录不能为空");
        }
        Set<Integer> uniqueQuestionIds = answers.stream()
            .map(AssessmentSubmitDTO.AnswerDTO::getQuestionId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (uniqueQuestionIds.size() != answers.size()) {
            return Result.error("存在重复的题目作答");
        }
        if (answers.size() != scale.getQuestionCount()) {
            return Result.error("答题数量与量表题目数量不一致");
        }
        // 计算得分
        BigDecimal totalScoreDecimal = BigDecimal.ZERO;
        List<Map<String, Object>> answerSnapshots = new ArrayList<>();
        for (AssessmentSubmitDTO.AnswerDTO answerDTO : answers) {
            ScaleQuestion question = questionMap.get(answerDTO.getQuestionId());
            if (question == null || !Objects.equals(question.getScaleId(), scale.getScaleId())) {
                return Result.error("存在不属于当前量表的题目");
            }
            if (answerDTO.getSelectedValue() == null) {
                return Result.error("题目选择分值不能为空");
            }
            BigDecimal weight = question.getWeight() == null ? BigDecimal.ONE : question.getWeight();
            BigDecimal score = BigDecimal.valueOf(answerDTO.getSelectedValue()).multiply(weight);
            totalScoreDecimal = totalScoreDecimal.add(score);

            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("questionId", question.getQuestionId());
            snapshot.put("questionNumber", question.getQuestionNumber());
            snapshot.put("questionText", question.getQuestionText());
            snapshot.put("selectedValue", answerDTO.getSelectedValue());
            answerSnapshots.add(snapshot);
        }
        int totalScore = totalScoreDecimal.setScale(0, RoundingMode.HALF_UP).intValue();
        InterpretationVO.InterpretationRules rules = parseInterpretationRules(scale.getInterpretationRules());
        InterpretationVO.ScoreRange matchedRange = matchRange(totalScore, rules);

        String assessmentResult = matchedRange != null ? matchedRange.getLevel() : "未定义";
        String recommendation = matchedRange != null ? matchedRange.getSuggestion() : "建议咨询专业医生";
        boolean needDoctorRecommendation = matchedRange != null && isSevereRange(matchedRange);
        String redirectUrl = needDoctorRecommendation ? DOCTOR_REDIRECT_URL : null;

        Assessment assessment = new Assessment();
        assessment.setPatientId(submitDTO.getPatientId());
        assessment.setScaleId(scale.getScaleId());
        assessment.setTotalScore(totalScore);
        assessment.setAssessmentResult(assessmentResult);
        assessment.setRecommendation(recommendation);
        assessment.setAnswers(writeJson(answerSnapshots));
        assessmentMapper.insertAssessment(assessment);

        AssessmentResultVO resultVO = new AssessmentResultVO();
        resultVO.setAssessmentId(assessment.getAssessmentId());
        resultVO.setTotalScore(totalScore);
        resultVO.setAssessmentResult(assessmentResult);
        resultVO.setRecommendation(recommendation);
        resultVO.setNeedDoctorRecommendation(needDoctorRecommendation);
        resultVO.setRedirectUrl(redirectUrl);
        return Result.success("评估完成", resultVO);
    }

    @Override
    public Result<InterpretationVO> getInterpretation(Integer scaleId) {
        if (scaleId == null || scaleId <= 0) {
            return Result.error("量表ID不能为空");
        }
        Scale scale = scaleMapper.selectById(scaleId);
        if (scale == null) {
            return Result.error("量表不存在");
        }
        InterpretationVO interpretationVO = new InterpretationVO();
        InterpretationVO.InterpretationRules rules = parseInterpretationRules(scale.getInterpretationRules());
        if (rules == null) {
            rules = new InterpretationVO.InterpretationRules();
            rules.setRanges(Collections.emptyList());
        }
        interpretationVO.setInterpretationRules(rules);
        return Result.success(interpretationVO);
    }

    private ScaleDetailVO.QuestionVO convertQuestion(ScaleQuestion question) {
        ScaleDetailVO.QuestionVO vo = new ScaleDetailVO.QuestionVO();
        vo.setQuestionId(question.getQuestionId());
        vo.setQuestionNumber(question.getQuestionNumber());
        vo.setQuestionText(question.getQuestionText());
        vo.setOptionType(question.getOptionType());
        vo.setWeight(question.getWeight());
        vo.setOptions(parseOptions(question.getOptions()));
        return vo;
    }

    private List<ScaleDetailVO.QuestionVO.OptionVO> parseOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(optionsJson, new TypeReference<List<ScaleDetailVO.QuestionVO.OptionVO>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private InterpretationVO.InterpretationRules parseInterpretationRules(String rulesJson) {
        if (rulesJson == null || rulesJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(rulesJson, InterpretationVO.InterpretationRules.class);
        } catch (Exception e) {
            return null;
        }
    }

    private InterpretationVO.ScoreRange matchRange(int totalScore, InterpretationVO.InterpretationRules rules) {
        if (rules == null || rules.getRanges() == null) {
            return null;
        }
        return rules.getRanges().stream()
            .filter(range -> range.getMin() != null && range.getMax() != null)
            .filter(range -> totalScore >= range.getMin() && totalScore <= range.getMax())
            .findFirst()
            .orElse(null);
    }

    private boolean isSevereRange(InterpretationVO.ScoreRange range) {
        if (range == null || range.getLevel() == null) {
            return false;
        }
        String level = range.getLevel().toLowerCase(Locale.ROOT);
        boolean levelSevere = SEVERE_KEYWORDS.stream().anyMatch(keyword -> level.contains(keyword.toLowerCase(Locale.ROOT)));
        if (levelSevere) {
            return true;
        }
        String suggestion = range.getSuggestion();
        if (suggestion == null) {
            return false;
        }
        return suggestion.contains("立即") || suggestion.contains("就医") || suggestion.contains("医生");
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}


