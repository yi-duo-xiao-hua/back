package com.example.hello.mapper;

import com.example.hello.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 医院Mapper接口
 */
@Mapper
public interface HospitalMapper {
    /**
     * 查询所有医院列表
     */
    List<Hospital> selectAllHospitals();
}

