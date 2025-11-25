package com.example.hello.mapper;

import com.example.hello.entity.ScaleQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 量表题目Mapper
 */
@Mapper
public interface ScaleQuestionMapper {

    /**
     * 根据量表ID查询题目列表
     */
    List<ScaleQuestion> selectByScaleId(Integer scaleId);
}



