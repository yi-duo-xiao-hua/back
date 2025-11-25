package com.example.hello.mapper;

import com.example.hello.entity.Scale;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 量表Mapper
 */
@Mapper
public interface ScaleMapper {

    /**
     * 查询全部量表
     */
    List<Scale> selectAll();

    /**
     * 根据ID查询量表
     */
    Scale selectById(Integer scaleId);
}



