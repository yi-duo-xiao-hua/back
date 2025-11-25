package com.example.hello.mapper;

import com.example.hello.entity.Assessment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评估记录Mapper接口
 */
@Mapper
public interface AssessmentMapper {
    /**
     * 根据患者ID查询所有评估记录
     */
    List<Assessment> selectByPatientId(Integer patientId);

    /**
     * 根据评估记录ID查询详情
     */
    Assessment selectByAssessmentId(Integer assessmentId);

    /**
     * 新增评估记录
     */
    int insertAssessment(Assessment assessment);
}

