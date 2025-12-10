package com.example.hello.service.impl;

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
import com.example.hello.entity.Assessment;
import com.example.hello.entity.Scale;
import com.example.hello.entity.ScaleQuestion;
import com.example.hello.mapper.AssessmentMapper;
import com.example.hello.mapper.PatientMapper;
import com.example.hello.mapper.ScaleMapper;
import com.example.hello.mapper.ScaleQuestionMapper;
import com.example.hello.service.ScaleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private static final String COMPREHENSIVE_CODE = "COMPREHENSIVE";
    private static final List<String> DISEASE_ORDER = List.of("depression", "schizophrenia", "anxiety", "insomnia", "obsessive");
    private static final Map<String, String> DISEASE_NAME_MAP = Map.of(
        "depression", "抑郁症",
        "schizophrenia", "精神分裂症",
        "anxiety", "焦虑症",
        "insomnia", "失眠症",
        "obsessive", "强迫症"
    );
    private static final Map<String, List<String>> DISEASE_KEYWORDS = Map.of(
        "depression", List.of("抑郁"),
        "schizophrenia", List.of("分裂"),
        "anxiety", List.of("焦虑"),
        "insomnia", List.of("失眠", "睡眠"),
        "obsessive", List.of("强迫")
    );
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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

    @Override
    public Result<ComprehensiveScaleVO> getComprehensiveScale() {
        Scale scale = scaleMapper.selectByCode(COMPREHENSIVE_CODE);
        if (scale == null) {
            return Result.error("综合量表不存在，请先配置量表（code=COMPREHENSIVE）");
        }
        ComprehensiveScaleVO vo = new ComprehensiveScaleVO();
        ComprehensiveScaleVO.ScaleInfo info = new ComprehensiveScaleVO.ScaleInfo();
        info.setScaleId(scale.getScaleId());
        info.setName(scale.getName());
        info.setCode(scale.getCode());
        info.setDescription(scale.getDescription());
        info.setQuestionCount(scale.getQuestionCount());
        vo.setScaleInfo(info);
        vo.setDiseaseTypes(DISEASE_ORDER.stream().map(DISEASE_NAME_MAP::get).collect(Collectors.toList()));
        vo.setInterpretationRules(parseComprehensiveRules(scale.getInterpretationRules()));
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ComprehensiveAssessmentResultVO> submitComprehensiveAssessment(ComprehensiveAssessmentSubmitDTO submitDTO) {
        if (submitDTO == null) {
            return Result.error("请求参数不能为空");
        }
        if (submitDTO.getPatientId() == null || submitDTO.getPatientId() <= 0) {
            return Result.error("患者ID不能为空");
        }
        int exists = patientMapper.checkPatientExists(submitDTO.getPatientId());
        if (exists == 0) {
            return Result.error("患者不存在");
        }
        Scale scale = scaleMapper.selectById(submitDTO.getScaleId());
        if (scale == null) {
            return Result.error("量表不存在");
        }
        if (!COMPREHENSIVE_CODE.equalsIgnoreCase(scale.getCode())) {
            return Result.error("量表ID不是综合量表");
        }
        List<ScaleQuestion> questions = scaleQuestionMapper.selectByScaleId(scale.getScaleId());
        if (questions.isEmpty()) {
            return Result.error("量表暂无配置题目");
        }
        Map<Integer, ScaleQuestion> questionMap = questions.stream()
            .collect(Collectors.toMap(ScaleQuestion::getQuestionId, q -> q));
        List<ComprehensiveAssessmentSubmitDTO.AnswerDTO> answers = submitDTO.getAnswers();
        if (answers == null || answers.isEmpty()) {
            return Result.error("答题记录不能为空");
        }
        Set<Integer> uniqueIds = answers.stream()
            .map(ComprehensiveAssessmentSubmitDTO.AnswerDTO::getQuestionId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (uniqueIds.size() != answers.size()) {
            return Result.error("存在重复的题目作答");
        }
        if (!Objects.equals(questions.size(), answers.size())) {
            return Result.error("答题数量与量表题目数量不一致");
        }

        Map<String, Integer> diseaseScore = new LinkedHashMap<>();
        DISEASE_ORDER.forEach(d -> diseaseScore.put(d, 0));

        List<Map<String, Object>> snapshots = new ArrayList<>();
        for (ComprehensiveAssessmentSubmitDTO.AnswerDTO dto : answers) {
            ScaleQuestion q = questionMap.get(dto.getQuestionId());
            if (q == null || !Objects.equals(q.getScaleId(), scale.getScaleId())) {
                return Result.error("存在不属于当前量表的题目");
            }
            if (dto.getSelectedValue() == null || dto.getSelectedValue() < 1 || dto.getSelectedValue() > 5) {
                return Result.error("选择的选项索引必须在1-5之间");
            }
            BigDecimal weight = q.getWeight() == null ? BigDecimal.ONE : q.getWeight();
            ComprehensiveOptionSelection selection = parseComprehensiveOption(q.getOptions(), dto.getSelectedValue());
            if (selection == null) {
                return Result.error("题目选项配置错误或索引超出范围");
            }
            Map<String, Integer> optionValues = selection.values();
            for (String key : DISEASE_ORDER) {
                int base = optionValues.getOrDefault(key, 0);
                int delta = BigDecimal.valueOf(base)
                    .multiply(weight)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
                diseaseScore.computeIfPresent(key, (k, v) -> v + delta);
            }

            Map<String, Object> snap = new HashMap<>();
            snap.put("questionId", q.getQuestionId());
            snap.put("questionNumber", q.getQuestionNumber());
            snap.put("questionText", q.getQuestionText());
            snap.put("selectedValue", dto.getSelectedValue());
            snap.put("selectedText", selection.text());
            snap.put("values", optionValues);
            snapshots.add(snap);
        }

        Map<String, Integer> maxScores = calcComprehensiveMaxScores(questions);
        Map<String, Integer> normalizedScores = normalizeScores(diseaseScore, maxScores);
        int totalScore = normalizedScores.values().stream().mapToInt(Integer::intValue).sum();
        Map<String, ComprehensiveScaleVO.DiseaseRule> rules = parseComprehensiveRules(scale.getInterpretationRules());
        Map<String, ComprehensiveAssessmentResultVO.DiseaseResult> assessmentResults = new LinkedHashMap<>();
        Map<String, String> diseaseLevels = new LinkedHashMap<>();
        Map<String, String> diseaseSuggestions = new LinkedHashMap<>();
        for (String key : DISEASE_ORDER) {
            int score = normalizedScores.getOrDefault(key, 0);
            ComprehensiveAssessmentResultVO.DiseaseResult result = new ComprehensiveAssessmentResultVO.DiseaseResult();
            ComprehensiveScaleVO.DiseaseRule rule = rules.get(key);
            ComprehensiveScaleVO.ScoreRange matched = matchDiseaseRange(score, rule, key);
            String level = matched != null && matched.getLevel() != null ? matched.getLevel() : "未定义";
            String suggestion = matched != null && matched.getSuggestion() != null ? matched.getSuggestion() : "建议咨询专业医生";
            result.setLevel(level);
            result.setSuggestion(suggestion);
            assessmentResults.put(key, result);
            diseaseLevels.put(key, level);
            diseaseSuggestions.put(key, suggestion);
        }

        String severeDiseaseKey = findSevereDisease(diseaseLevels);
        boolean needDoctorRecommendation = severeDiseaseKey != null;
        String redirectUrl = needDoctorRecommendation ? "/doctors/recommendation?disease=" + severeDiseaseKey : null;

        Assessment assessment = new Assessment();
        assessment.setPatientId(submitDTO.getPatientId());
        assessment.setScaleId(scale.getScaleId());
        assessment.setTotalScore(totalScore);
        assessment.setAssessmentResult(buildAssessmentResultText(diseaseLevels));
        assessment.setRecommendation(needDoctorRecommendation ? diseaseSuggestions.get(severeDiseaseKey) : null);
        assessment.setAnswers(writeJson(snapshots));
        assessment.setDepressionScore(normalizedScores.getOrDefault("depression", 0));
        assessment.setSchizophreniaScore(normalizedScores.getOrDefault("schizophrenia", 0));
        assessment.setAnxietyScore(normalizedScores.getOrDefault("anxiety", 0));
        assessment.setInsomniaScore(normalizedScores.getOrDefault("insomnia", 0));
        assessment.setObsessiveScore(normalizedScores.getOrDefault("obsessive", 0));
        assessmentMapper.insertAssessment(assessment);

        ComprehensiveAssessmentResultVO.DetailedScores detailedScores = new ComprehensiveAssessmentResultVO.DetailedScores();
        detailedScores.setDepression(assessment.getDepressionScore());
        detailedScores.setSchizophrenia(assessment.getSchizophreniaScore());
        detailedScores.setAnxiety(assessment.getAnxietyScore());
        detailedScores.setInsomnia(assessment.getInsomniaScore());
        detailedScores.setObsessive(assessment.getObsessiveScore());

        ComprehensiveAssessmentResultVO resultVO = new ComprehensiveAssessmentResultVO();
        resultVO.setAssessmentId(assessment.getAssessmentId());
        resultVO.setTotalScore(totalScore);
        resultVO.setDetailedScores(detailedScores);
        ComprehensiveAssessmentResultVO.AssessmentResults ar = new ComprehensiveAssessmentResultVO.AssessmentResults();
        ar.setDepression(assessmentResults.get("depression"));
        ar.setSchizophrenia(assessmentResults.get("schizophrenia"));
        ar.setAnxiety(assessmentResults.get("anxiety"));
        ar.setInsomnia(assessmentResults.get("insomnia"));
        ar.setObsessive(assessmentResults.get("obsessive"));
        resultVO.setAssessmentResults(ar);
        resultVO.setNeedDoctorRecommendation(needDoctorRecommendation);
        resultVO.setSevereDisease(severeDiseaseKey);
        resultVO.setRecommendationDisease(severeDiseaseKey != null ? DISEASE_NAME_MAP.get(severeDiseaseKey) : null);
        resultVO.setRedirectUrl(redirectUrl);
        return Result.success("综合评估完成", resultVO);
    }

    @Override
    public Result<ComprehensiveAssessmentDetailVO> getComprehensiveAssessmentDetail(Integer assessmentId) {
        if (assessmentId == null || assessmentId <= 0) {
            return Result.error("评估记录ID不能为空");
        }
        Assessment assessment = assessmentMapper.selectByAssessmentId(assessmentId);
        if (assessment == null) {
            return Result.error("评估记录不存在");
        }
        Scale scale = scaleMapper.selectById(assessment.getScaleId());
        if (scale == null || !COMPREHENSIVE_CODE.equalsIgnoreCase(scale.getCode())) {
            return Result.error("该评估记录不是综合量表");
        }
        Map<String, Integer> scores = Map.of(
            "depression", safeScore(assessment.getDepressionScore()),
            "schizophrenia", safeScore(assessment.getSchizophreniaScore()),
            "anxiety", safeScore(assessment.getAnxietyScore()),
            "insomnia", safeScore(assessment.getInsomniaScore()),
            "obsessive", safeScore(assessment.getObsessiveScore())
        );
        Map<String, ComprehensiveScaleVO.DiseaseRule> rules = parseComprehensiveRules(scale.getInterpretationRules());
        List<ScaleQuestion> questions = scaleQuestionMapper.selectByScaleId(scale.getScaleId());
        Map<String, Integer> maxScoresMap = calcComprehensiveMaxScores(questions);
        ComprehensiveAssessmentDetailVO vo = new ComprehensiveAssessmentDetailVO();
        vo.setAssessmentId(assessment.getAssessmentId());
        vo.setScaleName(scale.getName());
        vo.setAssessmentDate(assessment.getAssessmentDate() == null ? null : assessment.getAssessmentDate().format(DATETIME_FORMATTER));

        List<String> labels = new ArrayList<>();
        List<Integer> scoreList = new ArrayList<>();
        List<Integer> maxScores = new ArrayList<>();
        List<String> levels = new ArrayList<>();
        List<ComprehensiveAssessmentDetailVO.DetailedResult> detailedResults = new ArrayList<>();
        for (String key : DISEASE_ORDER) {
            int sc = scores.getOrDefault(key, 0);
            labels.add(DISEASE_NAME_MAP.get(key));
            scoreList.add(sc);
            maxScores.add(100);
            ComprehensiveScaleVO.DiseaseRule rule = rules.get(key);
            ComprehensiveScaleVO.ScoreRange matched = matchDiseaseRange(sc, rule, key);
            String level = matched != null && matched.getLevel() != null ? matched.getLevel() : "未定义";
            String suggestion = matched != null && matched.getSuggestion() != null ? matched.getSuggestion() : "建议咨询专业医生";
            levels.add(level);

            ComprehensiveAssessmentDetailVO.DetailedResult item = new ComprehensiveAssessmentDetailVO.DetailedResult();
            item.setDisease(DISEASE_NAME_MAP.get(key));
            item.setScore(sc);
            item.setLevel(level);
            item.setSuggestion(suggestion);
            item.setMaxScore(100);
            detailedResults.add(item);
        }
        ComprehensiveAssessmentDetailVO.RadarChartData radar = new ComprehensiveAssessmentDetailVO.RadarChartData();
        radar.setLabels(labels);
        radar.setScores(scoreList);
        radar.setMaxScores(maxScores);
        radar.setLevels(levels);
        vo.setRadarChartData(radar);
        vo.setDetailedResults(detailedResults);
        return Result.success(vo);
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
            JsonNode root = objectMapper.readTree(optionsJson);
            if (root == null) {
                return Collections.emptyList();
            }
            JsonNode optionsNode = root;
            if (root.isObject() && root.has("options")) {
                optionsNode = root.get("options");
            }
            if (optionsNode == null || !optionsNode.isArray()) {
                return Collections.emptyList();
            }
            List<ScaleDetailVO.QuestionVO.OptionVO> options = new ArrayList<>();
            optionsNode.forEach(node -> {
                ScaleDetailVO.QuestionVO.OptionVO optionVO = new ScaleDetailVO.QuestionVO.OptionVO();
                optionVO.setText(node.path("text").asText(null));
                if (node.has("value")) {
                    if (node.get("value").isNumber()) {
                        optionVO.setValue(node.get("value").asInt());
                    } else {
                        try {
                            optionVO.setValue(Integer.parseInt(node.get("value").asText()));
                        } catch (NumberFormatException e) {
                            optionVO.setValue(null);
                        }
                    }
                }
                options.add(optionVO);
            });
            return options;
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

    private ComprehensiveScaleVO.ScoreRange matchDiseaseRange(int score, ComprehensiveScaleVO.DiseaseRule rule, String diseaseKey) {
        List<ComprehensiveScaleVO.ScoreRange> ranges = rule != null ? rule.getRanges() : null;
        if (ranges == null || ranges.isEmpty()) {
            ComprehensiveScaleVO.DiseaseRule fallback = defaultComprehensiveRules().get(diseaseKey);
            ranges = fallback != null ? fallback.getRanges() : Collections.emptyList();
        }
        return ranges.stream()
            .filter(r -> r.getMin() != null && r.getMax() != null)
            .filter(r -> score >= r.getMin() && score <= r.getMax())
            .findFirst()
            .orElse(null);
    }

    private Map<String, ComprehensiveScaleVO.DiseaseRule> parseComprehensiveRules(String rulesJson) {
        Map<String, ComprehensiveScaleVO.DiseaseRule> parsed = null;
        JsonNode rootNode = null;
        if (rulesJson != null && !rulesJson.isBlank()) {
            try {
                parsed = objectMapper.readValue(rulesJson, new TypeReference<Map<String, ComprehensiveScaleVO.DiseaseRule>>(){});
            } catch (Exception ignored) {
                parsed = null;
            }
            try {
                rootNode = objectMapper.readTree(rulesJson);
            } catch (Exception ignored) {
                rootNode = null;
            }
        }
        Map<String, ComprehensiveScaleVO.DiseaseRule> defaults = defaultComprehensiveRules();
        Map<String, ComprehensiveScaleVO.DiseaseRule> result = parsed == null ? new LinkedHashMap<>() : new LinkedHashMap<>(parsed);

        // 兼容 *_ranges 写法，无论 parsed 是否成功都尝试
        for (String key : DISEASE_ORDER) {
            if (result.containsKey(key) && result.get(key) != null && result.get(key).getRanges() != null) {
                continue;
            }
            String alt = key + "_ranges";
            if (rootNode != null && rootNode.has(alt) && rootNode.get(alt).isArray()) {
                List<ComprehensiveScaleVO.ScoreRange> list = new ArrayList<>();
                rootNode.get(alt).forEach(r -> {
                    ComprehensiveScaleVO.ScoreRange sr = new ComprehensiveScaleVO.ScoreRange();
                    sr.setMin(r.path("min").isNumber() ? r.get("min").asInt() : null);
                    sr.setMax(r.path("max").isNumber() ? r.get("max").asInt() : null);
                    sr.setLevel(r.path("level").asText(null));
                    sr.setSuggestion(r.path("suggestion").asText(null));
                    list.add(sr);
                });
                ComprehensiveScaleVO.DiseaseRule dr = new ComprehensiveScaleVO.DiseaseRule();
                dr.setRanges(list);
                result.put(key, dr);
            }
        }

        // 对缺失的病种使用默认规则
        for (String key : DISEASE_ORDER) {
            if (!result.containsKey(key)
                || result.get(key) == null
                || result.get(key).getRanges() == null
                || result.get(key).getRanges().isEmpty()) {
                result.put(key, defaults.get(key));
            }
        }
        return result;
    }

    private Map<String, ComprehensiveScaleVO.DiseaseRule> defaultComprehensiveRules() {
        Map<String, ComprehensiveScaleVO.DiseaseRule> map = new LinkedHashMap<>();
        // 默认按 0-100 区间，适配归一化后的得分
        int[][] ranges = new int[][]{{0,25},{26,50},{51,75},{76,100}};
        String[] levels = new String[]{"正常","轻度","中度","重度"};
        map.put("depression", buildRule(ranges, levels,
            new String[]{"无明显抑郁症状","建议自我调节","建议咨询专业医生","建议立即就医治疗"}));
        map.put("schizophrenia", buildRule(ranges, levels,
            new String[]{"无明显症状","建议关注心理健康","建议专业评估","建议立即就医"}));
        map.put("anxiety", buildRule(ranges, levels,
            new String[]{"无明显焦虑症状","建议放松训练","建议寻求专业帮助","建议立即就医评估"}));
        map.put("insomnia", buildRule(ranges, levels,
            new String[]{"睡眠质量良好","建议改善睡眠习惯","建议睡眠治疗","建议专业睡眠治疗"}));
        map.put("obsessive", buildRule(ranges, levels,
            new String[]{"无明显强迫症状","建议自我调节","建议专业咨询","建议立即就医治疗"}));
        return map;
    }

    private ComprehensiveScaleVO.DiseaseRule buildRule(int[][] ranges, String[] levels, String[] suggestions) {
        ComprehensiveScaleVO.DiseaseRule rule = new ComprehensiveScaleVO.DiseaseRule();
        List<ComprehensiveScaleVO.ScoreRange> list = new ArrayList<>();
        for (int i = 0; i < ranges.length; i++) {
            ComprehensiveScaleVO.ScoreRange sr = new ComprehensiveScaleVO.ScoreRange();
            sr.setMin(ranges[i][0]);
            sr.setMax(ranges[i][1]);
            sr.setLevel(levels[i]);
            sr.setSuggestion(suggestions[i]);
            list.add(sr);
        }
        rule.setRanges(list);
        return rule;
    }

    private ComprehensiveOptionSelection parseComprehensiveOption(String optionsJson, int selectedIndex) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(optionsJson);
            JsonNode optionsNode = root;
            if (root.isObject() && root.has("options")) {
                optionsNode = root.get("options");
            }
            if (optionsNode == null || !optionsNode.isArray()) {
                return null;
            }
            int idx = selectedIndex - 1;
            if (idx < 0 || idx >= optionsNode.size()) {
                return null;
            }
            JsonNode optionNode = optionsNode.get(idx);
            String text = optionNode.path("text").asText(null);
            JsonNode valuesNode = optionNode.get("values");
            Map<String, Integer> values = new LinkedHashMap<>();
            if (valuesNode != null && valuesNode.isObject()) {
                valuesNode.fields().forEachRemaining(entry -> {
                    if (entry.getValue().isNumber()) {
                        values.put(entry.getKey(), entry.getValue().asInt());
                    } else {
                        try {
                            values.put(entry.getKey(), Integer.parseInt(entry.getValue().asText()));
                        } catch (NumberFormatException e) {
                            values.put(entry.getKey(), 0);
                        }
                    }
                });
            }
            return new ComprehensiveOptionSelection(text, values);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Integer> calcComprehensiveMaxScores(List<ScaleQuestion> questions) {
        Map<String, Integer> maxScores = new LinkedHashMap<>();
        DISEASE_ORDER.forEach(d -> maxScores.put(d, 0));
        if (questions == null || questions.isEmpty()) {
            return maxScores;
        }
        for (ScaleQuestion q : questions) {
            BigDecimal weight = q.getWeight() == null ? BigDecimal.ONE : q.getWeight();
            Map<String, Integer> bestPerDisease = findMaxPerDiseaseForQuestion(q.getOptions());
            for (String key : DISEASE_ORDER) {
                int base = bestPerDisease.getOrDefault(key, 0);
                int delta = BigDecimal.valueOf(base)
                    .multiply(weight)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
                maxScores.computeIfPresent(key, (k, v) -> v + delta);
            }
        }
        return maxScores;
    }

    private Map<String, Integer> normalizeScores(Map<String, Integer> rawScores, Map<String, Integer> maxScores) {
        Map<String, Integer> normalized = new LinkedHashMap<>();
        for (String key : DISEASE_ORDER) {
            int raw = rawScores.getOrDefault(key, 0);
            int max = Math.max(1, maxScores.getOrDefault(key, 0));
            int norm = BigDecimal.valueOf(raw)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(max), 0, RoundingMode.HALF_UP)
                .intValue();
            normalized.put(key, norm);
        }
        return normalized;
    }

    private ComprehensiveOptionSelection findMaxOptionForQuestion(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(optionsJson);
            JsonNode optionsNode = root;
            if (root.isObject() && root.has("options")) {
                optionsNode = root.get("options");
            }
            if (optionsNode == null || !optionsNode.isArray()) {
                return null;
            }
            ComprehensiveOptionSelection maxSel = null;
            int maxSum = Integer.MIN_VALUE;
            for (JsonNode opt : optionsNode) {
                JsonNode valuesNode = opt.get("values");
                int sum = 0;
                Map<String, Integer> values = new LinkedHashMap<>();
                if (valuesNode != null && valuesNode.isObject()) {
                    for (String key : DISEASE_ORDER) {
                        int v = valuesNode.path(key).isNumber() ? valuesNode.get(key).asInt() : 0;
                        values.put(key, v);
                        sum += v;
                    }
                }
                if (sum > maxSum) {
                    maxSum = sum;
                    maxSel = new ComprehensiveOptionSelection(opt.path("text").asText(null), values);
                }
            }
            return maxSel;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Integer> findMaxPerDiseaseForQuestion(String optionsJson) {
        Map<String, Integer> maxMap = new LinkedHashMap<>();
        DISEASE_ORDER.forEach(d -> maxMap.put(d, 0));
        if (optionsJson == null || optionsJson.isBlank()) {
            return maxMap;
        }
        try {
            JsonNode root = objectMapper.readTree(optionsJson);
            JsonNode optionsNode = root;
            if (root.isObject() && root.has("options")) {
                optionsNode = root.get("options");
            }
            if (optionsNode == null || !optionsNode.isArray()) {
                return maxMap;
            }
            for (JsonNode opt : optionsNode) {
                JsonNode valuesNode = opt.get("values");
                if (valuesNode != null && valuesNode.isObject()) {
                    for (String key : DISEASE_ORDER) {
                        int v = valuesNode.path(key).isNumber() ? valuesNode.get(key).asInt() : 0;
                        if (v > maxMap.getOrDefault(key, 0)) {
                            maxMap.put(key, v);
                        }
                    }
                }
            }
            return maxMap;
        } catch (Exception e) {
            return maxMap;
        }
    }

    private String findSevereDisease(Map<String, String> diseaseLevels) {
        Map<String, Integer> rank = Map.of(
            "正常", 1,
            "轻度", 2,
            "中度", 3,
            "重度", 4
        );
        String severe = null;
        int maxRank = 0;
        for (String key : DISEASE_ORDER) {
            String level = diseaseLevels.get(key);
            int r = rank.getOrDefault(level, 0);
            if (r > maxRank) {
                maxRank = r;
                severe = key;
            }
        }
        return maxRank >= 4 ? severe : null;
    }

    private String buildAssessmentResultText(Map<String, String> diseaseLevels) {
        return diseaseLevels.entrySet().stream()
            .map(e -> DISEASE_NAME_MAP.getOrDefault(e.getKey(), e.getKey()) + ":" + e.getValue())
            .collect(Collectors.joining(";"));
    }

    private int safeScore(Integer val) {
        return val == null ? 0 : val;
    }

    private record ComprehensiveOptionSelection(String text, Map<String, Integer> values) {}

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


