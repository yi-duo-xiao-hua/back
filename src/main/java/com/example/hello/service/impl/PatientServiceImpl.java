package com.example.hello.service.impl;

import com.example.hello.common.Result;
import com.example.hello.common.UserContext;
import com.example.hello.dto.AssessmentDetailVO;
import com.example.hello.dto.PatientInitDTO;
import com.example.hello.dto.PatientProfileVO;
import com.example.hello.dto.PatientUpdateDTO;
import com.example.hello.entity.Assessment;
import com.example.hello.entity.Patient;
import com.example.hello.mapper.AssessmentMapper;
import com.example.hello.mapper.PatientMapper;
import com.example.hello.service.PatientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 患者服务实现类
 */
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private AssessmentMapper assessmentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public Result<PatientProfileVO> getPatientProfile() {
        // 获取当前登录用户ID
        Integer userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 查询患者信息
        Patient patient = patientMapper.selectByUserId(userId);
        if (patient == null) {
            return Result.error("患者信息不存在");
        }

        // 查询评估记录列表
        List<Assessment> assessments = assessmentMapper.selectByPatientId(patient.getPatientId());

        // 构建返回对象
        PatientProfileVO profileVO = new PatientProfileVO();

        // 构建患者基本信息
        PatientProfileVO.PatientInfoVO patientInfo = new PatientProfileVO.PatientInfoVO();
        patientInfo.setPatientId(patient.getPatientId());
        patientInfo.setUserId(patient.getUserId());
        patientInfo.setName(patient.getName());
        patientInfo.setGender(patient.getGender());
        patientInfo.setBirthDate(patient.getBirthDate() != null ? patient.getBirthDate().format(DATE_FORMATTER) : null);
        patientInfo.setIdCard(patient.getIdCard());
        patientInfo.setMedicalHistoryRemark(patient.getMedicalHistoryRemark());
        patientInfo.setCreateTime(patient.getCreateTime() != null ? patient.getCreateTime().format(DATETIME_FORMATTER) : null);
        patientInfo.setUpdateTime(patient.getUpdateTime() != null ? patient.getUpdateTime().format(DATETIME_FORMATTER) : null);
        profileVO.setPatientInfo(patientInfo);

        // 构建评估记录列表
        List<PatientProfileVO.AssessmentItemVO> assessmentList = assessments.stream().map(assessment -> {
            PatientProfileVO.AssessmentItemVO itemVO = new PatientProfileVO.AssessmentItemVO();
            itemVO.setAssessmentId(assessment.getAssessmentId());
            itemVO.setScaleName(assessment.getScaleName());
            itemVO.setTotalScore(assessment.getTotalScore());
            itemVO.setAssessmentResult(assessment.getAssessmentResult());
            itemVO.setAssessmentDate(assessment.getAssessmentDate() != null ? 
                assessment.getAssessmentDate().format(DATETIME_FORMATTER) : null);
            itemVO.setRecommendation(assessment.getRecommendation());
            return itemVO;
        }).collect(Collectors.toList());
        profileVO.setAssessmentList(assessmentList);

        return Result.success(profileVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updatePatient(PatientUpdateDTO patientUpdateDTO) {
        // 检查患者是否存在
        Patient existPatient = patientMapper.selectByPatientId(patientUpdateDTO.getPatientId());
        if (existPatient == null) {
            return Result.error("患者不存在");
        }

        // 性别值校验已在DTO注解中完成

        // 校验身份证号格式（已经在DTO中校验，这里可以再次验证）
        String idCard = patientUpdateDTO.getIdCard();
        if (idCard != null && idCard.length() != 18) {
            return Result.error("身份证号格式错误");
        }

        // 转换日期
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(patientUpdateDTO.getBirthDate(), DATE_FORMATTER);
        } catch (Exception e) {
            return Result.error("出生日期格式错误");
        }

        // 更新患者信息
        Patient patient = new Patient();
        patient.setPatientId(patientUpdateDTO.getPatientId());
        patient.setName(patientUpdateDTO.getName());
        patient.setGender(patientUpdateDTO.getGender());
        patient.setBirthDate(birthDate);
        patient.setIdCard(patientUpdateDTO.getIdCard());
        patient.setMedicalHistoryRemark(patientUpdateDTO.getMedicalHistoryRemark());

        int result = patientMapper.updatePatient(patient);
        if (result > 0) {
            return Result.success("修改成功", null);
        } else {
            return Result.error("修改失败");
        }
    }

    @Override
    public Result<AssessmentDetailVO> getAssessmentDetail(Integer assessmentId) {
        if (assessmentId == null) {
            return Result.error("评估记录ID不能为空");
        }

        Assessment assessment = assessmentMapper.selectByAssessmentId(assessmentId);
        if (assessment == null) {
            return Result.error("评估记录不存在");
        }

        // 构建返回对象
        AssessmentDetailVO detailVO = new AssessmentDetailVO();
        detailVO.setAssessmentId(assessment.getAssessmentId());
        detailVO.setPatientId(assessment.getPatientId());
        detailVO.setScaleName(assessment.getScaleName());
        detailVO.setTotalScore(assessment.getTotalScore());
        detailVO.setAssessmentResult(assessment.getAssessmentResult());
        detailVO.setAssessmentDate(assessment.getAssessmentDate() != null ? 
            assessment.getAssessmentDate().format(DATETIME_FORMATTER) : null);
        detailVO.setRecommendation(assessment.getRecommendation());

        // 解析JSON格式的答题详情
        List<AssessmentDetailVO.AnswerVO> answers = parseAnswers(assessment.getAnswers());
        detailVO.setAnswers(answers);

        return Result.success(detailVO);
    }

    /**
     * 解析JSON格式的答题详情
     */
    private List<AssessmentDetailVO.AnswerVO> parseAnswers(String answersJson) {
        List<AssessmentDetailVO.AnswerVO> answerList = new ArrayList<>();
        
        if (answersJson == null || answersJson.trim().isEmpty()) {
            return answerList;
        }

        try {
            // 尝试将JSON字符串解析为List<Map>
            List<Map<String, Object>> answerMaps = objectMapper.readValue(
                answersJson, 
                new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> answerMap : answerMaps) {
                AssessmentDetailVO.AnswerVO answerVO = new AssessmentDetailVO.AnswerVO();
                
                // 获取题目序号
                if (answerMap.containsKey("questionNumber")) {
                    Object qn = answerMap.get("questionNumber");
                    if (qn instanceof Number) {
                        answerVO.setQuestionNumber(((Number) qn).intValue());
                    }
                }
                
                // 获取题目内容
                if (answerMap.containsKey("questionText")) {
                    answerVO.setQuestionText(String.valueOf(answerMap.get("questionText")));
                }
                
                // 获取选择的分值
                if (answerMap.containsKey("selectedValue")) {
                    Object sv = answerMap.get("selectedValue");
                    if (sv instanceof Number) {
                        answerVO.setSelectedValue(((Number) sv).intValue());
                    }
                }
                
                answerList.add(answerVO);
            }
        } catch (Exception e) {
            // 如果解析失败，返回空列表
            // 实际项目中可以记录日志
        }

        return answerList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Integer>> initPatientInfo(PatientInitDTO patientInitDTO) {
        Integer userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        Patient exist = patientMapper.selectByUserId(userId);
        if (exist != null) {
            return Result.error("该用户已存在患者档案");
        }

        if (patientInitDTO.getGender() == null || (patientInitDTO.getGender() != 1 && patientInitDTO.getGender() != 2)) {
            return Result.error("性别参数错误");
        }

        int idCardCount = patientMapper.countByIdCard(patientInitDTO.getIdCard());
        if (idCardCount > 0) {
            return Result.error("身份证号已被其他患者使用");
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(patientInitDTO.getBirthDate(), DATE_FORMATTER);
        } catch (Exception e) {
            return Result.error("出生日期格式错误");
        }

        Patient patient = new Patient();
        patient.setUserId(userId);
        patient.setName(patientInitDTO.getName());
        patient.setGender(patientInitDTO.getGender());
        patient.setBirthDate(birthDate);
        patient.setIdCard(patientInitDTO.getIdCard());
        patient.setMedicalHistoryRemark(patientInitDTO.getMedicalHistoryRemark());

        patientMapper.insertPatient(patient);

        Map<String, Integer> response = new HashMap<>();
        response.put("patientId", patient.getPatientId());
        return Result.success("患者信息初始化成功", response);
    }
}

